/*
 * Copyright 2019-present HiveMQ GmbH
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

package com.hivemq.codec.encoder;

import com.google.inject.Inject;
import com.hivemq.configuration.service.TopicConfigurationService;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.metrics.handler.GlobalMQTTMessageCounter;
import com.hivemq.mqtt.message.Message;
import com.hivemq.mqtt.message.publish.PUBLISH;
import com.hivemq.mqtt.message.subscribe.Topic;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Dominik Obermaier
 */
@ChannelHandler.Sharable
public class MQTTMessageEncoder extends MessageToByteEncoder<Message> {

    private final @NotNull EncoderFactory encoderFactory;
    private final @NotNull GlobalMQTTMessageCounter globalMQTTMessageCounter;
    private final @NotNull TopicConfigurationService topicConfigurationService;

    @Inject
    public MQTTMessageEncoder(
            final @NotNull EncoderFactory encoderFactory,
            final @NotNull GlobalMQTTMessageCounter globalMQTTMessageCounter,
            final @NotNull TopicConfigurationService topicConfigurationService) {
        this.encoderFactory = encoderFactory;
        this.globalMQTTMessageCounter = globalMQTTMessageCounter;
        this.topicConfigurationService = topicConfigurationService;
    }

    @Override
    protected void encode(
            final @NotNull ChannelHandlerContext ctx,
            final @NotNull Message msg,
            final @NotNull ByteBuf out) {
        globalMQTTMessageCounter.countOutbound(msg);
        encoderFactory.encode(ctx, msg, out);
        globalMQTTMessageCounter.countOutboundTraffic(out.readableBytes());
        if (msg instanceof PUBLISH){
            setTosValue(ctx, (PUBLISH)msg);
        }
    }

    @Override
    protected @NotNull ByteBuf allocateBuffer(
            final @NotNull ChannelHandlerContext ctx,
            final @NotNull Message msg,
            final boolean preferDirect) {
        return encoderFactory.allocateBuffer(ctx, msg, preferDirect);
    }

    public void setTosValue(@NotNull final ChannelHandlerContext ctx, @NotNull final PUBLISH message){
        final String topic = message.getTopic() ;
        topicConfigurationService.getTopics();

        for (Topic t : topicConfigurationService.getTopics()) {
            if (t.getTopic() == topic) {
                final int prevTos = ((SocketChannelConfig) ctx.channel().config()).getTrafficClass();
                final int priority = t.getPriority();
                if (priority < 100) {
                    ((SocketChannelConfig) ctx.channel().config()).setTrafficClass(prevTos | 0 << 1);
                } else if (priority < 200) {
                    ((SocketChannelConfig) ctx.channel().config()).setTrafficClass(prevTos | 1 << 1);
                } else if (priority < 300) {
                    ((SocketChannelConfig) ctx.channel().config()).setTrafficClass(prevTos | 2 << 1);
                } else {
                    ((SocketChannelConfig) ctx.channel().config()).setTrafficClass(prevTos | 3 << 1);
                }
            }
        }
    }
}


