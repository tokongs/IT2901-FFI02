index 98772c8..9b74a42 100644
--- a/broker/src/test/java/com/hivemq/configuration/reader/AbstractConfigurationTest.java
+++ b/broker/src/test/java/com/hivemq/configuration/reader/AbstractConfigurationTest.java
@@ -17,8 +17,14 @@ package com.hivemq.configuration.reader;
 
 import com.hivemq.configuration.info.SystemInformation;
 import com.hivemq.configuration.info.SystemInformationImpl;
-import com.hivemq.configuration.service.*;
-import com.hivemq.configuration.service.impl.*;
+import com.hivemq.configuration.service.MqttConfigurationService;
+import com.hivemq.configuration.service.PersistenceConfigurationService;
+import com.hivemq.configuration.service.RestrictionsConfigurationService;
+import com.hivemq.configuration.service.SecurityConfigurationService;
+import com.hivemq.configuration.service.impl.MqttConfigurationServiceImpl;
+import com.hivemq.configuration.service.impl.PersistenceConfigurationServiceImpl;
+import com.hivemq.configuration.service.impl.RestrictionsConfigurationServiceImpl;
+import com.hivemq.configuration.service.impl.SecurityConfigurationServiceImpl;
 import com.hivemq.configuration.service.impl.listener.ListenerConfigurationService;
 import com.hivemq.configuration.service.impl.listener.ListenerConfigurationServiceImpl;
 import com.hivemq.statistics.UsageStatisticsConfig;
@@ -52,7 +58,6 @@ public class AbstractConfigurationTest {
     UsageStatisticsConfig usageStatisticsConfig;
     SystemInformation systemInformation;
     PersistenceConfigurationService persistenceConfigurationService;
-    TopicPriorityConfigurationService topicPriorityConfigurationService;
 
     @Before
     public void setUp() throws Exception {
@@ -66,11 +71,9 @@ public class AbstractConfigurationTest {
         usageStatisticsConfig = new UsageStatisticsConfigImpl();
         systemInformation = new SystemInformationImpl(false);
         persistenceConfigurationService = new PersistenceConfigurationServiceImpl();
-        topicPriorityConfigurationService = new TopicPriorityConfigurationServiceImpl();
 
         when(envVarUtil.replaceEnvironmentVariablePlaceholders(anyString())).thenCallRealMethod();
         final ConfigurationFile configurationFile = new ConfigurationFile(xmlFile);
-
         reader = new ConfigFileReader(
                 configurationFile,
                 new RestrictionConfigurator(restrictionsConfigurationService),
@@ -79,8 +82,7 @@ public class AbstractConfigurationTest {
                 new UsageStatisticsConfigurator(usageStatisticsConfig),
                 new MqttConfigurator(mqttConfigurationService),
                 new ListenerConfigurator(listenerConfigurationService, systemInformation),
-                new PersistenceConfigurator(persistenceConfigurationService),
-                new TopicPriorirtyConfigurator(topicPriorityConfigurationService));
+                new PersistenceConfigurator(persistenceConfigurationService));
     }
 
 }
