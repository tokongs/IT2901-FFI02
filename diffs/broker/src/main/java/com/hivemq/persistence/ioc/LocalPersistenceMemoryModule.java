index 9d2d275..4f873c1 100644
--- a/broker/src/main/java/com/hivemq/persistence/ioc/LocalPersistenceMemoryModule.java
+++ b/broker/src/main/java/com/hivemq/persistence/ioc/LocalPersistenceMemoryModule.java
@@ -19,8 +19,6 @@ import com.google.inject.Injector;
 import com.hivemq.bootstrap.ioc.SingletonModule;
 import com.hivemq.extension.sdk.api.annotations.NotNull;
 import com.hivemq.extension.sdk.api.annotations.Nullable;
-import com.hivemq.mqtt.topic.TokenizedTopicMatcher;
-import com.hivemq.mqtt.topic.TopicMatcher;
 import com.hivemq.persistence.clientqueue.ClientQueueLocalPersistence;
 import com.hivemq.persistence.local.ClientSessionLocalPersistence;
 import com.hivemq.persistence.local.ClientSessionSubscriptionLocalPersistence;
@@ -29,7 +27,6 @@ import com.hivemq.persistence.local.memory.ClientSessionMemoryLocalPersistence;
 import com.hivemq.persistence.local.memory.ClientSessionSubscriptionMemoryLocalPersistence;
 import com.hivemq.persistence.local.memory.RetainedMessageMemoryLocalPersistence;
 import com.hivemq.persistence.retained.RetainedMessageLocalPersistence;
-import com.hivemq.util.PublishComparator;
 
 import javax.inject.Singleton;
 
@@ -61,7 +58,6 @@ class LocalPersistenceMemoryModule extends SingletonModule<Class<LocalPersistenc
         bindLocalPersistence(ClientQueueLocalPersistence.class,
                 ClientQueueMemoryLocalPersistence.class);
 
-        bind(PublishComparator.class);
     }
 
     private void bindLocalPersistence(final @NotNull Class localPersistenceClass,
