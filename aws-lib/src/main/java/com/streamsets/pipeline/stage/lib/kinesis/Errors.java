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
package com.streamsets.pipeline.stage.lib.kinesis;

import com.streamsets.pipeline.api.ErrorCode;
import com.streamsets.pipeline.api.GenerateResourceBundle;

@GenerateResourceBundle
public enum Errors implements ErrorCode {

  KINESIS_00("Failed to put record: {}"),
  KINESIS_01("Specified stream name is not available. Ensure you've specified the correct AWS Region. Cause: {}"),
  KINESIS_02("Unsupported partition strategy: '{}'"),
  KINESIS_03("Failed to parse incoming Kinesis record w/ sequence number: {}"),
  KINESIS_04("Failed to extract subSequenceNumber from offset: '{}'"),
  KINESIS_05("Failed to serialize record: '{}' - {}"),
  KINESIS_06("Error evaluating the partition expression '{}' for record '{}': {}"),
  KINESIS_07("Error JSON Content - JSON array of objects not supported for Firehose Target")
  ;
  private final String msg;

  Errors(String msg) {
    this.msg = msg;
  }

  @Override
  public String getCode() {
    return name();
  }

  @Override
  public String getMessage() {
    return msg;
  }
}
