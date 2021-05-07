deleted file mode 100644
index a13cccb..0000000
--- a/client/src/test/java/com/hivemq/client/internal/mqtt/message/subscribe/mqtt3/Mqtt3SubscribeViewTest.java
+++ /dev/null
@@ -1,32 +0,0 @@
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
-package com.hivemq.client.internal.mqtt.message.subscribe.mqtt3;
-
-import nl.jqno.equalsverifier.EqualsVerifier;
-import nl.jqno.equalsverifier.Warning;
-import org.junit.jupiter.api.Test;
-
-/**
- * @author Silvio Giebl
- */
-class Mqtt3SubscribeViewTest {
-
-    @Test
-    void equals() {
-        EqualsVerifier.forClass(Mqtt3SubscribeView.class).suppress(Warning.STRICT_INHERITANCE).verify();
-    }
-}
\ No newline at end of file
