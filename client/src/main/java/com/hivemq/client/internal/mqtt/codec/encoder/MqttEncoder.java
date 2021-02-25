/*
 * Copyright 2018-present HiveMQ and the HiveMQ Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hivemq.client.internal.mqtt.codec.encoder;
import com.hivemq.client.internal.mqtt.MqttClientConfig;
import com.hivemq.client.internal.mqtt.MqttClientConnectionConfig;
import com.hivemq.client.internal.mqtt.ioc.ConnectionScope;
import com.hivemq.client.internal.mqtt.message.MqttMessage;
import com.hivemq.client.internal.mqtt.message.MqttStatefulMessage;
import com.hivemq.client.internal.mqtt.message.publish.MqttPublish;
import com.hivemq.client.internal.mqtt.message.publish.MqttStatefulPublish;
import com.hivemq.client.mqtt.datatypes.MqttTopic;
import com.hivemq.client.mqtt.datatypes.MqttTopicFilter;
import com.hivemq.client.internal.util.collections.ImmutableList;
import com.hivemq.client.extensions.PriorityClass;
import com.hivemq.client.extensions.TopicPriority;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.ChannelPromise;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Main encoder for MQTT messages which delegates to the individual {@link MqttMessageEncoder}s.
 *
 * @author Silvio Giebl
 */
@ConnectionScope
public class MqttEncoder extends ChannelDuplexHandler {

    public static final @NotNull String NAME = "encoder";

    private final @NotNull MqttMessageEncoders encoders;
    private final @NotNull MqttEncoderContext context;

    private boolean inRead = false;
    private boolean pendingFlush = false;

    @Inject
    MqttEncoder(final @NotNull MqttMessageEncoders encoders) {
        this.encoders = encoders;
        context = new MqttEncoderContext(ByteBufAllocator.DEFAULT);
    }

    public void onConnected(final @NotNull MqttClientConnectionConfig connectionConfig) {
        context.setMaximumPacketSize(connectionConfig.getSendMaximumPacketSize());
    }

    public void setTOS(final @NotNull ChannelHandlerContext ctx,
                       final @NotNull Object msg) {

      /*
      MqttTopic topic = null;
      if (msg instanceof MqttPublish) { 
        topic = ((MqttPublish) msg).getTopic();
      } else if (msg instanceof MqttStatefulPublish) {
        topic =((MqttStatefulPublish) msg).stateless().getTopic();
      }
      */

      
      MqttTopic topic = (msg instanceof MqttPublish) 
                      ? ((MqttPublish) msg).getTopic()
                      : (msg instanceof MqttStatefulPublish)
                      ? ((MqttStatefulPublish) msg).stateless().getTopic()
                      : null;
      

      if (topic == null) return;

      ImmutableList<TopicPriority> priorities = 
        ((MqttClientConfig) ctx.channel().config()).getTopicPriorities();

      SocketChannelConfig config = ((SocketChannelConfig) ctx.channel().config());

      ImmutableList<TopicPriority> filteredPriorities = (ImmutableList<TopicPriority>)
        priorities.stream()
                  .filter(p -> p.getTopicFilter().matches(topic.filter())) 
                  .collect(Collectors.toList());

      PriorityClass priority = filteredPriorities.get(0) != null
                             ? filteredPriorities.get(0).getPriorityClass()
                             : PriorityClass.ROUTINE;
                              
      //byte prevTOS = (byte) config.getTrafficClass();
      byte newTOS = 4;
      
      // switch (priority) {
      //   //Shift once left, as least significant bit is reserved for something else,
      //   //Or with mask in order to preserve information in most significant bits
      //   case ROUTINE   : newTOS = 0x2;  break; //prevTOS | ((byte) (0 << 1)); break;
      //   case PRIORITY  : newTOS = 0x4;  break; //prevTOS | ((byte) (1 << 1)); break;
      //   case IMMEDIATE : newTOS = 0x8;  break; //prevTOS | ((byte) (2 << 1)); break;
      //   case FLASH     : newTOS = 0x16; break; //prevTOS | ((byte) (3 << 1)); break;
      //   default        : newTOS = 0x2;  break; //prevTOS | ((byte) (0 << 1)); 
      // }
 
      config.setTrafficClass(0x2);
    }

    @Override
    public void write(
            final @NotNull ChannelHandlerContext ctx,
            final @NotNull Object msg,
            final @NotNull ChannelPromise promise) {

        if (msg instanceof MqttMessage) {
            final MqttMessage message = (MqttMessage) msg;
            setTOS(ctx, message);
            final MqttMessageEncoder<?> messageEncoder = 
              encoders.get(message.getType().getCode());

            if (messageEncoder == null) {
                throw new UnsupportedOperationException();
            }
            final ByteBuf out = messageEncoder.castAndEncode(message, context);
            ctx.write(out, promise);
        } else {
            ctx.write(msg, promise);
        }
    }

    @Override
    public void flush(final @NotNull ChannelHandlerContext ctx) {
        if (inRead) {
            pendingFlush = true;
        } else {
            ctx.flush();
        }
    }

    @Override
    public void channelRead(final @NotNull ChannelHandlerContext ctx, final @NotNull Object msg) {
        inRead = true;
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(final @NotNull ChannelHandlerContext ctx) {
        ctx.fireChannelReadComplete();
        inRead = false;
        if (pendingFlush) {
            pendingFlush = false;
            ctx.flush();
        }
    }

    @Override
    public boolean isSharable() {
        return false;
    }
}
