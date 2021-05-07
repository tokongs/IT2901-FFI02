deleted file mode 100644
index 97a1d87..0000000
--- a/broker/src/main/java/com/hivemq/codec/encoder/MqttPingrespEncoder.java
+++ /dev/null
@@ -1,40 +0,0 @@
-/*
- * Copyright 2019-present HiveMQ GmbH
- *
- * Licensed under the Apache License, Version 2.0 (the "License");
- * you may not use this file except in compliance with the License.
- * You may obtain a copy of the License at
- *
- *     http://www.apache.org/licenses/LICENSE-2.0
- *
- * Unless required by applicable law or agreed to in writing, software
- * distributed under the License is distributed on an "AS IS" BASIS,
- * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
- * See the License for the specific language governing permissions and
- * limitations under the License.
- */
-package com.hivemq.codec.encoder;
-
-import com.hivemq.mqtt.message.PINGRESP;
-import io.netty.buffer.ByteBuf;
-import io.netty.channel.ChannelHandlerContext;
-
-public class MqttPingrespEncoder extends FixedSizeMessageEncoder<PINGRESP> implements MqttEncoder<PINGRESP> {
-
-    private static final byte PINGRESP_FIXED_HEADER = (byte) 0b1101_0000;
-    private static final byte PINGRESP_REMAINING_LENGTH = (byte) 0b0000_0000;
-    public static final int ENCODED_PINGRESP_SIZE = 2;
-
-    @Override
-    public void encode(final ChannelHandlerContext ctx, final PINGRESP msg, final ByteBuf out) {
-
-        out.writeByte(PINGRESP_FIXED_HEADER);
-        out.writeByte(PINGRESP_REMAINING_LENGTH);
-    }
-
-    @Override
-    public int bufferSize(final ChannelHandlerContext ctx, final PINGRESP msg) {
-        return ENCODED_PINGRESP_SIZE;
-    }
-
-}
\ No newline at end of file
