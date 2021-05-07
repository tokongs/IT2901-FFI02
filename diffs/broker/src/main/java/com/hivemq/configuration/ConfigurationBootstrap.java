index d7cd2e6..6de153a 100644
--- a/broker/src/main/java/com/hivemq/configuration/ConfigurationBootstrap.java
+++ b/broker/src/main/java/com/hivemq/configuration/ConfigurationBootstrap.java
@@ -38,9 +38,7 @@ public class ConfigurationBootstrap {
                 new RestrictionsConfigurationServiceImpl(),
                 new SecurityConfigurationServiceImpl(),
                 new UsageStatisticsConfigImpl(),
-                new PersistenceConfigurationServiceImpl(),
-
-                new TopicPriorityConfigurationServiceImpl());
+                new PersistenceConfigurationServiceImpl());
 
         final ConfigurationFile configurationFile = ConfigurationFileProvider.get(systemInformation);
 
@@ -52,9 +50,7 @@ public class ConfigurationBootstrap {
                 new UsageStatisticsConfigurator(configurationService.usageStatisticsConfiguration()),
                 new MqttConfigurator(configurationService.mqttConfiguration()),
                 new ListenerConfigurator(configurationService.listenerConfiguration(), systemInformation),
-                new PersistenceConfigurator(configurationService.persistenceConfigurationService()),
-
-                new TopicPriorirtyConfigurator(configurationService.topicConfiguration()));
+                new PersistenceConfigurator(configurationService.persistenceConfigurationService()));
 
         configFileReader.applyConfig();
 
