deleted file mode 100644
index 9d31ffe..0000000
--- a/broker/src/test/java/com/hivemq/persistence/retained/RetainedMessagePersistenceProviderTest.java
+++ /dev/null
@@ -1,39 +0,0 @@
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
-package com.hivemq.persistence.retained;
-
-import org.junit.Test;
-import org.mockito.Mockito;
-
-import static org.junit.Assert.assertEquals;
-
-/**
- * @author Florian Limpöck
- * @since 4.1.0
- */
-public class RetainedMessagePersistenceProviderTest {
-
-    @Test
-    public void test_get() {
-
-        final RetainedMessagePersistenceImpl retainedMessagePersistence = Mockito.mock(RetainedMessagePersistenceImpl.class);
-
-        final RetainedMessagePersistenceProvider provider = new RetainedMessagePersistenceProvider(() -> retainedMessagePersistence);
-
-        assertEquals(retainedMessagePersistence, provider.get());
-
-    }
-}
\ No newline at end of file
