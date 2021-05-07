deleted file mode 100644
index 07b6609..0000000
--- a/broker/src/test/java/com/hivemq/extensions/packets/subscribe/SubscriptionImplTest.java
+++ /dev/null
@@ -1,36 +0,0 @@
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
-package com.hivemq.extensions.packets.subscribe;
-
-import com.hivemq.extension.sdk.api.annotations.NotNull;
-import nl.jqno.equalsverifier.EqualsVerifier;
-import nl.jqno.equalsverifier.Warning;
-import org.junit.Test;
-
-/**
- * @author Silvio Giebl
- */
-public class SubscriptionImplTest {
-
-    @Test
-    public void equals() {
-        EqualsVerifier.forClass(SubscriptionImpl.class)
-                .withIgnoredAnnotations(NotNull.class) // EqualsVerifier thinks @NotNull Optional is @NotNull
-                .withNonnullFields("topicFilter", "qos", "retainHandling")
-                .suppress(Warning.STRICT_INHERITANCE)
-                .verify();
-    }
-}
\ No newline at end of file
