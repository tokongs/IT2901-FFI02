package test.app.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.hivemq.client.extensions.PriorityClass
import com.hivemq.client.extensions.TopicPriority
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.datatypes.MqttTopicFilter
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import java.util.*

class Publish : CliktCommand(printHelpOnEmptyArgs = true, help = "Publish messages to MQTT") {

    private val message by argument()
    private val clientId by option("-c", "--client-id", help = "Client identifier").default(
        UUID.randomUUID().toString()
    )
    private val brokerAddress by option("-b", "--broker-address", help = "MQTT Broker address").default("127.0.0.1")
    private val topic by option("-t", "--topic", help = "The topic to publish to").default("")
    private val qos by option("-q", "--qos", help = "Message qos").int().default(0)
    private val retain by option("-r", "--retain", help = "Wether to retain the message or not").flag(
        "-R",
        "--no-retain",
        default = false
    )
    private val topicPriorities by option("-p", "--topic-priority").pair().multiple()

    override fun run() {
        val client = Mqtt5Client.builder()
            .identifier(clientId)
            .serverHost(brokerAddress).apply {
                topicPriorities.forEach {
                    addTopicPriority(
                        TopicPriority(
                            MqttTopicFilter.of(it.first),
                            PriorityClass.valueOf(it.second.capitalize())
                        )
                    )
                }
            }.buildBlocking()

        client.connect()
        client.publishWith()
            .topic(topic)
            .payload(message.toByteArray())
            .retain(retain)
            .qos(MqttQos.fromCode(qos) ?: MqttQos.AT_MOST_ONCE).send()
        client.disconnect()
    }
}