deleted file mode 100644
index 1de9e63..0000000
--- a/broker/src/test/java/com/hivemq/migration/MigrationUnitTest.java
+++ /dev/null
@@ -1,32 +0,0 @@
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
-package com.hivemq.migration;
-
-import org.junit.Test;
-
-import static org.junit.Assert.*;
-
-/**
- * @author Florian Limpöck
- */
-public class MigrationUnitTest {
-
-    @Test
-    public void test_payloads_first() {
-        assertEquals(MigrationUnit.values()[0], MigrationUnit.FILE_PERSISTENCE_PUBLISH_PAYLOAD);
-    }
-
-}
\ No newline at end of file
