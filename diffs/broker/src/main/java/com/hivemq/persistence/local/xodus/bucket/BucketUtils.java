deleted file mode 100644
index 569c1ef..0000000
--- a/broker/src/main/java/com/hivemq/persistence/local/xodus/bucket/BucketUtils.java
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
-package com.hivemq.persistence.local.xodus.bucket;
-
-import com.hivemq.extension.sdk.api.annotations.NotNull;
-import net.openhft.hashing.LongHashFunction;
-
-/**
- * @author Dominik Obermaier
- */
-public class BucketUtils {
-
-    private static final LongHashFunction XX = LongHashFunction.xx();
-
-    public static int getBucket(@NotNull final String id, final int bucketSize) {
-        return Math.abs((int) (XX.hashChars(id) % bucketSize));
-    }
-
-}
