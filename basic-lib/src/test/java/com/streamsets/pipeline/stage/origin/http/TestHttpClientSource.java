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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.streamsets.pipeline.api.OnRecordError;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.config.DataFormat;
import com.streamsets.pipeline.config.JsonMode;
import com.streamsets.pipeline.sdk.SourceRunner;
import com.streamsets.pipeline.sdk.StageRunner;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Currently tests do not include basic auth because of lack of support in JerseyTest
 * so we trust that the Jersey client we use implements auth correctly.
 */
public class TestHttpClientSource extends JerseyTest {

  @Path("/stream")
  @Produces("application/json")
  public static class StreamResource {
    @GET
    public Response getStream() {
      return Response.ok(
          "{\"name\": \"adam\"}\r\n" +
          "{\"name\": \"joe\"}\r\n" +
          "{\"name\": \"sally\"}"
      ).build();
    }

    @POST
    public Response postStream(String name) {
      Map<String, String> map = ImmutableMap.of("adam", "adam", "joe", "joe", "sally", "sally");
      String queriedName = map.get(name);
      return Response.ok(
          "{\"name\": \"" + queriedName + "\"}\r\n"
      ).build();
    }
  }

  @Path("/nlstream")
  @Produces("application/json")
  public static class NewlineStreamResource {
    @GET
    public Response getStream() {
      return Response.ok(
          "{\"name\": \"adam\"}\n" +
          "{\"name\": \"joe\"}\n" +
          "{\"name\": \"sally\"}"
      ).build();
    }
  }

  @Path("/xmlstream")
  @Produces("application/xml")
  public static class XmlStreamResource {
    @GET
    public Response getStream() {
      return Response.ok(
          "<root>" +
          "<record>" +
          "<name>adam</name>" +
          "</record>" +
          "<record>" +
          "<name>joe</name>" +
          "</record>" +
          "<record>" +
          "<name>sally</name>" +
          "</record>" +
          "</root>"
      ).build();
    }
  }

  @Path("/textstream")
  @Produces("application/text")
  public static class TextStreamResource {
    @GET
    public Response getStream() {
      return Response.ok(
          "adam\r\n" +
          "joe\r\n" +
          "sally"
      ).build();
    }
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(
        Sets.newHashSet(
            StreamResource.class,
            NewlineStreamResource.class,
            TextStreamResource.class,
            XmlStreamResource.class
        )
    );
  }

