deleted file mode 100644
index 03d6577..0000000
--- a/client/proxy/build.gradle.kts
+++ /dev/null
@@ -1,28 +0,0 @@
-plugins {
-    id("java-platform")
-}
-
-
-/* ******************** metadata ******************** */
-
-description = "Adds dependencies for the HiveMQ MQTT Client proxy module"
-
-metadata {
-    moduleName = "com.hivemq.client.mqtt.proxy"
-    readableName = "HiveMQ MQTT Client proxy module"
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
-    extendsFrom(rootProject.configurations["proxyImplementation"])
-}
