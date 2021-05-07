deleted file mode 100644
index 4ebde98..0000000
--- a/client/src/main/java/com/hivemq/client/mqtt/MqttWebSocketConfigBuilder.java
+++ /dev/null
@@ -1,55 +0,0 @@
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
-package com.hivemq.client.mqtt;
-
-import com.hivemq.client.annotations.CheckReturnValue;
-import com.hivemq.client.annotations.DoNotImplement;
-import org.jetbrains.annotations.NotNull;
-
-/**
- * Builder for a {@link MqttWebSocketConfig}.
- *
- * @author Silvio Giebl
- * @since 1.0
- */
-@DoNotImplement
-public interface MqttWebSocketConfigBuilder extends MqttWebSocketConfigBuilderBase<MqttWebSocketConfigBuilder> {
-
-    /**
-     * Builds the {@link MqttWebSocketConfig}.
-     *
-     * @return the built {@link MqttWebSocketConfig}.
-     */
-    @CheckReturnValue
-    @NotNull MqttWebSocketConfig build();
-
-    /**
-     * Builder for a {@link MqttWebSocketConfig} that is applied to a parent.
-     *
-     * @param <P> the type of the result when the built {@link MqttWebSocketConfig} is applied to the parent.
-     */
-    @DoNotImplement
-    interface Nested<P> extends MqttWebSocketConfigBuilderBase<Nested<P>> {
-
-        /**
-         * Builds the {@link MqttWebSocketConfig} and applies it to the parent.
-         *
-         * @return the result when the built {@link MqttWebSocketConfig} is applied to the parent.
-         */
-        @NotNull P applyWebSocketConfig();
-    }
-}
