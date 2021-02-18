/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package test.app

import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import java.util.*

fun main(args: Array<String>) {

    val client = Mqtt5Client.builder()
        .identifier(UUID.randomUUID().toString())
        .serverHost("165.22.126.152")
        .buildBlocking()

    client.connect()
    // Will have DSCP value 0x10
    client.publishWith().topic("testtopic/1").payload("Hei".toByteArray()).qos(MqttQos.AT_MOST_ONCE).send()

    // Will have DSCP value 0x0
    client.publishWith().topic("nottesttopic/1").payload("Hei".toByteArray()).qos(MqttQos.AT_MOST_ONCE).send()
}