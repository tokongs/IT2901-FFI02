deleted file mode 100644
index 4e531f7..0000000
--- a/broker/src/main/java/com/hivemq/bootstrap/ioc/lazysingleton/LazySingleton.java
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
-package com.hivemq.bootstrap.ioc.lazysingleton;
-
-import javax.inject.Scope;
-import java.lang.annotation.Retention;
-import java.lang.annotation.Target;
-
-import static java.lang.annotation.ElementType.METHOD;
-import static java.lang.annotation.ElementType.TYPE;
-import static java.lang.annotation.RetentionPolicy.RUNTIME;
-
-
-/**
- * An annotation for Singletons which should <b>not</b> be created eagerly on application startup.
- * <p>
- * For true lazy behaviour, make sure to use a {@link javax.inject.Provider} for these lazy singleton
- * objects
- *
- * @author Dominik Obermaier
- */
-@Target({TYPE, METHOD})
-@Retention(RUNTIME)
-@Scope
-public @interface LazySingleton {
-}
\ No newline at end of file
