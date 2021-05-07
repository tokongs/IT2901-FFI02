index acaf9a5..05d525c 100644
--- a/broker/src/main/java/com/hivemq/configuration/entity/HiveMQConfigEntity.java
+++ b/broker/src/main/java/com/hivemq/configuration/entity/HiveMQConfigEntity.java
@@ -35,10 +35,6 @@ public class HiveMQConfigEntity {
     @XmlElementRef(required = false)
     private @NotNull List<ListenerEntity> listeners = new ArrayList<>();
 
-    @XmlElementWrapper(name = "priorities")
-    @XmlElementRef(required = false)
-    private @NotNull List<TopicPriorityEntity> priorities = new ArrayList<>();
-
     @XmlElementRef(required = false)
     private @NotNull MqttConfigEntity mqtt = new MqttConfigEntity();
 
@@ -54,15 +50,10 @@ public class HiveMQConfigEntity {
     @XmlElementRef(required = false)
     private @NotNull PersistenceEntity persistence = new PersistenceEntity();
 
-
     public @NotNull List<ListenerEntity> getListenerConfig() {
         return listeners;
     }
 
-    public @NotNull List<TopicPriorityEntity> getTopicPriorityConfig() {
-        return priorities;
-    }
-
     public @NotNull MqttConfigEntity getMqttConfig() {
         return mqtt;
     }
@@ -82,5 +73,4 @@ public class HiveMQConfigEntity {
     public @NotNull PersistenceEntity getPersistenceConfig() {
         return persistence;
     }
-
 }
