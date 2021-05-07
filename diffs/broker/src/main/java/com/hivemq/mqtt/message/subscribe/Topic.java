index d6f0e51..5c11e37 100644
--- a/broker/src/main/java/com/hivemq/mqtt/message/subscribe/Topic.java
+++ b/broker/src/main/java/com/hivemq/mqtt/message/subscribe/Topic.java
@@ -43,9 +43,6 @@ public class Topic implements Serializable, Comparable<Topic>, Mqtt3Topic, Mqtt5
      */
     public static final QoS DEFAULT_QOS = QoS.AT_LEAST_ONCE;
 
-    //Priority
-    private int priority;
-
     //MQTT 3 & 5
     private final @NotNull String topic;
     private @NotNull QoS qoS;
@@ -86,14 +83,6 @@ public class Topic implements Serializable, Comparable<Topic>, Mqtt3Topic, Mqtt5
         this(topic, qoS, noLocal, retainAsPublished, DEFAULT_RETAIN_HANDLING, null);
     }
 
-    //MQTT 5 Topic with priority
-    public Topic(final @NotNull String topic, final @NotNull QoS qoS, final @NotNull int priority,
-                  final boolean noLocal, final boolean retainAsPublished) {
-
-        this(topic, qoS, noLocal, retainAsPublished, DEFAULT_RETAIN_HANDLING, null);
-        this.priority = priority;
-    }
-
     //MQTT 3 Topic
     public Topic(final @NotNull String topic, final @NotNull QoS qoS) {
         this(topic, qoS, DEFAULT_NO_LOCAL, DEFAULT_RETAIN_AS_PUBLISHED, DEFAULT_RETAIN_HANDLING, null);
@@ -212,12 +201,4 @@ public class Topic implements Serializable, Comparable<Topic>, Mqtt3Topic, Mqtt5
         sizeInMemory = size;
         return sizeInMemory;
     }
-
-    /**
-     * testtesttest
-     * @return a integer to test topic config.
-     */
-    public int getPriority() {
-        return priority;
-    }
 }
