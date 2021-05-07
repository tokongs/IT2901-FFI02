deleted file mode 100644
index 505a849..0000000
--- a/client/src/main/java/com/hivemq/client/mqtt/mqtt3/message/Mqtt3ReturnCode.java
+++ /dev/null
@@ -1,39 +0,0 @@
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
-package com.hivemq.client.mqtt.mqtt3.message;
-
-import com.hivemq.client.annotations.DoNotImplement;
-
-/**
- * Return Code according to the MQTT 3 specification.
- *
- * @author Silvio Giebl
- * @since 1.0
- */
-@DoNotImplement
-public interface Mqtt3ReturnCode {
-
-    /**
-     * @return the byte code of this Return Code.
-     */
-    int getCode();
-
-    /**
-     * @return whether this Return Code is an Error Code.
-     */
-    boolean isError();
-}
