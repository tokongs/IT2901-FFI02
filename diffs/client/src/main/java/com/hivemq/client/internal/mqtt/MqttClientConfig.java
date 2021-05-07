index 453578a..63e6630 100644
--- a/client/src/main/java/com/hivemq/client/internal/mqtt/MqttClientConfig.java
+++ b/client/src/main/java/com/hivemq/client/internal/mqtt/MqttClientConfig.java
@@ -16,8 +16,6 @@
 
 package com.hivemq.client.internal.mqtt;
 
-import com.hivemq.client.extensions.PriorityClass;
-import com.hivemq.client.extensions.TopicPriority;
 import com.hivemq.client.internal.mqtt.advanced.MqttClientAdvancedConfig;
 import com.hivemq.client.internal.mqtt.datatypes.MqttClientIdentifierImpl;
 import com.hivemq.client.internal.mqtt.ioc.ClientComponent;
@@ -30,7 +28,6 @@ import com.hivemq.client.internal.util.collections.ImmutableList;
 import com.hivemq.client.mqtt.MqttClientState;
 import com.hivemq.client.mqtt.MqttVersion;
 import com.hivemq.client.mqtt.datatypes.MqttClientIdentifier;
-import com.hivemq.client.mqtt.datatypes.MqttTopicFilter;
 import com.hivemq.client.mqtt.lifecycle.MqttClientAutoReconnect;
 import com.hivemq.client.mqtt.lifecycle.MqttClientConnectedListener;
 import com.hivemq.client.mqtt.lifecycle.MqttClientDisconnectedListener;
@@ -60,13 +57,12 @@ public class MqttClientConfig implements Mqtt5ClientConfig {
     private final @NotNull ConnectDefaults connectDefaults;
     private final @NotNull ImmutableList<MqttClientConnectedListener> connectedListeners;
     private final @NotNull ImmutableList<MqttClientDisconnectedListener> disconnectedListeners;
-    private final @NotNull ImmutableList<TopicPriority> topicPriorities;
+
     private final @NotNull ClientComponent clientComponent;
 
     private volatile @Nullable EventLoop eventLoop;
     private int eventLoopAcquires;
     private long eventLoopAcquireCount;
-    private int trafficClass;
 
     private final @NotNull AtomicReference<@NotNull MqttClientState> state;
     private volatile @Nullable MqttClientConnectionConfig connectionConfig;
@@ -83,8 +79,7 @@ public class MqttClientConfig implements Mqtt5ClientConfig {
             final @NotNull MqttClientAdvancedConfig advancedConfig,
             final @NotNull ConnectDefaults connectDefaults,
             final @NotNull ImmutableList<MqttClientConnectedListener> connectedListeners,
-            final @NotNull ImmutableList<MqttClientDisconnectedListener> disconnectedListeners, 
-            @NotNull ImmutableList<TopicPriority> priorities) {
+            final @NotNull ImmutableList<MqttClientDisconnectedListener> disconnectedListeners) {
 
         this.mqttVersion = mqttVersion;
         this.clientIdentifier = clientIdentifier;
@@ -94,7 +89,6 @@ public class MqttClientConfig implements Mqtt5ClientConfig {
         this.connectDefaults = connectDefaults;
         this.connectedListeners = connectedListeners;
         this.disconnectedListeners = disconnectedListeners;
-        this.topicPriorities = priorities;
 
         clientComponent = SingletonComponent.INSTANCE.clientComponentBuilder().clientConfig(this).build();
 
@@ -102,11 +96,6 @@ public class MqttClientConfig implements Mqtt5ClientConfig {
         currentTransportConfig = transportConfig;
     }
 
-    @Override
-    public @NotNull ImmutableList<TopicPriority> getTopicPriorities() {
-        return topicPriorities;
-    }
-
     @Override
     public @NotNull MqttVersion getMqttVersion() {
         return mqttVersion;
