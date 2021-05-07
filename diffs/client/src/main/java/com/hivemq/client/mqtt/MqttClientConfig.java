index 75779c9..f7b8c58 100644
--- a/client/src/main/java/com/hivemq/client/mqtt/MqttClientConfig.java
+++ b/client/src/main/java/com/hivemq/client/mqtt/MqttClientConfig.java
@@ -18,8 +18,6 @@ package com.hivemq.client.mqtt;
 
 import com.hivemq.client.annotations.DoNotImplement;
 import com.hivemq.client.annotations.Immutable;
-import com.hivemq.client.extensions.TopicPriority;
-import com.hivemq.client.internal.util.collections.ImmutableList;
 import com.hivemq.client.mqtt.datatypes.MqttClientIdentifier;
 import com.hivemq.client.mqtt.lifecycle.MqttClientAutoReconnect;
 import com.hivemq.client.mqtt.lifecycle.MqttClientConnectedListener;
@@ -39,8 +37,6 @@ import java.util.Optional;
 @DoNotImplement
 public interface MqttClientConfig {
 
-    @NotNull ImmutableList<TopicPriority> getTopicPriorities();
-
     /**
      * @return the MQTT version of the client.
      */
@@ -54,7 +50,6 @@ public interface MqttClientConfig {
      */
     @NotNull Optional<MqttClientIdentifier> getClientIdentifier();
 
-
     /**
      * @return the server address the client connects to.
      * @since 1.1
