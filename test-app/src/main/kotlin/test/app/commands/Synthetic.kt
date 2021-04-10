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
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Synthetic : CliktCommand(printHelpOnEmptyArgs = true, help = "Put a synthetic load on the broker") {
    private val brokerAddress: String by option("-b", "--broker-address", help = "MQTT Broker address").default("127.0.0.1")
    private val topic: List<String> by option("-t", "--topic", help = "The topic to publish to. Can be specified multiple times").multiple()
    private val numMessages: Int by option("-n", "--num-messages", help = "Number of messages to send").int().default(100)
    private val length: Int by option("-l", "-length", help = "Length of each message").int().default(144)
    private val random: Boolean by option("-r", "--random", help = "Whether to send randomized payload or not").flag(default = false)
    private val qos: Int by option("-q", "--qos", help = "Message qos").int().default(0)
    private val save: Boolean by option("-s", "--save", help = "Whether to save or not").flag(default = false)
    private val output: String by option("-o", "--output", help = "File to save data").default( "./log.txt" )

    private fun randomPayload(n: Int): String {
        val chars = ('a' .. 'z') + ('A' .. 'Z') + ('0' .. '9')
        return List(n) { chars.random() }.joinToString("")
    }

    override fun run() = runBlocking {
        echo("Initializing...")

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
                    val synthPayload = (if (random) randomPayload(length) else "${LocalDateTime.now()}").toByteArray()
                    publisher.publishWith().topic(topic).payload(synthPayload)
                        .qos(MqttQos.fromCode(qos) ?: MqttQos.AT_MOST_ONCE).send()
                }
                publisher.disconnect()
            }
        }.forEach{ it.join() }

        // Analyze the results
        echo("Analyzing...")
        received.forEach { (topic, messages) ->
            echo("Results for $topic:")
            val sumDelay = messages.fold(Duration.ZERO) { acc, (m1, m2) ->
                acc + Duration.between(m1, m2)
            }
            val avgDelay = sumDelay.dividedBy(messages.size.toLong())
            val maxDelay = messages.maxByOrNull {
                message -> Duration.between(message.first, message.second)
            }
            val minDelay = messages.maxByOrNull {
                    message -> Duration.between(message.first, message.second)
            }

            val stats = "Total delay for all messages combined: $sumDelay." +
                        "Average delay per message: $avgDelay." +
                        "Highest recorded delay: $maxDelay." +
                        "Lowest recorded delay: $minDelay."

            if (save) {
                File(output).writeText(stats)
            }

            echo(stats)

        }
    }
}
