/**
 * Copyright 2016 StreamSets Inc.
 *
 * Licensed under the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.stage.destination.hive;

import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ConfigDefBean;
import com.streamsets.pipeline.api.ValueChooserModel;
import com.streamsets.pipeline.config.DataFormat;

public class HMSTargetConfigBean {

  @ConfigDefBean
  public HiveConfigBean hiveConfigBean;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.MODEL,
      label = "Data Format",
      displayPosition = 20,
      group = "HIVE"
  )
  @ValueChooserModel(HMSDataFormatChooserValues.class)
  public DataFormat dataFormat = DataFormat.AVRO;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.BOOLEAN,
      label = "Use as Avro",
      description = "Specifies Whether the table properties should not contain the schema url",
      defaultValue = "true",
      dependsOn = "dataFormat",
      triggeredByValue = "AVRO",
      displayPosition = 30,
      group = "ADVANCED"
  )
  public boolean useAsAvro = true;

  //Same as in HDFS origin.
  @ConfigDef(
      required = false,
      type = ConfigDef.Type.STRING,
      label = "HDFS User",
      description = "If set, the data collector will serialize avro " +
          "schemas in HDFS with specified hdfs user. The data collector" +
          " user must be configured as a proxy user in HDFS.",
      displayPosition = 40,
      group = "ADVANCED",
      dependsOn = "useAsAvro",
      triggeredByValue = "false"
  )
  public String hdfsUser;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.BOOLEAN,
      label = "Kerberos Authentication",
      defaultValue = "false",
      description = "",
      displayPosition = 50,
      group = "ADVANCED",
      dependsOn = "useAsAvro",
      triggeredByValue = "false"
  )
  public boolean hdfsKerberos;
}