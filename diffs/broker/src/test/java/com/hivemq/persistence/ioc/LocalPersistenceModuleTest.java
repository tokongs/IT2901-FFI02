index e925fcd..d666b06 100644
--- a/broker/src/test/java/com/hivemq/persistence/ioc/LocalPersistenceModuleTest.java
+++ b/broker/src/test/java/com/hivemq/persistence/ioc/LocalPersistenceModuleTest.java
@@ -101,9 +101,6 @@ public class LocalPersistenceModuleTest {
     @Mock
     private PersistenceConfigurationService persistenceConfigurationService;
 
-    @Mock
-    private TopicPriorityConfigurationService topicPriorityConfigurationService;
-
     @Mock
     private Injector persistenceInjector;
 
@@ -231,7 +228,6 @@ public class LocalPersistenceModuleTest {
                         bind(RestrictionsConfigurationService.class).toInstance(new RestrictionsConfigurationServiceImpl());
                         bind(MqttConfigurationService.class).toInstance(mqttConfigurationService);
                         bind(MqttServerDisconnector.class).toInstance(mock(MqttServerDisconnector.class));
-                        bind(TopicPriorityConfigurationService.class).toInstance(topicPriorityConfigurationService);
                     }
                 });
     }
