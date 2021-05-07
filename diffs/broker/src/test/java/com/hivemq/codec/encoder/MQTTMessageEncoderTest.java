index ce080ef..5537bc2 100644
--- a/broker/src/test/java/com/hivemq/codec/encoder/MQTTMessageEncoderTest.java
+++ b/broker/src/test/java/com/hivemq/codec/encoder/MQTTMessageEncoderTest.java
@@ -15,7 +15,6 @@
  */
 package com.hivemq.codec.encoder;
 
-import com.hivemq.configuration.service.TopicPriorityConfigurationService;
 import com.hivemq.configuration.service.SecurityConfigurationService;
 import com.hivemq.mqtt.message.PINGRESP;
 import com.hivemq.mqtt.message.ProtocolVersion;
@@ -31,14 +30,12 @@ import com.hivemq.mqtt.message.pubrel.PUBREL;
 import com.hivemq.mqtt.message.reason.Mqtt5SubAckReasonCode;
 import com.hivemq.mqtt.message.suback.SUBACK;
 import com.hivemq.mqtt.message.unsuback.UNSUBACK;
-import com.hivemq.mqtt.topic.TopicMatcher;
 import com.hivemq.util.ChannelAttributes;
 import io.netty.buffer.ByteBuf;
 import io.netty.channel.embedded.EmbeddedChannel;
 import org.junit.Before;
 import org.junit.Test;
 import org.mockito.Mock;
-import org.mockito.MockitoAnnotations;
 import util.TestMessageEncoder;
 import util.TestMessageUtil;
 
@@ -56,20 +53,9 @@ public class MQTTMessageEncoderTest {
     @Mock
     private SecurityConfigurationService securityConfigurationService;
 
-    @Mock
-    private TopicPriorityConfigurationService topicPriorityConfigurationService;
-
-    @Mock
-    private TopicMatcher topicMatcher;
-
     @Before
     public void setUp() throws Exception {
-        MockitoAnnotations.initMocks(this);
-        channel = new EmbeddedChannel(new TestMessageEncoder(
-                messageDroppedService,
-                securityConfigurationService,
-                topicPriorityConfigurationService,
-                topicMatcher));
+        channel = new EmbeddedChannel(new TestMessageEncoder(messageDroppedService, securityConfigurationService));
         channel.attr(ChannelAttributes.MQTT_VERSION).set(ProtocolVersion.MQTTv3_1);
     }
 
