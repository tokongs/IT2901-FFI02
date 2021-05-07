deleted file mode 100644
index 982a83e..0000000
--- a/broker/src/test/java/util/TestException.java
+++ /dev/null
@@ -1,33 +0,0 @@
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
-package util;
-
-/**
- * @author Florian Limpöck
- * @since 4.0.0
- */
-public class TestException extends Exception {
-
-    public static final TestException INSTANCE = new TestException();
-
-    private TestException() {
-        super("FOR TESTS ONLY");
-    }
-
-    public TestException(final String text) {
-        super(text);
-    }
-}
