plugins {
    id("java")
    id(("com.github.sgtsilvio.gradle.metadata"))
}


/* ******************** metadata ******************** */

description = "Examples using the HiveMQ MQTT Client"

metadata {
    moduleName = "com.hivemq.client.mqtt.examples"
    readableName = "HiveMQ MQTT Client examples"
}


/* ******************** dependencies ******************** */

dependencies {
    implementation(rootProject)
}
