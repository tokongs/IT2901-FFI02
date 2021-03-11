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
import com.hivemq.configuration.service.TopicPriorityConfigurationService;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extensions.priority.TopicPriority;
import com.hivemq.metrics.handler.GlobalMQTTMessageCounter;
import com.hivemq.mqtt.message.Message;
import com.hivemq.mqtt.message.publish.PUBLISH;
import com.hivemq.mqtt.topic.TopicMatcher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.logging.Logger;

/**
 * @author Dominik Obermaier
 */
@ChannelHandler.Sharable
public class MQTTMessageEncoder extends MessageToByteEncoder<Message> {

    private final @NotNull EncoderFactory encoderFactory;
    private final @NotNull GlobalMQTTMessageCounter globalMQTTMessageCounter;
    private final @NotNull TopicPriorityConfigurationService TopicPriorityConfigurationService;
    private final @NotNull TopicMatcher topicMatcher;
    private final int ROUTINE_BIT_MASK = 0b11100111;
    private final int PRIORITY_BIT_MASK = 0b11101111;
    private final int IMMEDIATE_BIT_MASK = 0b11110111;
    private final int FLASH_BIT_MASK = 0b11111111;

    @Inject
    public MQTTMessageEncoder(
            final @NotNull EncoderFactory encoderFactory,
            final @NotNull GlobalMQTTMessageCounter globalMQTTMessageCounter,
            final @NotNull TopicPriorityConfigurationService TopicPriorityConfigurationService,
            final @NotNull TopicMatcher topicMatcher) {
        this.encoderFactory = encoderFactory;
        this.globalMQTTMessageCounter = globalMQTTMessageCounter;
        this.TopicPriorityConfigurationService = TopicPriorityConfigurationService;
        this.topicMatcher = topicMatcher;
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
        TopicPriorityConfigurationService.getPriorities();
        try {
            final int prevTos = ((SocketChannelConfig) ctx.channel().config()).getTrafficClass();
            ((SocketChannelConfig) ctx.channel().config()).setTrafficClass(prevTos & ROUTINE_BIT_MASK);
            for (TopicPriority priority : TopicPriorityConfigurationService.getPriorities()) {
                if (topicMatcher.matches(priority.getTopicFilter(), topic)) {
                    switch (priority.getPriorityClass()) {
                        case PRIORITY:
                            ((SocketChannelConfig) ctx.channel().config()).setTrafficClass((prevTos | 24) & PRIORITY_BIT_MASK);
                            return;
                        case IMMEDIATE:
                            ((SocketChannelConfig) ctx.channel().config()).setTrafficClass((prevTos | 24) & IMMEDIATE_BIT_MASK);
                            return;
                        case FLASH:
                            ((SocketChannelConfig) ctx.channel().config()).setTrafficClass((prevTos | 24) & FLASH_BIT_MASK);
                            return;
                    }
                }
            }
        } catch (ClassCastException e){
            Logger log = Logger.getLogger("MQTTMessageEncoder");
            log.info("Failed to set traffic class... (ClassCastException)");
        }

    }
}


