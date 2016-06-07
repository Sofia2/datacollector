/**
 * Copyright 2015 StreamSets Inc.
 *
 * Licensed under the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.streamsets.pipeline.stage.origin.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.streamsets.pipeline.api.BatchMaker;
import com.streamsets.pipeline.api.ErrorCode;
import com.streamsets.pipeline.api.Field;
import com.streamsets.pipeline.api.OffsetCommitter;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.base.BaseSource;
import com.streamsets.pipeline.api.impl.Utils;
import com.streamsets.pipeline.config.CsvHeader;
import com.streamsets.pipeline.config.DataFormat;
import com.streamsets.pipeline.lib.executor.SafeScheduledExecutorService;
import com.streamsets.pipeline.lib.parser.DataParser;
import com.streamsets.pipeline.lib.parser.DataParserException;
import com.streamsets.pipeline.lib.parser.DataParserFactory;
public class HttpClientSource extends BaseSource implements OffsetCommitter {
  private static final Logger LOG = LoggerFactory.getLogger(HttpClientSource.class);
  private static final int SLEEP_TIME_WAITING_FOR_BATCH_SIZE_MS = 100;
  private static final String DATA_FORMAT_CONFIG_PREFIX = "conf.dataFormatConfig.";

  public static final String BASIC_CONFIG_PREFIX = "conf.basic.";

  private final HttpClientConfigBean conf;

  private ExecutorService executorService;
  private ScheduledExecutorService safeExecutor;

  private long recordCount;
  private DataParserFactory parserFactory;

  private BlockingQueue<String> entityQueue;
  private HttpStreamConsumer httpConsumer;
  private Scanner sc;
  boolean isNew=true;
  List<String> cabecera;
  /**
   * @param conf Configuration object for the HTTP client
   */
  public HttpClientSource(final HttpClientConfigBean conf) {
    this.conf = conf;
  }
  

  @Override
  protected List<ConfigIssue> init() {
    List<ConfigIssue> issues = super.init();

    conf.basic.init(getContext(), Groups.HTTP.name(), BASIC_CONFIG_PREFIX, issues);
    conf.dataFormatConfig.init(
        getContext(),
        conf.dataFormat,
        Groups.HTTP.name(),
        DATA_FORMAT_CONFIG_PREFIX,
        issues
    );

    // Queue may not be empty at shutdown, but because we can't rewind,
    // the dropped entities are not recoverable anyway. In the case
    // that the pipeline is restarted we'll resume with any entities still enqueued.
    entityQueue = new ArrayBlockingQueue<>(2 * conf.basic.maxBatchSize);

    parserFactory = conf.dataFormatConfig.getParserFactory();

    httpConsumer = new HttpStreamConsumer(conf, entityQueue);

    switch (conf.httpMode) {
      case STREAMING:
        createStreamingConsumer();
        break;
      case POLLING:
        createPollingConsumer();
        break;
      default:
        throw new IllegalStateException("Unrecognized httpMode " + conf.httpMode);
    }
    return issues;
  }

  private void createPollingConsumer() {
    safeExecutor = new SafeScheduledExecutorService(1, getClass().getName());
    LOG.info("Scheduling consumer at polling period {}.", conf.pollingInterval);
    safeExecutor.scheduleAtFixedRate(httpConsumer, 0L, conf.pollingInterval, TimeUnit.MILLISECONDS);
  }

  private void createStreamingConsumer() {
    executorService = Executors.newFixedThreadPool(1);
    executorService.execute(httpConsumer);
  }

  @Override
  public void destroy() {
    if (httpConsumer != null) {
      httpConsumer.stop();
      httpConsumer = null;
      if (conf.httpMode == HttpClientMode.STREAMING) {
        executorService.shutdownNow();
      } else if (conf.httpMode == HttpClientMode.POLLING) {
        safeExecutor.shutdownNow();
      }
    }
    super.destroy();
  }

  @Override
  public String produce(String lastSourceOffset, int maxBatchSize, BatchMaker batchMaker) throws StageException {
    long start = System.currentTimeMillis();
    int chunksToFetch = Math.min(conf.basic.maxBatchSize, maxBatchSize);
    while (((System.currentTimeMillis() - start) < conf.basic.maxWaitTime) && (entityQueue.size() < chunksToFetch)) {
      try {
        Thread.sleep(SLEEP_TIME_WAITING_FOR_BATCH_SIZE_MS);
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    }
    
    // Check for an error and propagate to the user
    if (httpConsumer.getError().isPresent()) {
      Exception e = httpConsumer.getError().get();
      throw new StageException(Errors.HTTP_03, e.getMessage(), e);
    }

    List<String> chunks = new ArrayList<>(chunksToFetch);

    // We didn't receive any new data within the time allotted for this batch.
    if (entityQueue.isEmpty()) {
      return getOffset();
    }
    entityQueue.drainTo(chunks, chunksToFetch);

    Response.StatusType lastResponseStatus = httpConsumer.getLastResponseStatus();
    
    if (lastResponseStatus.getFamily() == Response.Status.Family.SUCCESSFUL) {
    	
			if (conf.dataFormat == DataFormat.DELIMITED) {
				parseChunk(chunks, batchMaker, maxBatchSize, lastSourceOffset);
				return getOffset();
			}
			for (String chunk : chunks) {
				String sourceId = getOffset();
				try (DataParser parser = parserFactory.getParser(sourceId,
						chunk.getBytes(StandardCharsets.UTF_8))) {
					parseChunk(parser, batchMaker);
				} catch (IOException | DataParserException e) {
					handleError(Errors.HTTP_00, sourceId, e.toString(), e);
				}
			}
		} else {
			// If http response status != 2xx
			handleError(Errors.HTTP_01, lastResponseStatus.getStatusCode(),
					lastResponseStatus.getReasonPhrase());
		}

		return getOffset();
	}

	private void getScanner(String chunk) throws Exception {
		sc = new Scanner(chunk);
	}
  
	private int setNextSourceOffset(String lastSourceOffset){
		if (lastSourceOffset==null){
			return 0;
		}
		return Integer.parseInt(lastSourceOffset);
	}
	
	private void firstScan(String pattern){
		
		if (cabecera == null) {
			cabecera = new LinkedList<>();
		}
		
		if (conf.dataFormatConfig.csvHeader.name().equalsIgnoreCase(CsvHeader.WITH_HEADER.name())) {
			if (sc.hasNextLine()) {
				String[] datos = sc.nextLine().split(pattern);
				if (datos.length > 0) {
					while (datos[0].isEmpty() && sc.hasNextLine()) {
						datos = sc.nextLine().split(String.valueOf(conf.dataFormatConfig.csvCustomDelimiter));
					}
				}
				for (String temp : datos) {
					cabecera.add(temp);
				}
				recordCount++;
			}
		} else if (conf.dataFormatConfig.csvHeader.getLabel().equalsIgnoreCase(CsvHeader.IGNORE_HEADER.name())) {
			if (sc.hasNextLine()) {
				String datos = sc.nextLine();
				while (datos.isEmpty() && sc.hasNextLine()) {
					datos = sc.nextLine();
				}
			}
			recordCount++;
		}
	}
  
	private void parseChunk(List<String> chunks, BatchMaker batchMaker,int maxBatchSize, String lastSourceOffset) {

		String pattern = String.valueOf(conf.dataFormatConfig.csvCustomDelimiter);
		Map<String, Field> map = new HashMap<String, Field>();
		int nextSourceOffset=setNextSourceOffset(lastSourceOffset);

		if (nextSourceOffset==maxBatchSize && getContext().isPreview()){
			return;
		}
		
			try {
				for (int j=0;j<chunks.size();j++) {
					
					Record record = getContext().createRecord("temporal");// context.createRecord(hdfsInputPath+nextSourceOffset);
					String chunk=chunks.get(j);
					getScanner(chunk);
					
					if (j==0 && cabecera==null){
						firstScan(pattern);
					}
	
					for (int contador = 0, i = 1; sc.hasNextLine(); recordCount++, contador = 0, i++) {
						map = new LinkedHashMap<String, Field>();
						String[] listadoSplit = sc.nextLine().split(pattern);
						
						if (!cabecera.isEmpty()) {
							try{
								for (String temp : listadoSplit) {
									map.put(cabecera.get(contador), Field.create(temp));
									contador++;
								}
								while (cabecera.size() > contador + 1) {
									map.put(cabecera.get(contador + 1),Field.create(""));
									contador++;
								}
								record.set(Field.createListMap((LinkedHashMap<String, Field>) map));
								batchMaker.addRecord(record);
							} catch(Exception e){
								getContext().toError(getContext().createRecord("error"), Errors.HTTP_00, e.toString());
							}
						} else {
							for (String temp : listadoSplit) {
								map.put(String.valueOf(contador),Field.create(temp));
								contador++;
							}
							record.set(Field.createListMap((LinkedHashMap<String, Field>) map));
							batchMaker.addRecord(record);
						}
	
						if (i>=maxBatchSize && getContext().isPreview()){
							try{
								return;
							}catch (Exception e){}
						}
					}
				}
			} catch (Throwable e) {
				getContext().toError(getContext().createRecord("error"), Errors.HTTP_00, e.toString());
			} finally{
				isNew=false;
				if (sc!=null){
					sc.close();
				}
			}
	}
  private void parseChunk(DataParser parser, BatchMaker batchMaker) throws IOException, DataParserException {
	    if (conf.dataFormat == DataFormat.JSON) {
	      // For JSON, a chunk only contains a single record, so we only parse it once.
	      Record record = parser.parse();
	      if (record != null) {
	        batchMaker.addRecord(record);
	        recordCount++;
	      }
	      if (null != parser.parse()) {
	        throw new DataParserException(Errors.HTTP_02);
	      }
	    } else {
	      // For text and xml, a chunk may contain multiple records.
	      Record record = parser.parse();
	      while (record != null) {
	        batchMaker.addRecord(record);
	        recordCount++;
	        record = parser.parse();
	      }
	    }
	  }

  private void handleError(ErrorCode errorCode, Object... context) throws StageException {
    switch (getContext().getOnErrorRecord()) {
      case DISCARD:
        break;
      case TO_ERROR:
        getContext().reportError(errorCode, context);
        break;
      case STOP_PIPELINE:
        throw new StageException(errorCode, context);
      default:
        throw new IllegalStateException(
            Utils.format("Unknown OnError value '{}'", getContext().getOnErrorRecord())
        );
    }
  }

  private String getOffset() {
    return Long.toString(recordCount);
  }

  @Override
  public void commit(String offset) throws StageException {
    // NO-OP
  }
}
