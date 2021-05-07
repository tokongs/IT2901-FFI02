index 7f1161b..311c9fd 100644
--- a/client/src/test/java/com/hivemq/client/internal/mqtt/codec/decoder/AbstractMqttDecoderTest.java
+++ b/client/src/test/java/com/hivemq/client/internal/mqtt/codec/decoder/AbstractMqttDecoderTest.java
@@ -111,7 +111,7 @@ public abstract class AbstractMqttDecoderTest {
         final MqttClientConfig clientConfig =
                 new MqttClientConfig(MqttVersion.MQTT_5_0, MqttClientIdentifierImpl.of("test"),
                         MqttClientTransportConfigImpl.DEFAULT, MqttClientExecutorConfigImpl.DEFAULT, advancedConfig,
-                        MqttClientConfig.ConnectDefaults.of(null, null, null), ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
+                        MqttClientConfig.ConnectDefaults.of(null, null, null), ImmutableList.of(), ImmutableList.of());
 
         channel = new EmbeddedChannel();
         channel.pipeline().addLast(new MqttDecoder(decoders, clientConfig, connect)).addLast(disconnectHandler);
