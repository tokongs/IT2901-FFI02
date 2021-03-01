import java.util.*

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow")
    id("biz.aQute.bnd.builder")
    id("com.github.sgtsilvio.gradle.utf8")
    id("com.github.sgtsilvio.gradle.metadata")
    id("com.github.sgtsilvio.gradle.javadoc-links")
}


/* ******************** metadata ******************** */

allprojects {
    group = "com.hivemq"
    description = "HiveMQ MQTT Client is an MQTT 5.0 and MQTT 3.1.1 compatible and feature-rich high-performance " +
            "Java client library with different API flavours and backpressure support"

    plugins.apply("com.github.sgtsilvio.gradle.metadata")

    metadata {
        moduleName = "com.hivemq.client.mqtt"
        readableName = "HiveMQ MQTT Client"
        organization {
            name = "HiveMQ and the HiveMQ Community"
            url = "https://www.hivemq.com/"
        }
        license {
            apache2()
        }
        developers {
            developer {
                id = "SgtSilvio"
                name = "Silvio Giebl"
                email = "silvio.giebl@hivemq.com"
            }
        }
        github {
            org = "hivemq"
            repo = "hivemq-mqtt-client"
            pages()
            issues()
        }
    }
}


/* ******************** java ******************** */

allprojects {
    plugins.withId("java") {
        java {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        plugins.apply("com.github.sgtsilvio.gradle.utf8")
    }
}


/* ******************** dependencies ******************** */

allprojects {
    repositories {
        mavenCentral()
    }
}

dependencies {
    api("io.reactivex.rxjava2:rxjava:${property("rxjava.version")}")
    api("org.reactivestreams:reactive-streams:${property("reactive-streams.version")}")

    implementation("io.netty:netty-buffer:${property("netty.version")}")
    implementation("io.netty:netty-codec:${property("netty.version")}")
    implementation("io.netty:netty-common:${property("netty.version")}")
    implementation("io.netty:netty-handler:${property("netty.version")}")
    implementation("io.netty:netty-transport:${property("netty.version")}")
    implementation("org.jctools:jctools-core:${property("jctools.version")}")
    implementation("org.jetbrains:annotations:${property("annotations.version")}")
    implementation("com.google.dagger:dagger:${property("dagger.version")}")

    compileOnly("org.slf4j:slf4j-api:${property("slf4j.version")}")

    annotationProcessor("com.google.dagger:dagger-compiler:${property("dagger.version")}")
}


/* ******************** optional dependencies ******************** */

listOf("websocket", "proxy", "epoll").forEach {
    java.registerFeature(it) {
        usingSourceSet(sourceSets["main"])
    }
}

dependencies {
    "websocketImplementation"("io.netty:netty-codec-http:${property("netty.version")}")
    "proxyImplementation"("io.netty:netty-handler-proxy:${property("netty.version")}")
    "epollImplementation"("io.netty:netty-transport-native-epoll:${property("netty.version")}:linux-x86_64")
}


/* ******************** test ******************** */

allprojects {
    plugins.withId("java") {
        dependencies {
            testImplementation("org.junit.jupiter:junit-jupiter-api:${property("junit-jupiter.version")}")
            testImplementation("org.junit.jupiter:junit-jupiter-params:${property("junit-jupiter.version")}")
            testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${property("junit-jupiter.version")}")
        }

        tasks.test {
            useJUnitPlatform()
            maxHeapSize = "1g"
            maxParallelForks = Runtime.getRuntime().availableProcessors()
            jvmArgs("-XX:+UseParallelGC")
        }
    }
}

dependencies {
    testImplementation("nl.jqno.equalsverifier:equalsverifier:${property("equalsverifier.version")}")
    testImplementation("org.mockito:mockito-core:${property("mockito.version")}")
    testImplementation("com.google.guava:guava:${property("guava.version")}")
    testImplementation("org.bouncycastle:bcprov-jdk15on:${property("bouncycastle.version")}")
    testImplementation("org.bouncycastle:bcpkix-jdk15on:${property("bouncycastle.version")}")
    testImplementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:${property("paho.version")}")
    testRuntimeOnly("org.slf4j:slf4j-simple:${property("slf4j.version")}")
}


/* ******************** jars ******************** */

allprojects {
    plugins.withId("java-library") {

        plugins.apply("biz.aQute.bnd.builder")

        tasks.jar {
            withConvention(aQute.bnd.gradle.BundleTaskConvention::class) {
                bnd("-consumer-policy: \${range;[==,=+)}", "-removeheaders: Private-Package")
            }
        }

        java {
            withJavadocJar()
            withSourcesJar()
        }

        plugins.apply("com.github.sgtsilvio.gradle.javadoc-links")

        tasks.javadoc {
            exclude("**/internal/**")
        }
    }
}

tasks.jar {
    withConvention(aQute.bnd.gradle.BundleTaskConvention::class) {
        bnd("Export-Package: " +
                "com.hivemq.client.annotations.*," +
                "com.hivemq.client.mqtt.*," +
                "com.hivemq.client.rx.*," +
                "com.hivemq.client.util.*")
    }
}

tasks.shadowJar {
    archiveAppendix.set("shaded")
    archiveClassifier.set("")

    configurations = listOf(project.run {
        configurations.create("shaded") {
            extendsFrom(configurations["runtimeClasspath"])
            configurations["apiElements"].allDependencies.forEach {
                exclude(it.group, it.name)
            }
        }
    })

    val shadePrefix = "com.hivemq.client.internal.shaded."
    val shadeFilePrefix = shadePrefix.replace(".", "_")
    relocate("io.netty", "${shadePrefix}io.netty")
    relocate("META-INF/native/libnetty", "META-INF/native/lib${shadeFilePrefix}netty")
    exclude("META-INF/io.netty.versions.properties")
    relocate("org.jctools", "${shadePrefix}org.jctools")
    relocate("org.jetbrains", "${shadePrefix}org.jetbrains")
    relocate("dagger", "${shadePrefix}dagger")
    relocate("javax.inject", "${shadePrefix}javax.inject")

    minimize()
}

/* ******************** build cache ******************** */
allprojects {
    normalization {
        runtimeClasspath {
            ignore("META-INF/MANIFEST.MF")
        }
    }
}
