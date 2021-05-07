deleted file mode 100644
index be41d7e..0000000
--- a/broker/src/main/java/com/hivemq/annotations/ExecuteInSingleWriter.java
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
-package com.hivemq.annotations;
-
-import java.lang.annotation.ElementType;
-import java.lang.annotation.Retention;
-import java.lang.annotation.RetentionPolicy;
-import java.lang.annotation.Target;
-
-/**
- * This annotation indicates that a method must be executed by the SingleWriterService.
- *
- * @author Lukas Brandl
- */
-@Retention(RetentionPolicy.CLASS)
-@Target(ElementType.METHOD)
-public @interface ExecuteInSingleWriter {
-
-    String value() default "";
-}
\ No newline at end of file
