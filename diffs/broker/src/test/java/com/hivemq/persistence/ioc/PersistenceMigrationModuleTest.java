index 11753e7..f99a7e8 100644
--- a/broker/src/test/java/com/hivemq/persistence/ioc/PersistenceMigrationModuleTest.java
+++ b/broker/src/test/java/com/hivemq/persistence/ioc/PersistenceMigrationModuleTest.java
@@ -24,8 +24,6 @@ import com.hivemq.bootstrap.ioc.lazysingleton.LazySingletonScope;
 import com.hivemq.configuration.info.SystemInformation;
 import com.hivemq.configuration.service.MqttConfigurationService;
 import com.hivemq.configuration.service.PersistenceConfigurationService;
-import com.hivemq.configuration.service.TopicPriorityConfigurationService;
-import com.hivemq.mqtt.message.subscribe.Topic;
 import com.hivemq.persistence.PersistenceStartup;
 import com.hivemq.persistence.local.memory.RetainedMessageMemoryLocalPersistence;
 import com.hivemq.persistence.payload.PublishPayloadNoopPersistenceImpl;
@@ -55,9 +53,6 @@ public class PersistenceMigrationModuleTest {
     @Mock
     private PersistenceConfigurationService persistenceConfigurationService;
 
-    @Mock
-    private TopicPriorityConfigurationService topicPriorityConfigurationService;
-
     @Before
     public void setUp() throws Exception {
         MockitoAnnotations.initMocks(this);
@@ -74,7 +69,6 @@ public class PersistenceMigrationModuleTest {
                         bind(SystemInformation.class).toInstance(systemInformation);
                         bindScope(LazySingleton.class, LazySingletonScope.get());
                         bind(MqttConfigurationService.class).toInstance(mqttConfigurationService);
-                        bind(TopicPriorityConfigurationService.class).toInstance(topicPriorityConfigurationService);
                     }
                 });
 
@@ -96,7 +90,6 @@ public class PersistenceMigrationModuleTest {
                         bind(SystemInformation.class).toInstance(systemInformation);
                         bindScope(LazySingleton.class, LazySingletonScope.get());
                         bind(MqttConfigurationService.class).toInstance(mqttConfigurationService);
-                        bind(TopicPriorityConfigurationService.class).toInstance(topicPriorityConfigurationService);
                     }
                 });
         assertTrue(injector.getInstance(PublishPayloadPersistence.class) instanceof PublishPayloadNoopPersistenceImpl);
