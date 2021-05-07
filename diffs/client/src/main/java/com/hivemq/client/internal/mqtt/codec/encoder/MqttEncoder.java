index 359f9d7..3b99bb9 100644
--- a/client/src/main/java/com/hivemq/client/internal/mqtt/codec/encoder/MqttEncoder.java
+++ b/client/src/main/java/com/hivemq/client/internal/mqtt/codec/encoder/MqttEncoder.java
@@ -15,31 +15,17 @@
  */
 
 package com.hivemq.client.internal.mqtt.codec.encoder;
-import com.hivemq.client.internal.mqtt.MqttClientConfig;
+
 import com.hivemq.client.internal.mqtt.MqttClientConnectionConfig;
 import com.hivemq.client.internal.mqtt.ioc.ConnectionScope;
 import com.hivemq.client.internal.mqtt.message.MqttMessage;
-import com.hivemq.client.internal.mqtt.message.MqttStatefulMessage;
-import com.hivemq.client.internal.mqtt.message.publish.MqttPublish;
-import com.hivemq.client.internal.mqtt.message.publish.MqttStatefulPublish;
-import com.hivemq.client.mqtt.datatypes.MqttTopic;
-import com.hivemq.client.mqtt.datatypes.MqttTopicFilter;
-import com.hivemq.client.internal.util.collections.ImmutableList;
-import com.hivemq.client.extensions.PriorityClass;
-import com.hivemq.client.extensions.TopicPriority;
 import io.netty.buffer.ByteBuf;
 import io.netty.buffer.ByteBufAllocator;
 import io.netty.channel.ChannelDuplexHandler;
 import io.netty.channel.ChannelHandlerContext;
-import io.netty.channel.socket.SocketChannelConfig;
 import io.netty.channel.ChannelPromise;
 import org.jetbrains.annotations.NotNull;
 
-import java.util.Optional;
-import java.util.Comparator;
-import java.util.stream.Collectors;
-import java.util.Collections;
-
 import javax.inject.Inject;
 
 /**
@@ -54,7 +40,7 @@ public class MqttEncoder extends ChannelDuplexHandler {
 
     private final @NotNull MqttMessageEncoders encoders;
     private final @NotNull MqttEncoderContext context;
-    private ImmutableList<TopicPriority> priorities = ImmutableList.of();
+
     private boolean inRead = false;
     private boolean pendingFlush = false;
 
@@ -64,49 +50,8 @@ public class MqttEncoder extends ChannelDuplexHandler {
         context = new MqttEncoderContext(ByteBufAllocator.DEFAULT);
     }
 
-    public void onConnected(final @NotNull MqttClientConnectionConfig connectionConfig,
-                            final @NotNull MqttClientConfig clientConfig) {
-      context.setMaximumPacketSize(connectionConfig.getSendMaximumPacketSize());
-      this.priorities = clientConfig.getTopicPriorities();
-    }
-
     public void onConnected(final @NotNull MqttClientConnectionConfig connectionConfig) {
-      context.setMaximumPacketSize(connectionConfig.getSendMaximumPacketSize());
-    }
-
-    public void setTOS(final @NotNull ChannelHandlerContext ctx,
-                       final @NotNull Object msg) {
-
-      MqttTopic topic = (msg instanceof MqttPublish) 
-                      ? ((MqttPublish) msg).getTopic()
-                      : (msg instanceof MqttStatefulPublish)
-                      ? ((MqttStatefulPublish) msg).stateless().getTopic()
-                      : null;
-
-      if (topic == null) return;
-
-      if (!(ctx.channel().config() instanceof SocketChannelConfig)) return;
-      SocketChannelConfig config = ((SocketChannelConfig) ctx.channel().config());
-
-      PriorityClass priority = 
-        priorities.stream()
-                  .filter(p -> p.getTopicFilter().matches(topic.filter()))
-                  .max(Comparator.comparingInt(a -> a.getTopicFilter().getLevels().size()))
-                  .map(a -> a.getPriorityClass())
-                  .orElseGet(() -> PriorityClass.ROUTINE);
-                  
-      int tosField = config.getTrafficClass() & 0b11100111;
-      
-      switch (priority) {
-        //Or with mask in order to preserve information in most significant bits
-        case ROUTINE   : tosField |= 0b00000000; break;
-        case PRIORITY  : tosField |= 0b00001000; break;
-        case IMMEDIATE : tosField |= 0b00010000; break;
-        case FLASH     : tosField |= 0b00011000; break;
-        default        : tosField |= 0b00000000; 
-      }
-       
-      config.setTrafficClass(tosField);
+        context.setMaximumPacketSize(connectionConfig.getSendMaximumPacketSize());
     }
 
     @Override
@@ -117,10 +62,7 @@ public class MqttEncoder extends ChannelDuplexHandler {
 
         if (msg instanceof MqttMessage) {
             final MqttMessage message = (MqttMessage) msg;
-            setTOS(ctx, message);
-            final MqttMessageEncoder<?> messageEncoder = 
-              encoders.get(message.getType().getCode());
-
+            final MqttMessageEncoder<?> messageEncoder = encoders.get(message.getType().getCode());
             if (messageEncoder == null) {
                 throw new UnsupportedOperationException();
             }
