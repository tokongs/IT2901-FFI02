index 3460291..0fd42b9 100644
--- a/client/src/main/java/com/hivemq/client/internal/mqtt/MqttRxClientBuilderBase.java
+++ b/client/src/main/java/com/hivemq/client/internal/mqtt/MqttRxClientBuilderBase.java
@@ -16,7 +16,6 @@
 
 package com.hivemq.client.internal.mqtt;
 
-import com.hivemq.client.extensions.TopicPriority;
 import com.hivemq.client.internal.mqtt.advanced.MqttClientAdvancedConfig;
 import com.hivemq.client.internal.mqtt.datatypes.MqttClientIdentifierImpl;
 import com.hivemq.client.internal.mqtt.lifecycle.MqttClientAutoReconnectImpl;
@@ -49,7 +48,7 @@ public abstract class MqttRxClientBuilderBase<B extends MqttRxClientBuilderBase<
     private @Nullable MqttClientAutoReconnectImpl autoReconnect;
     private ImmutableList.@Nullable Builder<MqttClientConnectedListener> connectedListenersBuilder;
     private ImmutableList.@Nullable Builder<MqttClientDisconnectedListener> disconnectedListenersBuilder;
-    private ImmutableList.@Nullable Builder<TopicPriority> topicPriorityBuilder;
+
     protected MqttRxClientBuilderBase() {}
 
     protected MqttRxClientBuilderBase(final @NotNull MqttRxClientBuilderBase<?> clientBuilder) {
@@ -166,14 +165,6 @@ public abstract class MqttRxClientBuilderBase<B extends MqttRxClientBuilderBase<
         return self();
     }
 
-    public @NotNull B addTopicPriority(final @NotNull TopicPriority topicPriority){
-        if(topicPriorityBuilder == null){
-            topicPriorityBuilder = ImmutableList.builder();
-        }
-        topicPriorityBuilder.add(topicPriority);
-        return self();
-    }
-
     public @NotNull B addDisconnectedListener(final @Nullable MqttClientDisconnectedListener disconnectedListener) {
         Checks.notNull(disconnectedListener, "Disconnected listener");
         if (disconnectedListenersBuilder == null) {
@@ -213,20 +204,13 @@ public abstract class MqttRxClientBuilderBase<B extends MqttRxClientBuilderBase<
                 .build();
     }
 
-    private @NotNull ImmutableList<TopicPriority> buildTopicPriorities() {
-        if(topicPriorityBuilder == null){
-            return ImmutableList.of();
-        }
-        return ImmutableList.<TopicPriority>builder().addAll(topicPriorityBuilder.build()).build();
-    }
-
     protected @NotNull MqttClientConfig buildClientConfig(
             final @NotNull MqttVersion mqttVersion,
             final @NotNull MqttClientAdvancedConfig advancedConfig,
             final @NotNull MqttClientConfig.ConnectDefaults connectDefaults) {
 
         return new MqttClientConfig(mqttVersion, identifier, buildTransportConfig(), executorConfig, advancedConfig,
-                connectDefaults, buildConnectedListeners(), buildDisconnectedListeners(), buildTopicPriorities());
+                connectDefaults, buildConnectedListeners(), buildDisconnectedListeners());
     }
 
     public static class Choose extends MqttRxClientBuilderBase<Choose> implements MqttClientBuilder {
