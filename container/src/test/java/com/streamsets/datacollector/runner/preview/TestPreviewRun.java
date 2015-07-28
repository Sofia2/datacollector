/**
 * (c) 2014 StreamSets, Inc. All rights reserved. May not
 * be copied, modified, or distributed in whole or part without
 * written consent of StreamSets, Inc.
 */
package com.streamsets.datacollector.runner.preview;

import com.streamsets.datacollector.config.PipelineConfiguration;
import com.streamsets.datacollector.main.RuntimeInfo;
import com.streamsets.datacollector.record.RecordImpl;
import com.streamsets.datacollector.runner.MockStages;
import com.streamsets.datacollector.runner.Pipeline;
import com.streamsets.datacollector.runner.PipelineRunner;
import com.streamsets.datacollector.runner.SourceOffsetTracker;
import com.streamsets.datacollector.runner.StageOutput;
import com.streamsets.datacollector.runner.preview.PreviewPipeline;
import com.streamsets.datacollector.runner.preview.PreviewPipelineBuilder;
import com.streamsets.datacollector.runner.preview.PreviewPipelineOutput;
import com.streamsets.datacollector.runner.preview.PreviewPipelineRunner;
import com.streamsets.datacollector.util.ContainerError;
import com.streamsets.pipeline.api.Batch;
import com.streamsets.pipeline.api.BatchMaker;
import com.streamsets.pipeline.api.Field;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.Source;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.base.BaseSource;
import com.streamsets.pipeline.api.base.BaseTarget;
import com.streamsets.pipeline.api.base.SingleLaneRecordProcessor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;


public class TestPreviewRun {
  private RuntimeInfo runtimeInfo;

  @Before
  public void setUp() {
    MockStages.resetStageCaptures();
    runtimeInfo = Mockito.mock(RuntimeInfo.class);
  }

  @Test
  public void testPreviewRun() throws Exception {
    MockStages.setSourceCapture(new BaseSource() {
      @Override
      public String produce(String lastSourceOffset, int maxBatchSize, BatchMaker batchMaker) throws StageException {
        Record record = getContext().createRecord("x");
        record.set(Field.create(1));
        batchMaker.addRecord(record);
        return "1";
      }
    });
    MockStages.setProcessorCapture(new SingleLaneRecordProcessor() {
      @Override
      protected void process(Record record, SingleLaneBatchMaker batchMaker) throws StageException {
        record.set(Field.create(2));
        batchMaker.addRecord(record);
      }
    });
    MockStages.setTargetCapture(new BaseTarget() {
      @Override
      public void write(Batch batch) throws StageException {
      }
    });
    SourceOffsetTracker tracker = Mockito.mock(SourceOffsetTracker.class);
    PipelineRunner runner = new PreviewPipelineRunner( "name", "0", runtimeInfo, tracker, -1, 1, true);
    Pipeline pipeline = new Pipeline.Builder(MockStages.createStageLibrary(), "name", "name", "0",
                                             MockStages.createPipelineConfigurationSourceProcessorTarget()).build(runner);
    pipeline.init();
    pipeline.run();
    pipeline.destroy();
    List<StageOutput> output = runner.getBatchesOutput().get(0);
    Assert.assertEquals(1, output.get(0).getOutput().get("s").get(0).get().getValue());
    Assert.assertEquals(2, output.get(1).getOutput().get("p").get(0).get().getValue());
  }

  @Test
  public void testPreviewPipelineBuilder() throws Exception {
    MockStages.setSourceCapture(new BaseSource() {
      @Override
      public String produce(String lastSourceOffset, int maxBatchSize, BatchMaker batchMaker) throws StageException {
        Record record = getContext().createRecord("x");
        record.set(Field.create(1));
        batchMaker.addRecord(record);
        return "1";
      }
    });
    MockStages.setProcessorCapture(new SingleLaneRecordProcessor() {
      @Override
      protected void process(Record record, SingleLaneBatchMaker batchMaker) throws StageException {
        record.set(Field.create(2));
        batchMaker.addRecord(record);
      }
    });
    SourceOffsetTracker tracker = Mockito.mock(SourceOffsetTracker.class);
    PreviewPipelineRunner runner = new PreviewPipelineRunner( "name", "0", runtimeInfo, tracker, -1, 1, true);
    PipelineConfiguration pipelineConfiguration = MockStages.createPipelineConfigurationSourceProcessorTarget();
    pipelineConfiguration.getStages().remove(2);

    PreviewPipeline pipeline = new PreviewPipelineBuilder(MockStages.createStageLibrary(), "name", "0",
      pipelineConfiguration, null).build(runner);
    PreviewPipelineOutput previewOutput = pipeline.run();
    List<StageOutput> output = previewOutput.getBatchesOutput().get(0);
    Assert.assertEquals(2, output.size());
    Assert.assertEquals(1, output.get(0).getOutput().get("s").get(0).get().getValue());
    Assert.assertEquals(2, output.get(1).getOutput().get("p").get(0).get().getValue());
  }


