index a4644e6..274a9b5 100644
--- a/broker/src/test/java/util/TestMessageEncoder.java
+++ b/broker/src/test/java/util/TestMessageEncoder.java
@@ -21,7 +21,6 @@ import com.hivemq.codec.encoder.EncoderFactory;
 import com.hivemq.codec.encoder.FixedSizeMessageEncoder;
 import com.hivemq.codec.encoder.MQTTMessageEncoder;
 import com.hivemq.configuration.HivemqId;
-import com.hivemq.configuration.service.TopicPriorityConfigurationService;
 import com.hivemq.configuration.service.SecurityConfigurationService;
 import com.hivemq.extension.sdk.api.annotations.NotNull;
 import com.hivemq.logging.EventLog;
@@ -31,7 +30,6 @@ import com.hivemq.mqtt.handler.disconnect.MqttServerDisconnectorImpl;
 import com.hivemq.mqtt.message.Message;
 import com.hivemq.mqtt.message.PINGREQ;
 import com.hivemq.mqtt.message.dropping.MessageDroppedService;
-import com.hivemq.mqtt.topic.TopicMatcher;
 import io.netty.buffer.ByteBuf;
 import io.netty.channel.ChannelHandler;
 import io.netty.channel.ChannelHandlerContext;
@@ -48,16 +46,13 @@ public class TestMessageEncoder extends MQTTMessageEncoder {
 
     public TestMessageEncoder(
             final MessageDroppedService messageDroppedService,
-            final SecurityConfigurationService securityConfigurationService,
-            final TopicPriorityConfigurationService topicPriorityConfigurationService,
-            final TopicMatcher topicMatcher) {
+            final SecurityConfigurationService securityConfigurationService) {
         super(
                 new EncoderFactory(
                         messageDroppedService,
                         securityConfigurationService,
                         new MqttServerDisconnectorImpl(new EventLog(), new HivemqId())),
-                new GlobalMQTTMessageCounter(new MetricsHolder(new MetricRegistry())),
-                topicPriorityConfigurationService, topicMatcher);
+                new GlobalMQTTMessageCounter(new MetricsHolder(new MetricRegistry())));
         pingreqEncoder = new PingreqEncoder();
     }
 
