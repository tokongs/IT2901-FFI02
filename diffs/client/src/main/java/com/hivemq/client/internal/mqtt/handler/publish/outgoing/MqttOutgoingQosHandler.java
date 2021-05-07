index e63169c..7eb3e53 100644
--- a/client/src/main/java/com/hivemq/client/internal/mqtt/handler/publish/outgoing/MqttOutgoingQosHandler.java
+++ b/client/src/main/java/com/hivemq/client/internal/mqtt/handler/publish/outgoing/MqttOutgoingQosHandler.java
@@ -16,7 +16,6 @@
 
 package com.hivemq.client.internal.mqtt.handler.publish.outgoing;
 
-import com.hivemq.client.extensions.TopicPriority;
 import com.hivemq.client.internal.annotations.CallByThread;
 import com.hivemq.client.internal.logging.InternalLogger;
 import com.hivemq.client.internal.logging.InternalLoggerFactory;
@@ -50,7 +49,6 @@ import com.hivemq.client.internal.util.collections.IntIndex;
 import com.hivemq.client.internal.util.collections.NodeList;
 import com.hivemq.client.mqtt.MqttClientState;
 import com.hivemq.client.mqtt.datatypes.MqttQos;
-import com.hivemq.client.mqtt.datatypes.MqttTopic;
 import com.hivemq.client.mqtt.exceptions.ConnectionClosedException;
 import com.hivemq.client.mqtt.mqtt5.advanced.interceptor.qos1.Mqtt5OutgoingQos1Interceptor;
 import com.hivemq.client.mqtt.mqtt5.advanced.interceptor.qos2.Mqtt5OutgoingQos2Interceptor;
@@ -214,7 +212,6 @@ public class MqttOutgoingQosHandler extends MqttSessionAwareHandler
             if (publishWithFlow == null) {
                 break;
             }
-
             writePublish(ctx, publishWithFlow);
             written++;
             dequeued++;
