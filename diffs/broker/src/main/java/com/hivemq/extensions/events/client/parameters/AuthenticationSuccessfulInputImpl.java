deleted file mode 100644
index b2fee0a..0000000
--- a/broker/src/main/java/com/hivemq/extensions/events/client/parameters/AuthenticationSuccessfulInputImpl.java
+++ /dev/null
@@ -1,59 +0,0 @@
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
-package com.hivemq.extensions.events.client.parameters;
-
-import com.google.common.base.Preconditions;
-import com.hivemq.extension.sdk.api.annotations.NotNull;
-import com.hivemq.extension.sdk.api.client.parameter.ClientInformation;
-import com.hivemq.extension.sdk.api.client.parameter.ConnectionInformation;
-import com.hivemq.extension.sdk.api.events.client.parameters.AuthenticationSuccessfulInput;
-import com.hivemq.extensions.ExtensionInformationUtil;
-import com.hivemq.extensions.executor.task.PluginTaskInput;
-import io.netty.channel.Channel;
-
-import java.util.function.Supplier;
-
-/**
- * @author Florian Limpöck
- * @since 4.0.0
- */
-public class AuthenticationSuccessfulInputImpl implements AuthenticationSuccessfulInput, PluginTaskInput, Supplier<AuthenticationSuccessfulInputImpl> {
-
-    private final @NotNull ClientInformation clientInformation;
-    private final @NotNull ConnectionInformation connectionInformation;
-
-    public AuthenticationSuccessfulInputImpl(@NotNull final String clientId, @NotNull final Channel channel) {
-        Preconditions.checkNotNull(clientId, "client id must never be null");
-        Preconditions.checkNotNull(channel, "channel must never be null");
-        this.connectionInformation = ExtensionInformationUtil.getAndSetConnectionInformation(channel);
-        this.clientInformation = ExtensionInformationUtil.getAndSetClientInformation(channel, clientId);
-    }
-
-    @Override
-    public @NotNull ConnectionInformation getConnectionInformation() {
-        return connectionInformation;
-    }
-
-    @Override
-    public @NotNull ClientInformation getClientInformation() {
-        return clientInformation;
-    }
-
-    @Override
-    public @NotNull AuthenticationSuccessfulInputImpl get() {
-        return this;
-    }
-}
