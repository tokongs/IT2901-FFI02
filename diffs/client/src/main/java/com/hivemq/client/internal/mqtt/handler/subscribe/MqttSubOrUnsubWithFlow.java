deleted file mode 100644
index a8ae37a..0000000
--- a/client/src/main/java/com/hivemq/client/internal/mqtt/handler/subscribe/MqttSubOrUnsubWithFlow.java
+++ /dev/null
@@ -1,30 +0,0 @@
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
-package com.hivemq.client.internal.mqtt.handler.subscribe;
-
-import com.hivemq.client.internal.util.collections.NodeList;
-import org.jetbrains.annotations.Nullable;
-
-/**
- * @author Silvio Giebl
- */
-abstract class MqttSubOrUnsubWithFlow extends NodeList.Node<MqttSubOrUnsubWithFlow> {
-
-    int packetIdentifier;
-
-    abstract @Nullable MqttSubscriptionFlow<?> getFlow();
-}
