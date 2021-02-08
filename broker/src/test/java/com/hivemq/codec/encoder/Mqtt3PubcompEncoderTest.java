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

import com.hivemq.codec.encoder.mqtt3.Mqtt3PubcompEncoder;
import com.hivemq.mqtt.message.pubcomp.PUBCOMP;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Mqtt3PubcompEncoderTest {


    private EmbeddedChannel channel;

    @Before
    public void setUp() throws Exception {

        channel = new EmbeddedChannel(new Mqtt3PubcompEncoder());
    }

    @Test
    public void test_pubcomp_sent() {
        channel.writeOutbound(new PUBCOMP(10));

        final ByteBuf buf = channel.readOutbound();

        final Mqtt3PubcompEncoder encoder = new Mqtt3PubcompEncoder();
        assertEquals(encoder.bufferSize(channel.pipeline().context(encoder), new PUBCOMP(10)), buf.readableBytes());

        assertEquals((byte) 0b0111_0000, buf.readByte());
        assertEquals((byte) 0b0000_0010, buf.readByte());
        assertEquals(10, buf.readUnsignedShort());

        assertEquals(0, buf.readableBytes());
    }

}