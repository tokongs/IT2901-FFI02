deleted file mode 100644
index 07fcfc4..0000000
--- a/broker/src/main/java/com/hivemq/bootstrap/ioc/lazysingleton/package-info.java
+++ /dev/null
@@ -1,22 +0,0 @@
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
-/**
- * Unfortunately Guice does not have the concept of lazy singletons when using {@link com.google.inject.Stage#PRODUCTION},
- * so we need to provide it our own. This package contains everything we need for lazy Singletons
- *
- * @author Dominik Obermaier
- */
-package com.hivemq.bootstrap.ioc.lazysingleton;
\ No newline at end of file
