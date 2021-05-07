index b90c9f4..1002e15 100644
--- a/client/src/main/java/com/hivemq/client/internal/mqtt/mqtt3/Mqtt3ClientConfigView.java
+++ b/client/src/main/java/com/hivemq/client/internal/mqtt/mqtt3/Mqtt3ClientConfigView.java
@@ -17,13 +17,11 @@
 package com.hivemq.client.internal.mqtt.mqtt3;
 
 import com.hivemq.client.annotations.Immutable;
-import com.hivemq.client.extensions.TopicPriority;
 import com.hivemq.client.internal.mqtt.MqttClientConfig;
 import com.hivemq.client.internal.mqtt.message.auth.MqttSimpleAuth;
 import com.hivemq.client.internal.mqtt.message.auth.mqtt3.Mqtt3SimpleAuthView;
 import com.hivemq.client.internal.mqtt.message.publish.MqttWillPublish;
 import com.hivemq.client.internal.mqtt.message.publish.mqtt3.Mqtt3PublishView;
-import com.hivemq.client.internal.util.collections.ImmutableList;
 import com.hivemq.client.mqtt.MqttClientExecutorConfig;
 import com.hivemq.client.mqtt.MqttClientState;
 import com.hivemq.client.mqtt.MqttClientTransportConfig;
@@ -53,11 +51,6 @@ public class Mqtt3ClientConfigView implements Mqtt3ClientConfig {
         this.delegate = delegate;
     }
 
-    @Override
-    public @NotNull ImmutableList<TopicPriority> getTopicPriorities() {
-        return delegate.getTopicPriorities();
-    }
-
     @Override
     public @NotNull MqttVersion getMqttVersion() {
         return delegate.getMqttVersion();
@@ -122,5 +115,4 @@ public class Mqtt3ClientConfigView implements Mqtt3ClientConfig {
     public @NotNull Optional<Mqtt3ClientConnectionConfig> getConnectionConfig() {
         return Optional.ofNullable(delegate.getRawConnectionConfig());
     }
-
 }
