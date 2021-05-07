index 50b9fdc..957eed5 100644
--- a/broker/src/test/java/com/hivemq/extensions/ioc/ExtensionModuleTest.java
+++ b/broker/src/test/java/com/hivemq/extensions/ioc/ExtensionModuleTest.java
@@ -51,7 +51,6 @@ import com.hivemq.mqtt.message.dropping.MessageDroppedService;
 import com.hivemq.mqtt.services.InternalPublishService;
 import com.hivemq.mqtt.services.PublishDistributor;
 import com.hivemq.mqtt.services.PublishPollService;
-import com.hivemq.mqtt.topic.TopicMatcher;
 import com.hivemq.mqtt.topic.tree.LocalTopicTree;
 import com.hivemq.mqtt.topic.tree.TopicTreeImpl;
 import com.hivemq.persistence.ChannelPersistence;
@@ -125,7 +124,6 @@ public class ExtensionModuleTest {
                 bindScope(LazySingleton.class, LazySingletonScope.get());
                 bind(MqttServerDisconnector.class).toInstance(mock(MqttServerDisconnector.class));
                 bind(MqttConnacker.class).toInstance(mock(MqttConnacker.class));
-                bind(TopicMatcher.class).toInstance(mock(TopicMatcher.class));
             }
         });
     }
