index 5dd17e8..220e3b7 100644
--- a/broker/src/test/java/com/hivemq/configuration/ioc/ConfigurationModuleTest.java
+++ b/broker/src/test/java/com/hivemq/configuration/ioc/ConfigurationModuleTest.java
@@ -21,7 +21,10 @@ import com.google.inject.Injector;
 import com.hivemq.bootstrap.ioc.SystemInformationModule;
 import com.hivemq.configuration.HivemqId;
 import com.hivemq.configuration.info.SystemInformationImpl;
-import com.hivemq.configuration.service.*;
+import com.hivemq.configuration.service.ConfigurationService;
+import com.hivemq.configuration.service.FullConfigurationService;
+import com.hivemq.configuration.service.MqttConfigurationService;
+import com.hivemq.configuration.service.RestrictionsConfigurationService;
 import com.hivemq.configuration.service.impl.listener.ListenerConfigurationService;
 import com.hivemq.persistence.clientsession.SharedSubscriptionService;
 import org.junit.Before;
@@ -118,14 +121,4 @@ public class ConfigurationModuleTest {
         assertSame(configurationService.mqttConfiguration(), injector.getInstance(MqttConfigurationService.class));
         assertSame(configurationService.restrictionsConfiguration(), injector.getInstance(RestrictionsConfigurationService.class));
     }
-
-    @Test
-    public void test_topic_configuration_service_singleton() throws Exception {
-
-        final TopicPriorityConfigurationService instance = injector.getInstance(TopicPriorityConfigurationService.class);
-        final TopicPriorityConfigurationService instance2 = injector.getInstance(TopicPriorityConfigurationService.class);
-
-        assertSame(instance, instance2);
-        assertSame(testConfigurationBootstrap.getTopicConfigurationService(), instance);
-    }
 }
\ No newline at end of file
