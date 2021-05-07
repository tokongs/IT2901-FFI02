index d9befef..2cc7720 100644
--- a/broker/src/test/java/com/hivemq/configuration/reader/ConfigFileReaderTest.java
+++ b/broker/src/test/java/com/hivemq/configuration/reader/ConfigFileReaderTest.java
@@ -20,7 +20,10 @@ import com.hivemq.configuration.entity.PersistenceEntity;
 import com.hivemq.configuration.entity.RestrictionsEntity;
 import com.hivemq.configuration.entity.SecurityConfigEntity;
 import com.hivemq.configuration.info.SystemInformation;
-import com.hivemq.configuration.service.*;
+import com.hivemq.configuration.service.MqttConfigurationService;
+import com.hivemq.configuration.service.PersistenceConfigurationService;
+import com.hivemq.configuration.service.RestrictionsConfigurationService;
+import com.hivemq.configuration.service.SecurityConfigurationService;
 import com.hivemq.configuration.service.impl.listener.ListenerConfigurationService;
 import com.hivemq.configuration.service.impl.listener.ListenerConfigurationServiceImpl;
 import com.hivemq.mqtt.message.QoS;
@@ -59,12 +62,6 @@ public class ConfigFileReaderTest {
 
     private ListenerConfigurationService listenerConfigurationService;
 
-    /**
-     * Added by Christoffer Stensrud
-     */
-    @Mock
-    private TopicPriorityConfigurationService topicPriorityConfigurationService;
-
     ConfigFileReader reader;
 
     @Before
@@ -81,12 +78,7 @@ public class ConfigFileReaderTest {
                 new UsageStatisticsConfigurator(usageStatisticsConfig),
                 new MqttConfigurator(mqttConfigurationService),
                 new ListenerConfigurator(listenerConfigurationService, systemInformation),
-                new PersistenceConfigurator(persistenceConfigurationService),
-
-                /**
-                 * Added by Christoffer Stensrud
-                 */
-                new TopicPriorirtyConfigurator(topicPriorityConfigurationService));
+                new PersistenceConfigurator(persistenceConfigurationService));
     }
 
     @Test
