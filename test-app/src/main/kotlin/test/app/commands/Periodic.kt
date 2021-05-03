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
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*

class Periodic: CliktCommand(printHelpOnEmptyArgs = true, help = "Publish messages periodically") {

    private val message by argument()
    private val brokerAddress by option("-b", "--broker-address", help = "MQTT Broker address").default("127.0.0.1")
    private val topic by option("-t", "--topic", help = "The topic to publish to").default("")
    private val qos by option("-q", "--qos", help = "Message qos").int().default(0)
    private val topicPriorities by option("-p", "--topic-priority").pair().multiple()
    private val interval by option("-i", "--interval").int().default(1000)
    private val numMessages by option("-n", "--num-messages").int().default(1000)

    override fun run(): Unit = runBlocking {
        val client = Mqtt5Client.builder()
            .identifier(UUID.randomUUID().toString())
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
        repeat(numMessages){
            client.publishWith()
                .topic(topic)
                .payload(message.toByteArray())
                .qos(MqttQos.fromCode(qos) ?: MqttQos.AT_MOST_ONCE)
                .send()
            echo("Sent ${it+1} of $numMessages messages")
            delay(interval.toLong())
        }

        client.disconnect()
    }
}