  @Test
  public void testPreviewPipelineBuilderWithLastStage() throws Exception {
    MockStages.setSourceCapture(new BaseSource() {
      @Override
      public String produce(String lastSourceOffset, int maxBatchSize, BatchMaker batchMaker) throws StageException {
        Record record = getContext().createRecord("x");
        record.set(Field.create(1));
        batchMaker.addRecord(record);
        return "1";
      }
    });
    MockStages.setProcessorCapture(new SingleLaneRecordProcessor() {
      @Override
      protected void process(Record record, SingleLaneBatchMaker batchMaker) throws StageException {
        record.set(Field.create(2));
        //batchMaker.addRecord(record);
      }
    });

    SourceOffsetTracker tracker = Mockito.mock(SourceOffsetTracker.class);
    PreviewPipelineRunner runner = new PreviewPipelineRunner( "name", "0",runtimeInfo, tracker, -1, 1, true);
    PipelineConfiguration pipelineConfiguration = MockStages.createPipelineConfigurationSourceProcessorTarget();

    PreviewPipeline pipeline = new PreviewPipelineBuilder(MockStages.createStageLibrary(), "name", "0",
      pipelineConfiguration, "p").build(runner);

    PreviewPipelineOutput previewOutput = pipeline.run();
    List<StageOutput> output = previewOutput.getBatchesOutput().get(0);

    Assert.assertEquals(1, output.size());


    //Complex graph
    runner = new PreviewPipelineRunner("name", "0", runtimeInfo, tracker, -1, 1, true);
    pipelineConfiguration = MockStages.createPipelineConfigurationComplexSourceProcessorTarget();
    pipeline = new PreviewPipelineBuilder(MockStages.createStageLibrary(), "name",  "0",
      pipelineConfiguration, "p1").build(runner);
    previewOutput = pipeline.run();
    output = previewOutput.getBatchesOutput().get(0);
    Assert.assertEquals(1, output.size());
    Assert.assertEquals(1, output.get(0).getOutput().get("s").get(0).get().getValue());


    runner = new PreviewPipelineRunner("name", "0", runtimeInfo, tracker, -1, 1, true);
    pipelineConfiguration = MockStages.createPipelineConfigurationComplexSourceProcessorTarget();
    pipeline = new PreviewPipelineBuilder(MockStages.createStageLibrary(), "name", "0",
      pipelineConfiguration, "p5").build(runner);
    previewOutput = pipeline.run();
    output = previewOutput.getBatchesOutput().get(0);
    Assert.assertEquals(2, output.size());
    Assert.assertEquals(1, output.get(0).getOutput().get("s").get(0).get().getValue());


    runner = new PreviewPipelineRunner("name", "0", runtimeInfo, tracker, -1, 1, true);
    pipelineConfiguration = MockStages.createPipelineConfigurationComplexSourceProcessorTarget();
    pipeline = new PreviewPipelineBuilder(MockStages.createStageLibrary(), "name1", "0",
      pipelineConfiguration, "p6").build(runner);
    previewOutput = pipeline.run();
    output = previewOutput.getBatchesOutput().get(0);
    Assert.assertEquals(3, output.size());

    runner = new PreviewPipelineRunner("name", "0", runtimeInfo, tracker, -1, 1, true);
    pipelineConfiguration = MockStages.createPipelineConfigurationComplexSourceProcessorTarget();
    pipeline = new PreviewPipelineBuilder(MockStages.createStageLibrary(), "name1", "0",
      pipelineConfiguration, "t").build(runner);
    previewOutput = pipeline.run();
    output = previewOutput.getBatchesOutput().get(0);
    Assert.assertEquals(7, output.size());

  }


  @Test
  public void testIsPreview() throws Exception {
    MockStages.setSourceCapture(new BaseSource() {

      @Override
      protected List<ConfigIssue> init() {
        List<ConfigIssue> issues = super.init();
        Assert.assertTrue(getContext().isPreview());
        return issues;
      }

      @Override
      public String produce(String lastSourceOffset, int maxBatchSize, BatchMaker batchMaker) throws StageException {
        return "X";
      }
    });

    SourceOffsetTracker tracker = Mockito.mock(SourceOffsetTracker.class);
    PreviewPipelineRunner runner = new PreviewPipelineRunner("name", "0", runtimeInfo, tracker, -1, 1, true);
    PipelineConfiguration pipelineConfiguration = MockStages.createPipelineConfigurationSourceProcessorTarget();
    pipelineConfiguration.getStages().remove(2);

    PreviewPipeline pipeline = new PreviewPipelineBuilder(MockStages.createStageLibrary(), "name", "0",
                                                          pipelineConfiguration, null).build(runner);
    PreviewPipelineOutput previewOutput = pipeline.run();
    Mockito.verify(tracker).setOffset(Mockito.eq("X"));
  }

