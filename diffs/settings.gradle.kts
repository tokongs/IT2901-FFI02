deleted file mode 100644
index 16c98fe..0000000
--- a/settings.gradle.kts
+++ /dev/null
@@ -1,17 +0,0 @@
-pluginManagement {
-    repositories {
-        gradlePluginPortal()
-        mavenCentral()
-    }
-    plugins {
-        id("com.github.johnrengelman.shadow") version "${extra["plugin.shadow.version"]}"
-        id("biz.aQute.bnd.builder") version "${extra["plugin.bnd.version"]}"
-        id("com.github.hierynomus.license") version "${extra["plugin.license.version"]}"
-        id("com.github.sgtsilvio.gradle.utf8") version "${extra["plugin.utf8.version"]}"
-        id("com.github.sgtsilvio.gradle.metadata") version "${extra["plugin.metadata.version"]}"
-        id("com.github.sgtsilvio.gradle.javadoc-links") version "${extra["plugin.javadoc-links.version"]}"
-    }
-}
-
-rootProject.name = "ffi02"
-include("client", "test-app")
\ No newline at end of file
