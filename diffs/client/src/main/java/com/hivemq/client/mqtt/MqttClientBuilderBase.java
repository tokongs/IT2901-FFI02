index c9fad1f..c954888 100644
--- a/client/src/main/java/com/hivemq/client/mqtt/MqttClientBuilderBase.java
+++ b/client/src/main/java/com/hivemq/client/mqtt/MqttClientBuilderBase.java
@@ -18,8 +18,6 @@ package com.hivemq.client.mqtt;
 
 import com.hivemq.client.annotations.CheckReturnValue;
 import com.hivemq.client.annotations.DoNotImplement;
-import com.hivemq.client.extensions.PriorityClass;
-import com.hivemq.client.extensions.TopicPriority;
 import com.hivemq.client.mqtt.datatypes.MqttClientIdentifier;
 import com.hivemq.client.mqtt.lifecycle.MqttClientAutoReconnect;
 import com.hivemq.client.mqtt.lifecycle.MqttClientAutoReconnectBuilder;
@@ -41,9 +39,6 @@ import java.net.InetSocketAddress;
 @DoNotImplement
 public interface MqttClientBuilderBase<B extends MqttClientBuilderBase<B>> {
 
-    @CheckReturnValue
-    @NotNull B addTopicPriority(@NotNull TopicPriority topicPriority);
-
     /**
      * Sets the {@link MqttClientConfig#getClientIdentifier() Client Identifier}.
      *
