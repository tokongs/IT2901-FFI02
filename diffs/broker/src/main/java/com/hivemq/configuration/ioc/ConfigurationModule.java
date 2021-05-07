index 34ab40e..95eea12 100644
--- a/broker/src/main/java/com/hivemq/configuration/ioc/ConfigurationModule.java
+++ b/broker/src/main/java/com/hivemq/configuration/ioc/ConfigurationModule.java
@@ -59,8 +59,6 @@ public class ConfigurationModule extends SingletonModule {
         bind(UsageStatisticsConfig.class).toInstance(configurationService.usageStatisticsConfiguration());
 
         bind(SecurityConfigurationService.class).toInstance(configurationService.securityConfiguration());
-
-        bind(TopicPriorityConfigurationService.class).toInstance(configurationService.topicConfiguration());
     }
 
 }
