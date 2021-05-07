deleted file mode 100644
index b2dab79..0000000
--- a/test-app/src/main/kotlin/test/app/commands/Subscribe.kt
+++ /dev/null
@@ -1,33 +0,0 @@
-package test.app.commands
-
-import com.github.ajalt.clikt.core.CliktCommand
-import com.github.ajalt.clikt.parameters.arguments.argument
-import com.github.ajalt.clikt.parameters.arguments.default
-import com.github.ajalt.clikt.parameters.options.default
-import com.github.ajalt.clikt.parameters.options.option
-import com.github.ajalt.clikt.parameters.types.int
-import com.hivemq.client.mqtt.datatypes.MqttQos
-import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
-import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
-import java.util.*
-
-class Subscribe : CliktCommand(printHelpOnEmptyArgs = true, help = "Subscribe to MQTT topic filter") {
-    private val topicFilter by argument()
-    private val clientId by option ("-c", "--client-id", help = "Client identifier").default(UUID.randomUUID().toString())
-    private val brokerAddress by option("-b", "--broker-address", help = "MQTT Broker address").default("127.0.0.1")
-    private val qos by option("-q", "--qos", help = "Message qos").int().default(0)
-
-    override fun run() {
-        val client = Mqtt5Client.builder()
-            .identifier(clientId)
-            .serverHost(brokerAddress)
-            .buildAsync()
-
-        client.connect()
-        client.subscribeWith()
-            .topicFilter(topicFilter)
-            .qos(MqttQos.fromCode(qos) ?: MqttQos.AT_MOST_ONCE)
-            .callback {println("${it.topic}: ${String(it.payloadAsBytes)}")}
-            .send()
-    }
-}
\ No newline at end of file
