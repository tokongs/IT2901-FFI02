index 2b90bdc..7793cda 100644
--- a/broker/src/main/java/com/hivemq/configuration/reader/ConfigFileReader.java
+++ b/broker/src/main/java/com/hivemq/configuration/reader/ConfigFileReader.java
@@ -53,8 +53,6 @@ public class ConfigFileReader {
     private final @NotNull UsageStatisticsConfigurator usageStatisticsConfigurator;
     private final @NotNull PersistenceConfigurator persistenceConfigurator;
 
-    private final @NotNull TopicPriorirtyConfigurator topicPriorirtyConfigurator;
-
     public ConfigFileReader(
             @NotNull final ConfigurationFile configurationFile,
             @NotNull final RestrictionConfigurator restrictionConfigurator,
@@ -63,9 +61,7 @@ public class ConfigFileReader {
             @NotNull final UsageStatisticsConfigurator usageStatisticsConfigurator,
             @NotNull final MqttConfigurator mqttConfigurator,
             @NotNull final ListenerConfigurator listenerConfigurator,
-            @NotNull final PersistenceConfigurator persistenceConfigurator,
-
-            @NotNull final TopicPriorirtyConfigurator topicPriorirtyConfigurator) {
+            @NotNull final PersistenceConfigurator persistenceConfigurator) {
 
         this.configurationFile = configurationFile;
         this.envVarUtil = envVarUtil;
@@ -75,8 +71,6 @@ public class ConfigFileReader {
         this.securityConfigurator = securityConfigurator;
         this.usageStatisticsConfigurator = usageStatisticsConfigurator;
         this.persistenceConfigurator = persistenceConfigurator;
-
-        this.topicPriorirtyConfigurator = topicPriorirtyConfigurator;
     }
 
     public void applyConfig() {
@@ -145,13 +139,6 @@ public class ConfigFileReader {
         securityConfigurator.setSecurityConfig(config.getSecurityConfig());
         usageStatisticsConfigurator.setUsageStatisticsConfig(config.getUsageStatisticsConfig());
         persistenceConfigurator.setPersistenceConfig(config.getPersistenceConfig());
-
-        /**
-         * Topic configurator
-         *
-         * Added by Christoffer Stensrud
-         */
-        topicPriorirtyConfigurator.setTopicPriorityConfig(config.getTopicPriorityConfig());
     }
 
 }
