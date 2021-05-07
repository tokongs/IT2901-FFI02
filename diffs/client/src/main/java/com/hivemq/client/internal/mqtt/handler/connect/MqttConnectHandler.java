index 5362443..ff5d470 100644
--- a/client/src/main/java/com/hivemq/client/internal/mqtt/handler/connect/MqttConnectHandler.java
+++ b/client/src/main/java/com/hivemq/client/internal/mqtt/handler/connect/MqttConnectHandler.java
@@ -164,7 +164,7 @@ public class MqttConnectHandler extends MqttTimeoutInboundHandler {
 
             channel.pipeline().remove(this);
 
-            ((MqttEncoder) channel.pipeline().get(MqttEncoder.NAME)).onConnected(connectionConfig, clientConfig);
+            ((MqttEncoder) channel.pipeline().get(MqttEncoder.NAME)).onConnected(connectionConfig);
 
             session.startOrResume(connAck, connectionConfig, channel.pipeline(), channel.eventLoop());
 
