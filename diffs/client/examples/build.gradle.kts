deleted file mode 100644
index a2a898d..0000000
--- a/client/examples/build.gradle.kts
+++ /dev/null
@@ -1,21 +0,0 @@
-plugins {
-    id("java")
-    id(("com.github.sgtsilvio.gradle.metadata"))
-}
-
-
-/* ******************** metadata ******************** */
-
-description = "Examples using the HiveMQ MQTT Client"
-
-metadata {
-    moduleName = "com.hivemq.client.mqtt.examples"
-    readableName = "HiveMQ MQTT Client examples"
-}
-
-
-/* ******************** dependencies ******************** */
-
-dependencies {
-    implementation(rootProject)
-}
