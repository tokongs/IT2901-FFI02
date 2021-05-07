deleted file mode 100644
index 8baee0b..0000000
--- a/broker/src/main/java/com/hivemq/persistence/local/xodus/bucket/Bucket.java
+++ /dev/null
@@ -1,55 +0,0 @@
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
-package com.hivemq.persistence.local.xodus.bucket;
-
-import com.hivemq.extension.sdk.api.annotations.Immutable;
-import com.hivemq.extension.sdk.api.annotations.NotNull;
-import jetbrains.exodus.env.Environment;
-import jetbrains.exodus.env.Store;
-
-import java.util.concurrent.atomic.AtomicBoolean;
-
-@Immutable
-public class Bucket {
-
-    @NotNull
-    private final Environment environment;
-    @NotNull
-    private final Store store;
-
-    private final AtomicBoolean closing = new AtomicBoolean(false);
-
-    public Bucket(@NotNull final Environment environment, @NotNull final Store store) {
-        this.environment = environment;
-        this.store = store;
-    }
-
-    public boolean close() {
-        return closing.compareAndSet(false, true);
-    }
-
-    @NotNull
-    public Environment getEnvironment() {
-        return environment;
-    }
-
-    @NotNull
-    public Store getStore() {
-        return store;
-    }
-
-
-}
\ No newline at end of file
