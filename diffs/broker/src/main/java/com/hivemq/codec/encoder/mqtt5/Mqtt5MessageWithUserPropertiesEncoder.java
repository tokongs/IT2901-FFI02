index 0271b5b..36dda7f 100644
--- a/broker/src/main/java/com/hivemq/codec/encoder/mqtt5/Mqtt5MessageWithUserPropertiesEncoder.java
+++ b/broker/src/main/java/com/hivemq/codec/encoder/mqtt5/Mqtt5MessageWithUserPropertiesEncoder.java
@@ -29,12 +29,10 @@ import com.hivemq.mqtt.message.mqtt5.Mqtt5UserProperties;
 import com.hivemq.mqtt.message.mqtt5.MqttMessageWithUserProperties;
 import com.hivemq.mqtt.message.publish.PUBLISH;
 import com.hivemq.mqtt.message.reason.Mqtt5ReasonCode;
-import com.hivemq.mqtt.message.subscribe.Topic;
 import com.hivemq.util.ChannelAttributes;
 import io.netty.buffer.ByteBuf;
 import io.netty.channel.Channel;
 import io.netty.channel.ChannelHandlerContext;
-import io.netty.channel.socket.SocketChannelConfig;
 import io.netty.handler.codec.EncoderException;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
@@ -61,10 +59,7 @@ abstract class Mqtt5MessageWithUserPropertiesEncoder<T extends Message> extends
     // Need the security service here for enabling / disabling configuration on runtime.
     private final @NotNull SecurityConfigurationService securityConfigurationService;
 
-
-
-    public Mqtt5MessageWithUserPropertiesEncoder(final @NotNull MessageDroppedService messageDroppedService,
-            final @NotNull SecurityConfigurationService securityConfigurationService) {
+    public Mqtt5MessageWithUserPropertiesEncoder(final @NotNull MessageDroppedService messageDroppedService, final @NotNull SecurityConfigurationService securityConfigurationService) {
         this.messageDroppedService = messageDroppedService;
         this.securityConfigurationService = securityConfigurationService;
     }
