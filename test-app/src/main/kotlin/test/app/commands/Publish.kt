package test.app.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import java.util.*

class Publish : CliktCommand(printHelpOnEmptyArgs = true, help = "Publish messages to MQTT") {
    private val brokerAddress by option("-b", "--broker-address", help = "MQTT Broker address").default("127.0.0.1")
    private val topic by option("-t", "--topic", help = "The topic to publish to").default("")
    private val message by option("-m", "--message", help = "Message payload to send").default("")
    private val qos by option("-q", "--qos", help = "Message qos").int().default(0)

    override fun run() {
        val client = Mqtt5Client.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost(brokerAddress)
            .buildBlocking()

        client.connect()
        client.publishWith().topic(topic).payload(message.toByteArray())
            .qos(MqttQos.fromCode(qos) ?: MqttQos.AT_MOST_ONCE).send()
        client.disconnect()
    }
}