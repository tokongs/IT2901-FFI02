index f126cc3..19f7ce7 100644
--- a/broker/src/test/java/com/hivemq/bootstrap/netty/ChannelDependenciesTest.java
+++ b/broker/src/test/java/com/hivemq/bootstrap/netty/ChannelDependenciesTest.java
@@ -36,7 +36,6 @@ import com.hivemq.mqtt.handler.ping.PingRequestHandler;
 import com.hivemq.mqtt.handler.publish.MessageExpiryHandler;
 import com.hivemq.mqtt.handler.subscribe.SubscribeHandler;
 import com.hivemq.mqtt.handler.unsubscribe.UnsubscribeHandler;
-import com.hivemq.mqtt.topic.TopicMatcher;
 import com.hivemq.security.ssl.SslParameterHandler;
 import io.netty.channel.group.ChannelGroup;
 import io.netty.handler.traffic.GlobalTrafficShapingHandler;
@@ -140,9 +139,6 @@ public class ChannelDependenciesTest {
     @Mock
     private GlobalMQTTMessageCounter globalMQTTMessageCounter;
 
-    @Mock
-    private TopicMatcher topicMatcher;
-
     @Before
     public void setUp() throws Exception {
 
@@ -176,8 +172,7 @@ public class ChannelDependenciesTest {
                 () -> messageExpiryHandler,
                 mqttServerDisconnector,
                 interceptorHandler,
-                globalMQTTMessageCounter,
-                topicMatcher);
+                globalMQTTMessageCounter);
 
     }
 
