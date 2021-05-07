index db57ec6..a3ebc8d 100644
--- a/broker/src/main/java/com/hivemq/configuration/service/FullConfigurationService.java
+++ b/broker/src/main/java/com/hivemq/configuration/service/FullConfigurationService.java
@@ -28,6 +28,4 @@ public interface FullConfigurationService extends ConfigurationService {
 
     PersistenceConfigurationService persistenceConfigurationService();
 
-    TopicPriorityConfigurationService topicConfiguration();
-
 }
