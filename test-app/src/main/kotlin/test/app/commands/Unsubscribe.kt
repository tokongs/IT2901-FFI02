package test.app.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import java.util.*

class Unsubscribe : CliktCommand(printHelpOnEmptyArgs = true, help = "Subscribe to MQTT topic filter") {
    private val topicFilter by argument()
    private val clientId by option ("-c", "--client-id", help = "Client identifier").required()
    private val brokerAddress by option("-b", "--broker-address", help = "MQTT Broker address").default("127.0.0.1")

    override fun run() {
        val client = Mqtt5Client.builder()
            .identifier(clientId)
            .serverHost(brokerAddress)
            .buildAsync()

        client.connect()
        client.unsubscribeWith()
            .topicFilter(topicFilter)
            .send()
    }
}