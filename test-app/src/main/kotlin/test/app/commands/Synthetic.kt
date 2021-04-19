package test.app.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit


class Synthetic : CliktCommand(printHelpOnEmptyArgs = true, help = "Put a syntethic load on the broker") {
    private val brokerAddress by option("-b", "--broker-address", help = "MQTT Broker address").default("127.0.0.1")
    private val topic: List<String> by option(
        "-t",
        "--topic",
        help = "The topic to publish to. Can be specified multiple times"
    ).multiple()
    private val numMessages by option("-n", "--num-messages", help = "Number of messages to send").int().default(100)
    private val numPublishersPerTopic by option("-p", "--num-publishers-per-topic", help = "Number of clients to build per topic").int().default(10)
    private val timeout by option("--timeout", help = "seconds to wait for message receive").int().default(10)
    private val qos by option("-q", "--qos", help = "Message qos").int().default(0)

    override fun run() = runBlocking {
        val received =
            ConcurrentHashMap(topic.associateBy({ it }, { mutableListOf<Pair<LocalDateTime, LocalDateTime>>() }))

        val subscriber = Mqtt5Client.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost(brokerAddress)
            .buildAsync()

        subscriber.connect()



        echo("Setting up subscriptions")
        // Setup subscriptions
        topic.forEach { topic ->
            subscriber.subscribeWith()
                .topicFilter("$topic/#")
                .qos(MqttQos.fromCode(qos) ?: MqttQos.AT_MOST_ONCE)
                .callback{
                    received[topic]?.add(Pair(LocalDateTime.parse(String(it.payloadAsBytes)), LocalDateTime.now()))
                }
                .send()
        }



        echo("Connecting publishers")
        val publishers = topic.associateBy({ it }) {
            (1..numPublishersPerTopic).map {
                val publisher = Mqtt5Client.builder()
                    .identifier(UUID.randomUUID().toString())
                    .serverHost(brokerAddress)
                    .buildBlocking()

                publisher.connect()
                publisher
            }
        }

        topic.map { topic ->
                launch(Dispatchers.Default) {
                    publishers[topic]!!.forEach{ publisher ->
                        repeat(numMessages) {
                            publisher.publishWith()
                                .topic(topic)
                                .payload("${LocalDateTime.now()}".toByteArray())
                                .qos(MqttQos.fromCode(qos) ?: MqttQos.AT_MOST_ONCE)
                                .send()
                        }
                        publisher.disconnect()
                    }
                }
        }

        echo("Waiting for messages to be received")
        delay(timeout.toLong() * 1000)

        // Analyze the results
        echo("Analyzing...\n")
        received.forEach { (topic, messages) ->
            echo("Results for $topic:")
            val sumDelay = messages.fold(Duration.ZERO) { acc, message ->
                acc + Duration.between(message.first, message.second)
            }
            val avgDelay = sumDelay.dividedBy(messages.size.toLong())
            val maxDelay = messages.map { message -> Duration.between(message.first, message.second)}.maxOrNull()
            val numDroppedMessages = numMessages * numPublishersPerTopic - messages.size
            echo("Total number of messages received ${messages.size}")
            echo("Total delay for all messages combined: $sumDelay.")
            echo("Average delay per message: $avgDelay.")
            echo("Longest delay: $maxDelay.")
            echo("Number of dropped messages $numDroppedMessages")

            echo("\n")
        }
    }
}
