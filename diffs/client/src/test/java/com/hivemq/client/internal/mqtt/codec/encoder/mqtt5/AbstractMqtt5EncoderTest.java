deleted file mode 100644
index 1f785c2..0000000
--- a/client/src/test/java/com/hivemq/client/internal/mqtt/codec/encoder/mqtt5/AbstractMqtt5EncoderTest.java
+++ /dev/null
@@ -1,31 +0,0 @@
-/*
- * Copyright 2018-present HiveMQ and the HiveMQ Community
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
-
-package com.hivemq.client.internal.mqtt.codec.encoder.mqtt5;
-
-import com.hivemq.client.internal.mqtt.codec.encoder.AbstractMqttEncoderTest;
-import com.hivemq.client.internal.mqtt.codec.encoder.MqttMessageEncoders;
-import org.jetbrains.annotations.NotNull;
-
-/**
- * @author Silvio Giebl
- */
-abstract class AbstractMqtt5EncoderTest extends AbstractMqttEncoderTest {
-
-    AbstractMqtt5EncoderTest(final @NotNull MqttMessageEncoders messageEncoders, final boolean connected) {
-        super(messageEncoders, connected);
-    }
-}
