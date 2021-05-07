index efbaa64..b975d9d 100644
--- a/broker/src/main/java/com/hivemq/bootstrap/netty/ChannelDependencies.java
+++ b/broker/src/main/java/com/hivemq/bootstrap/netty/ChannelDependencies.java
@@ -38,7 +38,6 @@ import com.hivemq.mqtt.handler.ping.PingRequestHandler;
 import com.hivemq.mqtt.handler.publish.MessageExpiryHandler;
 import com.hivemq.mqtt.handler.subscribe.SubscribeHandler;
 import com.hivemq.mqtt.handler.unsubscribe.UnsubscribeHandler;
-import com.hivemq.mqtt.topic.TopicMatcher;
 import com.hivemq.security.ssl.SslParameterHandler;
 import io.netty.channel.group.ChannelGroup;
 import io.netty.handler.traffic.GlobalTrafficShapingHandler;
@@ -111,8 +110,7 @@ public class ChannelDependencies {
             final @NotNull Provider<MessageExpiryHandler> publishMessageExpiryHandlerProvider,
             final @NotNull MqttServerDisconnector mqttServerDisconnector,
             final @NotNull InterceptorHandler interceptorHandler,
-            final @NotNull GlobalMQTTMessageCounter globalMQTTMessageCounter,
-            final @NotNull TopicMatcher topicMatcher) {
+            final @NotNull GlobalMQTTMessageCounter globalMQTTMessageCounter) {
 
         this.noConnectIdleHandler = noConnectIdleHandler;
         this.connectHandlerProvider = connectHandlerProvider;
@@ -128,8 +126,7 @@ public class ChannelDependencies {
         this.pingRequestHandler = pingRequestHandler;
         this.restrictionsConfigurationService = restrictionsConfigurationService;
         this.mqttConnectDecoder = mqttConnectDecoder;
-        this.mqttMessageEncoder = new MQTTMessageEncoder(encoderFactory, globalMQTTMessageCounter,
-                fullConfigurationService.topicConfiguration(), topicMatcher);
+        this.mqttMessageEncoder = new MQTTMessageEncoder(encoderFactory, globalMQTTMessageCounter);
         this.eventLog = eventLog;
         this.sslParameterHandler = sslParameterHandler;
         this.mqttDecoders = mqttDecoders;
