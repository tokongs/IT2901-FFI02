deleted file mode 100644
index c614881..0000000
--- a/broker/src/main/java/com/hivemq/extensions/executor/task/PluginInOutTask.java
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
-package com.hivemq.extensions.executor.task;
-
-import java.util.function.BiFunction;
-
-/**
- * A wrapper for an extension task that provides information for the extension developer and can affect HiveMQ.
- * <p>
- * It is assumed that only the returned {@link PluginTaskOutput} object of the {@link BiFunction#apply(Object, Object)}
- * call is relevant.
- *
- * @author Georg Held
- */
-public interface PluginInOutTask<I extends PluginTaskInput, O extends PluginTaskOutput> extends BiFunction<I, O, O>, PluginTask {
-}