  @Override
  protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
    return new GrizzlyWebTestContainerFactory();
  }

  @Override
  protected DeploymentContext configureDeployment() {
    return ServletDeploymentContext.forServlet(
        new ServletContainer(
            new ResourceConfig(
                Sets.newHashSet(
                    StreamResource.class,
                    NewlineStreamResource.class,
                    TextStreamResource.class,
                    XmlStreamResource.class
                )
            )
        )
    ).build();
  }

  @Test
  public void testStreamingHttp() throws Exception {
    HttpClientConfigBean conf = new HttpClientConfigBean();
    conf.authType = AuthenticationType.NONE;
    conf.httpMode = HttpClientMode.STREAMING;
    conf.resourceUrl = "http://localhost:9998/stream";
    conf.requestTimeoutMillis = 1000;
    conf.entityDelimiter = "\r\n";
    conf.basic.maxBatchSize = 100;
    conf.basic.maxWaitTime = 1000;
    conf.pollingInterval = 1000;
    conf.httpMethod = HttpMethod.GET;
    conf.dataFormat = DataFormat.JSON;
    conf.dataFormatConfig.jsonContent = JsonMode.MULTIPLE_OBJECTS;

    HttpClientSource origin = new HttpClientSource(conf);

    SourceRunner runner = new SourceRunner.Builder(HttpClientSource.class, origin)
        .addOutputLane("lane")
        .build();
    runner.runInit();

    try {
      StageRunner.Output output = runner.runProduce(null, 1000);
      Map<String, List<Record>> recordMap = output.getRecords();
      List<Record> parsedRecords = recordMap.get("lane");

      assertEquals(3, parsedRecords.size());

      String[] names = { "adam", "joe", "sally" };

      for (int i = 0; i < parsedRecords.size(); i++) {
        assertTrue(parsedRecords.get(i).has("/name"));
        assertEquals(names[i], extractValueFromRecord(parsedRecords.get(i), DataFormat.JSON));
      }
    } finally {
      runner.runDestroy();
    }
  }

  @Test
  public void testStreamingPost() throws Exception {
    HttpClientConfigBean conf = new HttpClientConfigBean();
    conf.authType = AuthenticationType.NONE;
    conf.httpMode = HttpClientMode.STREAMING;
    conf.resourceUrl = "http://localhost:9998/stream";
    conf.requestTimeoutMillis = 1000;
    conf.entityDelimiter = "\r\n";
    conf.basic.maxBatchSize = 100;
    conf.basic.maxWaitTime = 1000;
    conf.pollingInterval = 1000;
    conf.httpMethod = HttpMethod.POST;
    conf.requestData = "adam";
    conf.dataFormat = DataFormat.JSON;
    conf.dataFormatConfig.jsonContent = JsonMode.MULTIPLE_OBJECTS;

    HttpClientSource origin = new HttpClientSource(conf);

    SourceRunner runner = new SourceRunner.Builder(HttpClientSource.class, origin)
        .addOutputLane("lane")
        .build();
    runner.runInit();

    try {
      StageRunner.Output output = runner.runProduce(null, 1000);
      Map<String, List<Record>> recordMap = output.getRecords();
      List<Record> parsedRecords = recordMap.get("lane");

      assertEquals(1, parsedRecords.size());

      String[] names = { "adam" };

      for (int i = 0; i < parsedRecords.size(); i++) {
        assertTrue(parsedRecords.get(i).has("/name"));
        assertEquals(names[i], extractValueFromRecord(parsedRecords.get(i), DataFormat.JSON));
      }
    } finally {
      runner.runDestroy();
    }

  }

  @Test
  public void testStreamingHttpWithNewlineOnly() throws Exception {
    HttpClientConfigBean conf = new HttpClientConfigBean();
    conf.authType = AuthenticationType.NONE;
    conf.httpMode = HttpClientMode.STREAMING;
    conf.resourceUrl = "http://localhost:9998/nlstream";
    conf.requestTimeoutMillis = 1000;
    conf.entityDelimiter = "\n";
    conf.basic.maxBatchSize = 100;
    conf.basic.maxWaitTime = 1000;
    conf.pollingInterval = 1000;
    conf.httpMethod = HttpMethod.GET;
    conf.dataFormat = DataFormat.JSON;
    conf.dataFormatConfig.jsonContent = JsonMode.MULTIPLE_OBJECTS;

    HttpClientSource origin = new HttpClientSource(conf);

    SourceRunner runner = new SourceRunner.Builder(HttpClientSource.class, origin)
        .addOutputLane("lane")
        .build();
    runner.runInit();

    try {
      StageRunner.Output output = runner.runProduce(null, 1000);
      Map<String, List<Record>> recordMap = output.getRecords();
      List<Record> parsedRecords = recordMap.get("lane");

      assertEquals(3, parsedRecords.size());

      String[] names = { "adam", "joe", "sally" };

      for (int i = 0; i < parsedRecords.size(); i++) {
        assertTrue(parsedRecords.get(i).has("/name"));
        assertEquals(names[i], extractValueFromRecord(parsedRecords.get(i), DataFormat.JSON));
      }
    } finally {
      runner.runDestroy();
    }

  }

  @Test
  public void testStreamingHttpWithXml() throws Exception {
    HttpClientConfigBean conf = new HttpClientConfigBean();
    conf.authType = AuthenticationType.NONE;
    conf.httpMode = HttpClientMode.STREAMING;
    conf.resourceUrl = "http://localhost:9998/xmlstream";
    conf.requestTimeoutMillis = 1000;
    conf.entityDelimiter = "\r\n";
    conf.basic.maxBatchSize = 100;
    conf.basic.maxWaitTime = 1000;
    conf.pollingInterval = 1000;
    conf.httpMethod = HttpMethod.GET;
    conf.dataFormat = DataFormat.XML;
    conf.dataFormatConfig.xmlRecordElement = "record";
    HttpClientSource origin = new HttpClientSource(conf);

    SourceRunner runner = new SourceRunner.Builder(HttpClientSource.class, origin)
        .addOutputLane("lane")
        .build();
    runner.runInit();

    try {
      StageRunner.Output output = runner.runProduce(null, 1000);
      Map<String, List<Record>> recordMap = output.getRecords();
      List<Record> parsedRecords = recordMap.get("lane");

      assertEquals(3, parsedRecords.size());

      String[] names = { "adam", "joe", "sally" };

      for (int i = 0; i < parsedRecords.size(); i++) {
        assertTrue(parsedRecords.get(i).has("/name"));
        assertEquals(names[i], extractValueFromRecord(parsedRecords.get(i), DataFormat.XML));
      }
    } finally {
      runner.runDestroy();
    }
  }

  @Test
  public void testStreamingHttpWithText() throws Exception {
    HttpClientConfigBean conf = new HttpClientConfigBean();
    conf.authType = AuthenticationType.NONE;
    conf.httpMode = HttpClientMode.STREAMING;
    conf.resourceUrl = "http://localhost:9998/textstream";
    conf.requestTimeoutMillis = 1000;
    conf.entityDelimiter = "\r\n";
    conf.basic.maxBatchSize = 100;
    conf.basic.maxWaitTime = 1000;
    conf.pollingInterval = 1000;
    conf.httpMethod = HttpMethod.GET;
    conf.dataFormat = DataFormat.TEXT;
    HttpClientSource origin = new HttpClientSource(conf);

    SourceRunner runner = new SourceRunner.Builder(HttpClientSource.class, origin)
        .addOutputLane("lane")
        .build();
    runner.runInit();

    try {
      StageRunner.Output output = runner.runProduce(null, 1000);
      Map<String, List<Record>> recordMap = output.getRecords();
      List<Record> parsedRecords = recordMap.get("lane");

      assertEquals(3, parsedRecords.size());

      String[] names = { "adam", "joe", "sally" };

      for (int i = 0; i < parsedRecords.size(); i++) {
        assertEquals(names[i], extractValueFromRecord(parsedRecords.get(i), DataFormat.TEXT));
      }
    } finally {
      runner.runDestroy();
    }
  }

  @Test
  public void testNoAuthorizeHttpOnSendToError() throws Exception {
    HttpClientSource origin = getTwitterHttpClientSource();
    SourceRunner runner = new SourceRunner.Builder(HttpClientSource.class, origin)
      .addOutputLane("lane")
      .setOnRecordError(OnRecordError.TO_ERROR)
      .build();
    runner.runInit();

    try {
      runner.runProduce(null, 1000);
      List<String> errors = runner.getErrors();
      assertEquals(1, errors.size());
    } finally {
      runner.runDestroy();
    }
  }

  @Test
  public void testNoAuthorizeHttpOnStopPipeline() throws Exception {
    HttpClientSource origin = getTwitterHttpClientSource();
    SourceRunner runner = new SourceRunner.Builder(HttpClientSource.class, origin)
      .addOutputLane("lane")
      .setOnRecordError(OnRecordError.STOP_PIPELINE)
      .build();
    runner.runInit();

    boolean exceptionThrown = false;

    try {
      runner.runProduce(null, 1000);
      List<String> errors = runner.getErrors();
      assertEquals(1, errors.size());
    } catch (StageException ex){
      exceptionThrown = true;
      assertEquals(ex.getErrorCode(), Errors.HTTP_01);
    }
    finally {
      runner.runDestroy();
    }

    assertTrue(exceptionThrown);
  }

  @Test
  public void testNoAuthorizeHttpOnDiscard() throws Exception {
    HttpClientSource origin = getTwitterHttpClientSource();

    SourceRunner runner = new SourceRunner.Builder(HttpClientSource.class, origin)
      .addOutputLane("lane")
      .setOnRecordError(OnRecordError.DISCARD)
      .build();
    runner.runInit();

    try {
      runner.runProduce(null, 1000);
      List<String> errors = runner.getErrors();
      assertEquals(0, errors.size());
    } finally {
      runner.runDestroy();
    }
  }

  private String extractValueFromRecord(Record r, DataFormat f) {
    String v = null;
    if (f == DataFormat.JSON) {
      v = r.get("/name").getValueAsString();
    } else if (f == DataFormat.TEXT) {
      v = r.get().getValueAsMap().get("text").getValueAsString();
    } else if (f == DataFormat.XML) {
      v = r.get().getValueAsMap().get("name").getValueAsList().get(0).getValueAsMap().get("value").getValueAsString();
    }
    return v;
  }

  private HttpClientSource getTwitterHttpClientSource() {
    HttpClientConfigBean conf = new HttpClientConfigBean();
    conf.authType = AuthenticationType.NONE;
    conf.httpMode = HttpClientMode.STREAMING;
    conf.resourceUrl = "https://stream.twitter.com/1.1/statuses/sample.json";
    conf.requestTimeoutMillis = 1000;
    conf.entityDelimiter = "\r\n";
    conf.basic.maxBatchSize = 100;
    conf.basic.maxWaitTime = 1000;
    conf.pollingInterval = 1000;
    conf.httpMethod = HttpMethod.GET;
    conf.dataFormat = DataFormat.JSON;
    conf.dataFormatConfig.jsonContent = JsonMode.MULTIPLE_OBJECTS;
    conf.useProxy = false;

    return new HttpClientSource(conf);
  }
}
