deleted file mode 100644
index 64ffeac..0000000
--- a/broker/src/main/java/com/hivemq/statistics/UsageStatisticsSender.java
+++ /dev/null
@@ -1,29 +0,0 @@
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
-package com.hivemq.statistics;
-
-/**
- * @author Christoph Schäbel
- */
-public interface UsageStatisticsSender {
-
-    /**
-     * Send statistics.
-     *
-     * @param jsonPayload the statistics to sent.
-     */
-    void sendStatistics(final String jsonPayload);
-}
