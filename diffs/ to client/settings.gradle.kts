similarity index 61%
rename from settings.gradle.kts
rename to client/settings.gradle.kts
index 16c98fe..6658e91 100644
--- a/settings.gradle.kts
+++ b/client/settings.gradle.kts
@@ -7,11 +7,17 @@ pluginManagement {
         id("com.github.johnrengelman.shadow") version "${extra["plugin.shadow.version"]}"
         id("biz.aQute.bnd.builder") version "${extra["plugin.bnd.version"]}"
         id("com.github.hierynomus.license") version "${extra["plugin.license.version"]}"
+        id("com.jfrog.bintray") version "${extra["plugin.bintray.version"]}"
+        id("com.github.breadmoirai.github-release") version "${extra["plugin.github-release.version"]}"
         id("com.github.sgtsilvio.gradle.utf8") version "${extra["plugin.utf8.version"]}"
         id("com.github.sgtsilvio.gradle.metadata") version "${extra["plugin.metadata.version"]}"
         id("com.github.sgtsilvio.gradle.javadoc-links") version "${extra["plugin.javadoc-links.version"]}"
     }
 }
 
-rootProject.name = "ffi02"
-include("client", "test-app")
\ No newline at end of file
+rootProject.name = "hivemq-mqtt-client"
+
+listOf("websocket", "proxy", "epoll", "reactor", "examples").forEach { module ->
+    include("${rootProject.name}-$module")
+    project(":${rootProject.name}-$module").projectDir = file(module)
+}