  @Test
  public void testPreviewRunFailValidationConfigs() throws Exception {

    MockStages.setSourceCapture(new BaseSource() {
      @Override
      public List<ConfigIssue> init(Info info, Source.Context context) {
        return Arrays.asList(context.createConfigIssue(null, null, ContainerError.CONTAINER_0000));
      }

      @Override
      public String produce(String lastSourceOffset, int maxBatchSize, BatchMaker batchMaker) throws StageException {
        return "1";
      }
    });
    MockStages.setProcessorCapture(new SingleLaneRecordProcessor() {
      @Override
      protected void process(Record record, SingleLaneBatchMaker batchMaker) throws StageException {
        record.set(Field.create(2));
        batchMaker.addRecord(record);
      }
    });
    MockStages.setTargetCapture(new BaseTarget() {
      @Override
      public void write(Batch batch) throws StageException {
      }
    });
    SourceOffsetTracker tracker = Mockito.mock(SourceOffsetTracker.class);
    PipelineRunner runner = new PreviewPipelineRunner("name", "0", runtimeInfo, tracker, -1, 1, true);
    PreviewPipeline pp = new PreviewPipelineBuilder(MockStages.createStageLibrary(), "name", "0",
                                                    MockStages.createPipelineConfigurationSourceProcessorTarget(), null).build(runner);
    Assert.assertFalse(pp.validateConfigs().isEmpty());
  }


  @Test
  public void testPreviewRunOverride() throws Exception {
    MockStages.setSourceCapture(new BaseSource() {
      @Override
      public String produce(String lastSourceOffset, int maxBatchSize, BatchMaker batchMaker) throws StageException {
        Record record = getContext().createRecord("x");
        record.set(Field.create(1));
        batchMaker.addRecord(record);
        return "1";
      }
    });
    MockStages.setProcessorCapture(new SingleLaneRecordProcessor() {
      @Override
      protected void process(Record record, SingleLaneBatchMaker batchMaker) throws StageException {
        int currentValue = record.get().getValueAsInteger();
        record.set(Field.create(currentValue * 2));
        batchMaker.addRecord(record);
      }
    });
    MockStages.setTargetCapture(new BaseTarget() {
      @Override
      public void write(Batch batch) throws StageException {
      }
    });
    PipelineConfiguration pipelineConf = MockStages.createPipelineConfigurationSourceProcessorTarget();
    SourceOffsetTracker tracker = Mockito.mock(SourceOffsetTracker.class);
    PipelineRunner runner = new PreviewPipelineRunner("name", "0", runtimeInfo, tracker, -1, 1, true);
    Pipeline pipeline = new Pipeline.Builder(MockStages.createStageLibrary(), "name", "name", "0", pipelineConf).build(runner);
    pipeline.init();
    pipeline.run();
    pipeline.destroy();
    List<StageOutput> output = runner.getBatchesOutput().get(0);
    Assert.assertEquals(1, output.get(0).getOutput().get("s").get(0).get().getValue());
    Assert.assertEquals(2, output.get(1).getOutput().get("p").get(0).get().getValue());

    StageOutput sourceOutput = output.get(0);
    Assert.assertEquals("s", sourceOutput.getInstanceName());

    Record modRecord = new RecordImpl("i", "source", null, null);
    modRecord.set(Field.create(10));
    //modifying the source output
    sourceOutput.getOutput().get(pipelineConf.getStages().get(0).getOutputLanes().get(0)).set(0, modRecord);

    runner = new PreviewPipelineRunner("name", "0",runtimeInfo, tracker, -1, 1, true);
    pipeline = new Pipeline.Builder(MockStages.createStageLibrary(), "name", "name", "0",
                                    MockStages.createPipelineConfigurationSourceProcessorTarget()).build(runner);

    pipeline.init();
    pipeline.run(Arrays.asList(sourceOutput));
    pipeline.destroy();
    output = runner.getBatchesOutput().get(0);
    Assert.assertEquals(10, output.get(0).getOutput().get("s").get(0).get().getValue());
    Assert.assertEquals(20, output.get(1).getOutput().get("p").get(0).get().getValue());
  }

}