deleted file mode 100644
index b095c4c..0000000
--- a/broker/src/main/java/com/hivemq/diagnostic/data/AbstractInformation.java
+++ /dev/null
@@ -1,26 +0,0 @@
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
-package com.hivemq.diagnostic.data;
-
-/**
- * @author Dominik Obermaier
- */
-abstract class AbstractInformation {
-
-    protected StringBuilder addInformation(final StringBuilder infoBuilder, final String key, final String value) {
-        return infoBuilder.append(String.format("[%s] = [%s]\n", key, value));
-    }
-}
