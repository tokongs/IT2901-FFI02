index f04b885..c56000c 100644
--- a/client/build.gradle.kts
+++ b/client/build.gradle.kts
@@ -4,6 +4,11 @@ plugins {
     id("java-library")
     id("com.github.johnrengelman.shadow")
     id("biz.aQute.bnd.builder")
+    id("maven-publish")
+    id("com.jfrog.bintray")
+    id("com.github.breadmoirai.github-release")
+    id("com.github.hierynomus.license")
+    id("pmd")
     id("com.github.sgtsilvio.gradle.utf8")
     id("com.github.sgtsilvio.gradle.metadata")
     id("com.github.sgtsilvio.gradle.javadoc-links")
@@ -193,7 +198,157 @@ tasks.shadowJar {
 
     minimize()
 }
+
+
+/* ******************** publishing ******************** */
+
+apply("${rootDir}/gradle/publishing.gradle.kts")
+
+allprojects {
+    plugins.withId("java-library") {
+
+        plugins.apply("maven-publish")
+
+        publishing.publications.register<MavenPublication>("base") {
+            from(components["java"])
+            suppressAllPomMetadataWarnings()
+        }
+    }
+
+    plugins.withId("java-platform") {
+
+        plugins.apply("maven-publish")
+
+        publishing.publications.register<MavenPublication>("base") {
+            from(components["javaPlatform"])
+            suppressAllPomMetadataWarnings()
+        }
+    }
+}
+
+publishing.publications.register<MavenPublication>("shaded") {
+    artifactId = "${project.name}-shaded"
+    artifact(tasks["shadowJar"])
+    artifact(tasks["javadocJar"])
+    artifact(tasks["sourcesJar"])
+    pom.withXml {
+        asNode().appendNode("dependencies").apply {
+            configurations["apiElements"].allDependencies.forEach {
+                appendNode("dependency").apply {
+                    appendNode("groupId", it.group)
+                    appendNode("artifactId", it.name)
+                    appendNode("version", it.version)
+                    appendNode("scope", "compile")
+                }
+            }
+        }
+    }
+}
+
+allprojects {
+    plugins.withId("maven-publish") {
+        afterEvaluate {
+            publishing.publications.withType<MavenPublication>().configureEach {
+                pom.withXml {
+                    (asNode()["dependencies"] as groovy.util.NodeList).forEach { dependencies ->
+                        (dependencies as groovy.util.Node).children().forEach { dependency ->
+                            val dep = dependency as groovy.util.Node
+                            val optional = dep["optional"] as groovy.util.NodeList
+                            val scope = dep["scope"] as groovy.util.NodeList
+                            if (!optional.isEmpty() && (optional[0] as groovy.util.Node).text() == "true") {
+                                (scope[0] as groovy.util.Node).setValue("runtime")
+                            }
+                        }
+                    }
+                }
+            }
+        }
+    }
+}
+
+allprojects {
+    plugins.withId("maven-publish") {
+
+        plugins.apply("com.jfrog.bintray")
+
+        bintray {
+            user = "${rootProject.extra["bintray_username"]}"
+            key = "${rootProject.extra["bintray_apiKey"]}"
+            publish = true
+            pkg.apply {
+                userOrg = "hivemq"
+                repo = "HiveMQ"
+                name = "hivemq-mqtt-client"
+                desc = project.description
+                websiteUrl = metadata.url
+                issueTrackerUrl = metadata.issueManagement.url
+                vcsUrl = metadata.scm.url
+                setLicenses(metadata.license.shortName)
+                setLabels("mqtt", "mqtt-client", "iot", "internet-of-things", "rxjava2", "reactive-streams", "backpressure")
+                version.apply {
+                    released = Date().toString()
+                    vcsTag = "v${project.version}"
+                    gpg.apply {
+                        sign = true
+                    }
+                }
+            }
+        }
+        afterEvaluate {
+            bintray.setPublications(*publishing.publications.withType<MavenPublication>().names.toTypedArray())
+        }
+
+        // workaround for publishing gradle module metadata https://github.com/bintray/gradle-bintray-plugin/issues/229
+        tasks.withType<com.jfrog.bintray.gradle.tasks.BintrayUploadTask> {
+            doFirst {
+                publishing.publications.withType<MavenPublication> {
+                    val moduleFile = buildDir.resolve("publications/$name/module.json")
+                    if (moduleFile.exists()) {
+                        artifact(moduleFile).extension = "module"
+                    }
+                }
+            }
+        }
+    }
+}
+
+githubRelease {
+    token("${rootProject.extra["github_token"]}")
+    owner.set(metadata.github.org)
+    repo.set(metadata.github.repo)
+    targetCommitish.set("master")
+    tagName.set("v${project.version}")
+    releaseName.set("${project.version}")
+}
+
+
+/* ******************** checks ******************** */
+
+allprojects {
+    plugins.apply("com.github.hierynomus.license")
+
+    license {
+        header = rootDir.resolve("HEADER")
+        mapping("java", "SLASHSTAR_STYLE")
+    }
+}
+
+allprojects {
+    plugins.withId("java") {
+
+        plugins.apply("pmd")
+
+        pmd {
+            toolVersion = "5.7.0"
+        }
+    }
+}
+
+apply("${rootDir}/gradle/japicc.gradle.kts")
+
+
 /* ******************** build cache ******************** */
+
 allprojects {
     normalization {
         runtimeClasspath {
