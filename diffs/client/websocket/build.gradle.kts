deleted file mode 100644
index c9439a0..0000000
--- a/client/websocket/build.gradle.kts
+++ /dev/null
@@ -1,28 +0,0 @@
-plugins {
-    id("java-platform")
-}
-
-
-/* ******************** metadata ******************** */
-
-description = "Adds dependencies for the HiveMQ MQTT Client websocket module"
-
-metadata {
-    moduleName = "com.hivemq.client.mqtt.websocket"
-    readableName = "HiveMQ MQTT Client websocket module"
-}
-
-
-/* ******************** dependencies ******************** */
-
-javaPlatform {
-    allowDependencies()
-}
-
-dependencies {
-    api(rootProject)
-}
-
-configurations.runtime {
-    extendsFrom(rootProject.configurations["websocketImplementation"])
-}
