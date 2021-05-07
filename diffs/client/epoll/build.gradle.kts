deleted file mode 100644
index d29901c..0000000
--- a/client/epoll/build.gradle.kts
+++ /dev/null
@@ -1,29 +0,0 @@
-plugins {
-    id("java-platform")
-    id("com.github.sgtsilvio.gradle.metadata")
-}
-
-
-/* ******************** metadata ******************** */
-
-description = "Adds dependencies for the HiveMQ MQTT Client epoll module"
-
-metadata {
-    moduleName = "com.hivemq.client.mqtt.epoll"
-    readableName = "HiveMQ MQTT Client epoll module"
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
-    extendsFrom(rootProject.configurations["epollImplementation"])
-}
