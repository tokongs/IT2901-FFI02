deleted file mode 100644
index ab4075f..0000000
--- a/broker/src/main/java/com/hivemq/websocket/WebSocketBinaryFrameHandler.java
+++ /dev/null
@@ -1,28 +0,0 @@
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
-package com.hivemq.websocket;
-
-import io.netty.channel.ChannelHandlerContext;
-import io.netty.channel.SimpleChannelInboundHandler;
-import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
-
-public class WebSocketBinaryFrameHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {
-
-    @Override
-    protected void channelRead0(final ChannelHandlerContext ctx, final BinaryWebSocketFrame msg) throws Exception {
-        ctx.fireChannelRead(msg.content().retain());
-    }
-}
\ No newline at end of file
