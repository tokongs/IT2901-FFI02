index 0031051..9f11e0a 100644
--- a/broker/src/test/java/util/TestConfigurationBootstrap.java
+++ b/broker/src/test/java/util/TestConfigurationBootstrap.java
@@ -15,7 +15,10 @@
  */
 package util;
 
-import com.hivemq.configuration.service.*;
+import com.hivemq.configuration.service.ConfigurationService;
+import com.hivemq.configuration.service.FullConfigurationService;
+import com.hivemq.configuration.service.PersistenceConfigurationService;
+import com.hivemq.configuration.service.SecurityConfigurationService;
 import com.hivemq.configuration.service.impl.*;
 import com.hivemq.configuration.service.impl.listener.ListenerConfigurationServiceImpl;
 import com.hivemq.statistics.UsageStatisticsConfig;
@@ -34,8 +37,6 @@ public class TestConfigurationBootstrap {
     private final UsageStatisticsConfig usageStatisticsConfig;
     private final PersistenceConfigurationService persistenceConfigurationService;
 
-    private final TopicPriorityConfigurationServiceImpl topicConfigurationService;
-
     public TestConfigurationBootstrap() {
         listenerConfigurationService = new ListenerConfigurationServiceImpl();
         mqttConfigurationService = new MqttConfigurationServiceImpl();
@@ -44,17 +45,13 @@ public class TestConfigurationBootstrap {
         usageStatisticsConfig = new UsageStatisticsConfigImpl();
         persistenceConfigurationService = new PersistenceConfigurationServiceImpl();
 
-        topicConfigurationService = new TopicPriorityConfigurationServiceImpl();
-
         configurationService = new ConfigurationServiceImpl(
                 listenerConfigurationService,
                 mqttConfigurationService,
                 restrictionsConfigurationService,
                 securityConfigurationService,
                 usageStatisticsConfig,
-                persistenceConfigurationService,
-
-                topicConfigurationService);
+                persistenceConfigurationService);
     }
 
     public SecurityConfigurationService getSecurityConfigurationService() {
@@ -100,8 +97,4 @@ public class TestConfigurationBootstrap {
     public PersistenceConfigurationService getPersistenceConfigurationService() {
         return persistenceConfigurationService;
     }
-
-    public TopicPriorityConfigurationServiceImpl getTopicConfigurationService() {
-        return topicConfigurationService;
-    }
 }
