package test.app.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

class Analyze :
    CliktCommand(printHelpOnEmptyArgs = true, help = "Capture messages from a topic and analyze the results") {
    private val topic by argument()
    private val brokerAddress by option("-b", "--broker-address", help = "MQTT Broker address").default("127.0.0.1")
    private val numMessages by option("-n", "--num-messages", help = "Number of messages to capture").int().default(100)
    private val qos by option("-q", "--qos", help = "Message qos").int().default(0)
    private val discardStart by option("--discard-start", help = "Number of messages to discard in the start of the message flow").int().default(10)
    private val discardEnd by option("--discard-end", help = "Number of messages to discard in the end of the message flow").int().default(10)
    override fun run() = runBlocking {
        val receiveChannel = Channel<Pair<LocalDateTime, LocalDateTime>>()

        val subscriber = Mqtt5Client.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost(brokerAddress)
            .buildAsync()

        subscriber.connect().whenComplete { _, error ->
            if (error != null) echo("Failed to connect to broker...")
            subscriber.subscribeWith()
                .topicFilter("$topic/#")
                .qos(MqttQos.fromCode(qos) ?: MqttQos.AT_MOST_ONCE)
                .callback {
                    GlobalScope.launch {
                        receiveChannel.send(Pair(LocalDateTime.parse(String(it.payloadAsBytes)), LocalDateTime.now()))
                    }
                }
                .send()
        }

        val received = mutableListOf<Pair<LocalDateTime, LocalDateTime>>()

        repeat(numMessages) {
            echo("Received ${it + 1} of $numMessages messages")
            received.add(receiveChannel.receive())
        }

        val delays = received.drop(discardStart).dropLast(discardEnd).map { Duration.between(it.first, it.second) }
        val sumDelay = delays.fold(Duration.ZERO) { acc, delay -> acc + delay }
        val avgDelay = sumDelay.dividedBy(delays.size.toLong())
        val maxDelay = delays.maxByOrNull { it }

        echo("\nResults:")
        echo("Analyzed ${delays.size} number of messages.")
        echo("Total delay for all messages combined: $sumDelay.")
        echo("Average delay per message: $avgDelay.")
        echo("Longest delay: $maxDelay.")
        echo("\n")
    }
}