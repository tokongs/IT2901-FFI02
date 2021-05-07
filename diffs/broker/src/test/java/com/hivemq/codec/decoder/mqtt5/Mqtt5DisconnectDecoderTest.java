index 5bb2ce1..b2532ed 100644
--- a/broker/src/test/java/com/hivemq/codec/decoder/mqtt5/Mqtt5DisconnectDecoderTest.java
+++ b/broker/src/test/java/com/hivemq/codec/decoder/mqtt5/Mqtt5DisconnectDecoderTest.java
@@ -17,7 +17,6 @@ package com.hivemq.codec.decoder.mqtt5;
 
 import com.google.common.collect.ImmutableList;
 import com.hivemq.configuration.service.FullConfigurationService;
-import com.hivemq.configuration.service.TopicPriorityConfigurationService;
 import com.hivemq.configuration.service.SecurityConfigurationService;
 import com.hivemq.extension.sdk.api.annotations.NotNull;
 import com.hivemq.mqtt.message.ProtocolVersion;
@@ -25,7 +24,6 @@ import com.hivemq.mqtt.message.disconnect.DISCONNECT;
 import com.hivemq.mqtt.message.dropping.MessageDroppedService;
 import com.hivemq.mqtt.message.mqtt5.MqttUserProperty;
 import com.hivemq.mqtt.message.reason.Mqtt5DisconnectReasonCode;
-import com.hivemq.mqtt.topic.TopicMatcher;
 import com.hivemq.util.ChannelAttributes;
 import io.netty.buffer.ByteBuf;
 import io.netty.buffer.UnpooledByteBufAllocator;
@@ -52,12 +50,6 @@ public class Mqtt5DisconnectDecoderTest extends AbstractMqtt5DecoderTest {
     @Mock
     private SecurityConfigurationService securityConfigurationService;
 
-    @Mock
-    private TopicPriorityConfigurationService topicPriorityConfigurationService;
-
-    @Mock
-    private TopicMatcher topicMatcher;
-
     @Before
     public void before() {
         MockitoAnnotations.initMocks(this);
@@ -162,11 +154,7 @@ public class Mqtt5DisconnectDecoderTest extends AbstractMqtt5DecoderTest {
 
         //Now Encode
 
-        channel = new EmbeddedChannel(new TestMessageEncoder(
-                messageDroppedService,
-                securityConfigurationService,
-                topicPriorityConfigurationService,
-                topicMatcher));
+        channel = new EmbeddedChannel(new TestMessageEncoder(messageDroppedService, securityConfigurationService));
         channel.config().setAllocator(new UnpooledByteBufAllocator(false));
         channel.attr(ChannelAttributes.MQTT_VERSION).set(ProtocolVersion.MQTTv5);
 
