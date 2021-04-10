package test.app.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.options.flag
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import test.app.functions.*
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ExperimentSuite : CliktCommand( printHelpOnEmptyArgs = true, help = "Automated run of test suite along custom parameters") {
    private val brokerAddress: String by option("-b", "--broker-address", help = "MQTT Broker address").default("127.0.0.1")
    private val topic: List<String> by option("-t", "--topic", help = "The topic to publish to. Can be specified multiple times").multiple()
    private val output: String by option("-o", "--output", help = "File to save data").default( "./log.txt" )
    private val dimensions: List<String> by option("-d", "-dims", help = "which dimensions to test, defaults to delay against topic priority").multiple()
    private val numMessages: Int by option("-n", "--num-messages", help = "Number of messages to send").int().default(100)
    private val length: Int by option("-l", "-length", help = "Length of each message").int().default(144)
    private val qos: Int by option("-q", "--qos", help = "Message qos").int().default(0)

    private fun randomPayload(n: Int): String {
        val chars = ('a' .. 'z') + ('A' .. 'Z') + ('0' .. '9')
        return List(n) { chars.random() }.joinToString("")
    }

    public override fun run() = runBlocking {
        echo("Running tests...")

        val received = ConcurrentHashMap(topic.associateBy({it}, { mutableListOf<Pair<LocalDateTime, LocalDateTime>>()}))

        val subscriber = Mqtt5Client.builder().identifier(UUID.randomUUID().toString())
            .serverHost(brokerAddress).buildAsync()
        subscriber.connect()

        // Setup subscriptions
        topic.forEach {
            subscriber.subscribeWith()
                .topicFilter(it)
                .qos(MqttQos.fromCode(qos) ?: MqttQos.AT_MOST_ONCE)
                .callback{message ->
                    received[it]?.add(Pair(LocalDateTime.parse(String(message.payloadAsBytes)), LocalDateTime.now()!!))
                }.send()
        }

        // Send messages with a coroutine per topic.
        echo("Sending messages...")
        topic.map{ topic ->
            launch(Dispatchers.Default) {
                val publisher = Mqtt5Client.builder()
                    .identifier(UUID.randomUUID().toString())
                    .serverHost(brokerAddress)
                    .buildBlocking()

                publisher.connect()
                repeat(numMessages) {
                    val synthPayload = randomPayload(length).toByteArray()
                    publisher.publishWith().topic(topic).payload(synthPayload)
                        .qos(MqttQos.fromCode(qos) ?: MqttQos.AT_MOST_ONCE).send()
                }
                publisher.disconnect()
            }
        }.forEach{ it.join() }


    }
}