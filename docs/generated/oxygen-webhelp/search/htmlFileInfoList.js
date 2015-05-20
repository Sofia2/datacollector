fil = new Array();
fil["0"]= "Administration/Administration_title.html@@@Administration@@@...";
fil["1"]= "Administration/RESTResponse.html@@@REST Response@@@You can view REST response JSON data for different aspects of the Data Collector, such as pipeline configuration information or monitoring details...";
fil["2"]= "Administration/ShuttingDown.html@@@Shutting Down the Data Collector@@@Shut down and restart the Data Collector to apply changes to the Data Collector configuration file, environment configuration file, or user logins. In Data Collector console, click Administration &gt...";
fil["3"]= "Administration/ViewingDCConfigs.html@@@Viewing Data Collector Configuration Properties@@@To view Data Collector configuration properties, click Administration &gt; Configuration . For details about the configuration properties or to edit the configuration file, see Configuring the Dat...";
fil["4"]= "Administration/ViewingDirectories.html@@@Viewing Data Collector Directories@@@You can view the directories that the Data Collector uses. You might check the directories being used to access a file in the directory or to increase the amount of available space for a directory...";
fil["5"]= "Administration/ViewingJVMMetrics.html@@@Viewing JVM Metrics@@@The Data Collector provides JVM metrics for the Data Collector...";
fil["6"]= "Administration/ViewingLogData.html@@@Viewing Data Collector Logs@@@You can view and download log data. When you download log data, you can select the file to download. To view log data for the Data Collector , click Administration &gt; Logs . The console displays...";
fil["7"]= "Advanced_PipelineConfig/AdvancedPipelineConfig-title.html@@@Advanced Configuration@@@...";
fil["8"]= "Advanced_PipelineConfig/ExecutionMode.html@@@Execution Mode@@@The Data Collector can run standalone mode or cluster mode. In standalone mode, the Data Collector runs as a standalone product. The Data Collector runs in standalone mode by default. In cluster mode...";
fil["9"]= "Advanced_PipelineConfig/RuntimeProperties.html@@@Runtime Properties@@@Runtime properties are properties that you define in a file local to the Data Collector and call from within a pipeline. With runtime properties, you can define different sets of values for different Data Collectors...";
fil["10"]= "Alerts/Alerts_title.html@@@Alerts@@@...";
fil["11"]= "Alerts/DataAlerts.html@@@Data Rules and Alerts@@@Data rules define the information that you want to see about the data that passes between stages. You can create data rules based on any link in the pipeline. You can also create alerts based on the data rules...";
fil["12"]= "Alerts/EmailforAlerts-Configuring.html@@@Configuring Email for Alerts@@@You can define the email addresses to receive metric and data alerts. When an alert triggers an email, the Data Collector sends an email to every address in the list...";
fil["13"]= "Alerts/MetricAlerts.html@@@Metric Alerts@@@Create a metric alert to receive monitoring or email alerts based on metric rules. You can configure metric alerts when you configure or monitor a pipeline. You can edit or delete alerts when they are not active...";
fil["14"]= "Apx-GrokPatterns/DateTimePatterns.html@@@Date and Time Grok Patterns@@@You can use the following date and time grok patterns to define the structure of log data: MONTH...";
fil["15"]= "Apx-GrokPatterns/GeneralPatterns.html@@@General Grok Patterns@@@You can use the following general grok patterns to define the structure of log data: USER %{USERNAME} USERNAME [a-zA-Z0-9._-]+ BASE10NUM (?&lt;![0-9.+-])(?&gt;[+-]?(?:(?:[0-9]+(?:\\.[0-9]+)?)|(?:\\.[0-9...";
fil["16"]= "Apx-GrokPatterns/GrokPatterns.html@@@Grok Patterns@@@You can use the grok patterns in this appendix to define the structure of log data. You can use a single pattern or several patterns to define a larger pattern. You can also use valid sections of patterns to define a custom pattern...";
fil["17"]= "Apx-GrokPatterns/GrokPatterns_title.html@@@Grok Patterns@@@...";
fil["18"]= "Apx-GrokPatterns/JavaPatterns.html@@@Java Grok Patterns@@@You can use the following Java-related grok patterns to define the structure of log data: JAVACLASS (?:[a-zA-Z$_][a-zA-Z$_0-9]*\\.)*[a-zA-Z$_][a-zA-Z$_0-9]* JAVAFILE (?:[A-Za-z0-9_. -]+) A space...";
fil["19"]= "Apx-GrokPatterns/LogPatterns.html@@@Log Grok Patterns@@@You can use the following log-related grok patterns to define the structure of log data: SYSLOGTIMESTAMP %{MONTH} +%{MONTHDAY} %{TIME} PROG (?:[\\w._/%-]+) SYSLOGPROG...";
fil["20"]= "Apx-GrokPatterns/NetworkingPatterns.html@@@Networking Grok Patterns@@@You can use the following networking-related grok patterns to define the structure of log data: MAC (?:%{CISCOMAC}|%{WINDOWSMAC}|%{COMMONMAC}) CISCOMAC (?:(?:[A-Fa-f0-9]{4}\\.){2}[A-Fa-f0-9]{4...";
fil["21"]= "Apx-GrokPatterns/PathPatterns.html@@@Path Grok Patterns@@@You can use the following path grok patterns to define the structure of log data: PATH (?:%{UNIXPATH}|%{WINPATH}) UNIXPATH (?&gt;/(?&gt;[\\w_%!$@:.,~-]+|\\\\.)*)+ TTY...";
fil["22"]= "Data_Preview/DataCollectorWindow-Preview.html@@@Data Collector Console - Preview Mode@@@When you preview data, the Preview panel uses different colors to represent different types of data. The Preview panel also uses colors, italics, and asterisks to highlight the field values and records that you have changed for testing...";
fil["23"]= "Data_Preview/DataPreview.html@@@Data Preview@@@You can edit preview data to view how a stage or group of stages processes the changed data. Edit preview data to test for data conditions that might not appear in data preview...";
fil["24"]= "Data_Preview/DataPreview_Title.html@@@Data Preview@@@...";
fil["25"]= "Data_Preview/PreviewingMultipleStages.html@@@Previewing Multiple Stages@@@You can preview data for a group of linked stages within a pipeline. You can also edit the preview data to see how the group processes your changes. Preview multiple stages to verify that the group processes data as expected...";
fil["26"]= "Data_Preview/PreviewingaSingleStage.html@@@Previewing a Single Stage@@@You can preview data for a single stage. In the Preview panel, you can drill down to review the values for each record to determine if the stage transforms data as expected. You can also edit the...";
fil["27"]= "Destinations/Cassandra.html@@@Cassandra@@@The Cassandra destination writes data to a Cassandra cluster...";
fil["28"]= "Destinations/Destinations-title.html@@@Destinations@@@...";
fil["29"]= "Destinations/Destinations_overview.html@@@Destinations@@@A destination stage represents the target for a pipeline. You can use one or more destinations in a pipeline...";
fil["30"]= "Destinations/Elasticsearch.html@@@Elasticsearch@@@The Elasticsearch destination writes data to an Elasticsearch cluster. The Elasticsearch destination writes each record to Elasticsearch as a document...";
fil["31"]= "Destinations/Flume.html@@@Flume@@@The Flume destination writes data to Flume. When you write data to Flume, you pass data to a Flume client. The Flume client passes data to Flume hosts based on Flume client configuration properties...";
fil["32"]= "Destinations/HBase.html@@@HBase@@@You can add custom HBase configuration properties to the HBase destination...";
fil["33"]= "Destinations/HadoopFS.html@@@Hadoop FS@@@You can add custom Hadoop configuration properties to the Hadoop FS destination...";
fil["34"]= "Destinations/KProducer.html@@@Kafka Producer@@@You can add custom Kafka configuration properties to the Kafka Producer...";
fil["35"]= "Destinations/KinProducer.html@@@Kinesis Producer@@@The Kinesis Producer destination writes data to an Amazon Kinesis cluster. You can write JSON objects or SDC Record data to Kinesis. Kinesis Producer writes JSON data to Kinesis as multiple objects. It does not write JSON arrays...";
fil["36"]= "Destinations/Solr.html@@@Solr@@@The index mode determines how the Solr destination indexes records when uploading to Solr. Index mode also determines how the destination handles errors...";
fil["37"]= "Destinations/ToError.html@@@To Error@@@The To Error destination passes records to the pipeline for error handling. Use the To Error destination to send a stream of records to pipeline error handling...";
fil["38"]= "Destinations/Trash.html@@@Trash@@@The Trash destination discards records. Use the Trash destination as a visual representation of records discarded from the pipeline. Or, you might use the Trash destination during development as a temporary placeholder...";
fil["39"]= "Expression_Language/ExpressionLanguage_overview.html@@@Expression Language@@@Stage paths indicate the path that a record took through the pipeline...";
fil["40"]= "Expression_Language/ExpressionLanguage_title.html@@@Expression Language@@@...";
fil["41"]= "Expression_Language/Functions.html@@@Functions@@@Use string functions to transform string dat...";
fil["42"]= "Expression_Language/Literals.html@@@Literals@@@The expression language includes the following literals...";
fil["43"]= "Expression_Language/MiscFunctions.html@@@Miscellaneous Functions@@@The expression language includes the following miscellaneous functions: emptyList Creates an empty list. emptyMap Creates an empty map. runtime:conf( &lt;runtime configuration name&gt; ) Returns the value...";
fil["44"]= "Expression_Language/Operators.html@@@Operators@@@The precedence of operators highest to lowest, left to right is as follows...";
fil["45"]= "Expression_Language/ReservedWords.html@@@Reserved Words@@@The following words are reserved for the expression language and should not be used as identifiers...";
fil["46"]= "Getting_Started/DCollector_Window.html@@@Data Collector Console@@@The Data Collector provides a console to configure pipelines, preview data, monitor pipelines, and view snapshots of dat...";
fil["47"]= "Getting_Started/GettingStarted_Title.html@@@Getting Started@@@...";
fil["48"]= "Getting_Started/LoggingIn_CreatingPipeline.html@@@Logging In and Creating a Pipeline@@@After you start the Data Collector, you can log in to the Data Collector console and create your first pipeline...";
fil["49"]= "Getting_Started/What_isa_DataCollector.html@@@What is a Data Collector?@@@Let s walk through it...";
fil["50"]= "Glossary/GlossaryOfTerms.html@@@Glossary of Terms@@@data alerts Alerts based on rules that gather information about the data that passes between two stages. destination A stage type used in a pipeline to represent where the Data Collector writes...";
fil["51"]= "Glossary/Glossary_title.html@@@Glossary@@@...";
fil["52"]= "Install_Config/DCConfig.html@@@Data Collector Configuration@@@You can edit the Data Collector configuration file, sdc.properties, to configure properties such as the host name and port number, the HTTP protocol, and account information for email alerts...";
fil["53"]= "Install_Config/DCEnvironmentConfig.html@@@Data Collector Environment Configuration@@@You can edit the Data Collector environment configuration file, sdc-env.sh, to configure Java options...";
fil["54"]= "Install_Config/Install_Config_title.html@@@Installation and Configuration@@@...";
fil["55"]= "Install_Config/InstallationAndConfig.html@@@Installation and Configuration@@@You can install the Data Collector to be started manually or to run as a service...";
fil["56"]= "Install_Config/InstallingAdditionalStages.html@@@Installing Additional Stages@@@You can install a new stage by untarring the stage library and copying files...";
fil["57"]= "Install_Config/MultipleInstances.html@@@Multiple Data Collector Instances@@@You can create multiple Data Collector instances. Each instance uses the same executables, but can be configured differently and run different pipelines...";
fil["58"]= "Install_Config/UsersandRoles.html@@@Users and Roles@@@The Data Collector provides several roles and a default user account based on each role. For increased security, update the passwords for these accounts or create additional user accounts. A production deployment of the Data Collector should integrate with intranet authentication such as LDAP...";
fil["59"]= "Origins/Directory.html@@@Directory@@@Configure a Directory origin to read data from files in a directory...";
fil["60"]= "Origins/FileTail.html@@@File Tail@@@When you use an origin to read log data, you define the format of the log files to be read...";
fil["61"]= "Origins/HTTPClient.html@@@HTTP Client@@@The HTTP Client origin reads JSON data from a streaming HTTP resource URL...";
fil["62"]= "Origins/JDBCConsumer.html@@@JDBC Consumer@@@The SQL query defines the data returned from the database...";
fil["63"]= "Origins/KConsumer.html@@@Kafka Consumer@@@Configure a Kafka Consumer to read data from a Kafka cluster...";
fil["64"]= "Origins/KinConsumer.html@@@Kinesis Consumer@@@You can configure the read interval for the Kinesis Consumer. The read interval determines how long Kinesis Consumer waits before requesting additional data from Kinesis shards. By default, the Kinesis Consumer waits 1000 milliseconds - one second - between requests...";
fil["65"]= "Origins/Omniture.html@@@Omniture@@@The Omniture origin processes JSON website usage reports generated by the Omniture reporting APIs. Omniture is also known as the Adobe Marketing Cloud...";
fil["66"]= "Origins/Origins_overview.html@@@Origins@@@You can preview raw source data when you configure an origin. Preview raw source data when reviewing the data might help with origin configuration...";
fil["67"]= "Origins/Origins_title.html@@@Origins@@@...";
fil["68"]= "Origins/UDP.html@@@UDP Source@@@Configure a UDP Source origin to process NetFlow messages from a UPD source...";
fil["69"]= "Pipeline_Configuration/ConfiguringAPipeline.html@@@Configuring a Pipeline@@@Configure a pipeline to define the stream of data. After you configure the pipeline, you can start the pipeline...";
fil["70"]= "Pipeline_Configuration/DataCollectorWindow-Config.html@@@Data Collector Console - Edit Mode@@@The following image shows the Data Collector console when you configure a pipeline: Area / Icon Name Description 1 Pipeline canvas Displays the pipeline. Use to configure the pipeline data flow. 2...";
fil["71"]= "Pipeline_Configuration/ErrorHandling.html@@@Error Record Handling@@@You can configure error record handling at a stage level and at a pipeline level...";
fil["72"]= "Pipeline_Configuration/Expressions.html@@@Expressions@@@Use the following information and tips when you invoke expression completion...";
fil["73"]= "Pipeline_Configuration/PipelineConfiguration_title.html@@@Pipeline Configuration@@@...";
fil["74"]= "Pipeline_Configuration/PipelineDesign.html@@@Pipeline Design@@@You can configure required fields for any processor and most destination stages. A required field must exist in a record to allow the record to enter the stage for processing. When a record does not include a required field, the record is passed to the pipeline for error handling...";
fil["75"]= "Pipeline_Configuration/PipelineProperties.html@@@Pipeline Properties@@@A pipeline constant is a constant that you define for the pipeline and that you can use in any stage in the pipeline. Define a pipeline constant when you have a constant that you want to update easily or to use more than once...";
fil["76"]= "Pipeline_Configuration/Validation.html@@@Validation@@@The Data Collector provides two types of validation...";
fil["77"]= "Pipeline_Configuration/What_isa_Pipeline.html@@@What is a Pipeline?@@@A pipeline describes the flow of data for the Data Collector...";
fil["78"]= "Pipeline_Maintenance/DeletingAPipeline.html@@@Deleting a Pipeline@@@You can delete a pipeline when you no longer need it. Deleting a pipeline is permanent. If you think you might want to archive a pipeline, you might export the pipeline before you delete it. In the...";
fil["79"]= "Pipeline_Maintenance/DuplicatingAPipeline.html@@@Duplicating a Pipeline@@@Duplicate a pipeline when you want to keep the existing version of a pipeline while continuing to configure a duplicate version. A duplicate is an exact copy of the original pipeline...";
fil["80"]= "Pipeline_Maintenance/ExportingAPipeline.html@@@Exporting a Pipeline@@@Export a pipeline when you want to use the pipeline with another Data Collector. You might also export pipelines to keep a backup...";
fil["81"]= "Pipeline_Maintenance/ImportingAPipeline.html@@@Importing a Pipeline@@@Import a pipeline to use a pipeline developed on a different Data Collector or to restore a backup file. Import a pipeline from a pipeline file. Pipeline files are JSON files exported from another Data Collector...";
fil["82"]= "Pipeline_Maintenance/PipelineMaintenance_title.html@@@Pipeline Maintenance@@@...";
fil["83"]= "Pipeline_Maintenance/StartingAPipeline.html@@@Starting a Pipeline@@@You can start a pipeline when it is valid. When you start a pipeline, the Data Collector runs the pipeline until you stop the pipeline or shut down the Data Collector...";
fil["84"]= "Pipeline_Maintenance/StoppingAPipeline.html@@@Stopping a Pipeline@@@Stop a pipeline when you want the Data Collector to stop processing data for the pipeline...";
fil["85"]= "Pipeline_Maintenance/ViewingtheRunHistory.html@@@Viewing the Run History@@@You can view a run summary for each run of the pipeline when you view the pipeline history...";
fil["86"]= "Pipeline_Monitoring/DataCollectorWindow-Monitor.html@@@Data Collector Console - Monitor Mode@@@In Monitor mode, you can use the Data Collector console to view data as it passes through the pipeline...";
fil["87"]= "Pipeline_Monitoring/MonitoringErrors.html@@@Monitoring Errors@@@You can view the errors related to each stage. Stage-related errors include the error records that the stage produces and other errors encountered by the stage...";
fil["88"]= "Pipeline_Monitoring/PipelineMonitoring.html@@@Pipeline Monitoring@@@When the Data Collector runs a pipeline, you can view real-time statistics about the pipeline, examine a sample of the data being processed, and create rules and alerts...";
fil["89"]= "Pipeline_Monitoring/PipelineMonitoring_title.html@@@Pipeline Monitoring@@@...";
fil["90"]= "Pipeline_Monitoring/Snapshots.html@@@Snapshots@@@A snapshot is a set of data captured as it moves through a running pipeline. The Data Collector can save up to ten snapshots. You can capture snapshots when you monitor a pipeline...";
fil["91"]= "Pipeline_Monitoring/ViewingPipelineStageStatistics.html@@@Viewing Pipeline and Stage Statistics@@@When you monitor a pipeline, you can view real-time summary and error statistics for the pipeline and for stages in the pipeline...";
fil["92"]= "Processors/Expression.html@@@Expression Evaluator@@@When you configure an expression, the Expression Evaluator writes the results of the expression to a field. You can use an existing field or create a new field...";
fil["93"]= "Processors/FieldConverter.html@@@Field Converter@@@Configure a Field Converter to convert the data type of a field...";
fil["94"]= "Processors/FieldHasher.html@@@Field Hasher@@@Configure a Field Hasher to encode sensitive dat...";
fil["95"]= "Processors/FieldMasker.html@@@Field Masker@@@Configure a Field Masker to mask sensitive dat...";
fil["96"]= "Processors/FieldRemover.html@@@Field Remover@@@Configure a Field Remover to remove unwanted fields from the pipeline...";
fil["97"]= "Processors/FieldSplitter.html@@@Field Splitter@@@The Field Splitter splits string data based on a separator character and passes the separated data to new fields. Use the Field Splitter to split complex string values into logical components...";
fil["98"]= "Processors/JSONParser.html@@@JSON Parser@@@Configure a JSON Parser to parse a JSON object in a String field...";
fil["99"]= "Processors/JavaScript.html@@@JavaScript Evaluator@@@You can choose the processing mode to use with the JavaScript Evaluator. You can use the same script in each processing mode. However, you should include error handling in the script before you run in batch mode...";
fil["100"]= "Processors/Jython.html@@@Jython Evaluator@@@You can choose the processing mode to use with the Jython Evaluator. You can use the same script in each processing mode. However, you should include error handling in the script before you run in batch mode...";
fil["101"]= "Processors/Processors_overview.html@@@Processors@@@At various points in a pipeline, you can use preview data to help select fields. Preview data displays when a pipeline is configured to a point where you can to perform a data preview...";
fil["102"]= "Processors/Processors_title.html@@@Processors@@@...";
fil["103"]= "Processors/RDeduplicator.html@@@Record Deduplicator@@@Use a Record Deduplicator to route or remove records with duplicate dat...";
fil["104"]= "Processors/StreamSelector.html@@@Stream Selector@@@A condition defines the data that passes to the associated stream. All records that meet the condition passes to the stream. Use the expression language to define conditions...";
fil["105"]= "Processors/ValueReplacer.html@@@Value Replacer@@@The Value Replacer replaces null values in a field with a constant. The Value Replacer can also replace existing values in a field with Nulls...";
fil["106"]= "Troubleshooting/Troubleshooting.html@@@Troubleshooting@@@Use the following tips to help you with the Data Collector. If you have suggestions for additional items, feel free to send them to docs@streamsets.com...";
fil["107"]= "Troubleshooting/Troubleshooting_title.html@@@Troubleshooting@@@...";
