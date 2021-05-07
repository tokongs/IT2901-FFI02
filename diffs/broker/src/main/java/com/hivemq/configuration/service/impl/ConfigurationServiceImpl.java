index 7b9c711..741f658 100644
--- a/broker/src/main/java/com/hivemq/configuration/service/impl/ConfigurationServiceImpl.java
+++ b/broker/src/main/java/com/hivemq/configuration/service/impl/ConfigurationServiceImpl.java
@@ -34,26 +34,19 @@ public class ConfigurationServiceImpl implements FullConfigurationService {
     private final UsageStatisticsConfig usageStatisticsConfig;
     private final PersistenceConfigurationService persistenceConfigurationService;
 
-    private final TopicPriorityConfigurationService topicPriorityConfigurationService;
-
     public ConfigurationServiceImpl(
             final ListenerConfigurationService listenerConfigurationService,
             final MqttConfigurationService mqttConfigurationService,
             final RestrictionsConfigurationService restrictionsConfigurationService,
             final SecurityConfigurationService securityConfigurationService,
             final UsageStatisticsConfig usageStatisticsConfig,
-            final PersistenceConfigurationService persistenceConfigurationService,
-
-            final TopicPriorityConfigurationServiceImpl topicConfigurationService) {
-
+            final PersistenceConfigurationService persistenceConfigurationService) {
         this.listenerConfigurationService = listenerConfigurationService;
         this.mqttConfigurationService = mqttConfigurationService;
         this.restrictionsConfigurationService = restrictionsConfigurationService;
         this.securityConfigurationService = securityConfigurationService;
         this.usageStatisticsConfig = usageStatisticsConfig;
         this.persistenceConfigurationService = persistenceConfigurationService;
-
-        this.topicPriorityConfigurationService = topicConfigurationService;
     }
 
     @Override
@@ -85,9 +78,4 @@ public class ConfigurationServiceImpl implements FullConfigurationService {
     public PersistenceConfigurationService persistenceConfigurationService() {
         return persistenceConfigurationService;
     }
-
-    @Override
-    public TopicPriorityConfigurationService topicConfiguration() {
-        return topicPriorityConfigurationService;
-    }
 }
