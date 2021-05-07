index d16933d..0c187e5 100644
--- a/broker/src/test/java/com/hivemq/persistence/local/memory/ClientQueueMemoryLocalPersistenceTest.java
+++ b/broker/src/test/java/com/hivemq/persistence/local/memory/ClientQueueMemoryLocalPersistenceTest.java
@@ -32,7 +32,6 @@ import com.hivemq.persistence.local.memory.ClientQueueMemoryLocalPersistence.Pub
 import com.hivemq.persistence.local.xodus.bucket.BucketUtils;
 import com.hivemq.persistence.payload.PublishPayloadPersistence;
 import com.hivemq.util.ObjectMemoryEstimation;
-import com.hivemq.util.PublishComparator;
 import org.apache.commons.lang3.RandomStringUtils;
 import org.junit.After;
 import org.junit.Before;
@@ -42,7 +41,6 @@ import org.junit.rules.TemporaryFolder;
 import org.mockito.Mock;
 import org.mockito.MockitoAnnotations;
 
-import java.util.Arrays;
 import java.util.Set;
 import java.util.stream.Collectors;
 
@@ -68,10 +66,6 @@ public class ClientQueueMemoryLocalPersistenceTest {
     @Mock
     private MessageDroppedService messageDroppedService;
 
-    @Mock
-    private PublishComparator publishComparator;
-
-
     private ClientQueueMemoryLocalPersistence persistence;
 
     private final int bucketCount = 4;
@@ -91,7 +85,7 @@ public class ClientQueueMemoryLocalPersistenceTest {
         metricRegistry = new MetricRegistry();
         persistence = new ClientQueueMemoryLocalPersistence(
                 payloadPersistence,
-                messageDroppedService, metricRegistry, publishComparator);
+                messageDroppedService, metricRegistry);
     }
 
     @After
@@ -114,6 +108,62 @@ public class ClientQueueMemoryLocalPersistenceTest {
         assertEquals(publish.getTopic(), publishes.get(0).getTopic());
     }
 
+    @Test
+    public void test_readNew_moreAvailable() {
+        final PUBLISH[] publishes = new PUBLISH[4];
+        for (int i = 0; i < publishes.length; i++) {
+            publishes[i] = createPublish(10 + i, (i % 2 == 0) ? QoS.EXACTLY_ONCE : QoS.AT_LEAST_ONCE, "topic" + i);
+        }
+        final PUBLISH otherPublish = createPublish(14, QoS.EXACTLY_ONCE, "topic5");
+
+        persistence.add("client10", false, otherPublish, 100L, DISCARD, false, 0);
+        for (final PUBLISH publish : publishes) {
+            persistence.add("client1", false, publish, 100L, DISCARD, false, 0);
+        }
+        persistence.add("client01", false, otherPublish, 100L, DISCARD, false, 0);
+
+        final ImmutableIntArray packetIds = ImmutableIntArray.of(2, 3, 5);
+        final ImmutableList<PUBLISH> readPublishes = persistence.readNew("client1", false, packetIds, 256000, 0);
+
+        assertEquals(3, readPublishes.size());
+        for (int i = 0; i < packetIds.length(); i++) {
+            assertEquals(packetIds.get(i), readPublishes.get(i).getPacketIdentifier());
+            assertEquals(publishes[i].getQoS(), readPublishes.get(i).getQoS());
+            assertEquals(publishes[i].getTopic(), readPublishes.get(i).getTopic());
+        }
+    }
+
+    @Test
+    public void test_readNew_twice() {
+        final PUBLISH[] publishes = new PUBLISH[4];
+        for (int i = 0; i < publishes.length; i++) {
+            publishes[i] = createPublish(10 + i, (i % 2 == 0) ? QoS.EXACTLY_ONCE : QoS.AT_LEAST_ONCE, "topic" + i);
+        }
+        final PUBLISH otherPublish = createPublish(14, QoS.EXACTLY_ONCE, "topic5");
+
+        persistence.add("client10", false, otherPublish, 100L, DISCARD, false, 0);
+        for (final PUBLISH publish : publishes) {
+            persistence.add("client1", false, publish, 100L, DISCARD, false, 0);
+        }
+        persistence.add("client01", false, otherPublish, 100L, DISCARD, false, 0);
+
+        final ImmutableList<PUBLISH> messages1 =
+                persistence.readNew("client1", false, ImmutableIntArray.of(5), 256000, 0);
+
+        assertEquals(1, messages1.size());
+        assertEquals(5, messages1.get(0).getPacketIdentifier());
+        assertEquals("topic0", messages1.get(0).getTopic());
+
+        final ImmutableIntArray packetIds = ImmutableIntArray.of(2, 3, 4);
+        final ImmutableList<PUBLISH> messages2 = persistence.readNew("client1", false, packetIds, 256000, 0);
+
+        assertEquals(3, messages2.size());
+        for (int i = 0; i < packetIds.length(); i++) {
+            assertEquals(packetIds.get(i), messages2.get(i).getPacketIdentifier());
+            assertEquals(publishes[1 + i].getTopic(), messages2.get(i).getTopic());
+        }
+    }
+
     @Test
     public void test_readNew_qos0() {
         final PUBLISH[] publishes = new PUBLISH[4];
@@ -129,8 +179,7 @@ public class ClientQueueMemoryLocalPersistenceTest {
         assertEquals(1, persistence.size("client", false, 0));
         assertEquals(3, messages.size());
         for (int i = 0; i < 3; i++) {
-            int finalI = i;
-            assertTrue(Arrays.stream(publishes).anyMatch(x -> x.getTopic() == messages.get(finalI).getTopic()));
+            assertEquals(publishes[i].getTopic(), messages.get(i).getTopic());
         }
     }
 
@@ -156,6 +205,19 @@ public class ClientQueueMemoryLocalPersistenceTest {
         assertEquals(3, persistence.size("client", false, 0));
         assertEquals(6, messages.size());
 
+        assertEquals(0, messages.get(1).getPacketIdentifier());
+        assertEquals(QoS.AT_MOST_ONCE, messages.get(1).getQoS());
+        assertEquals(0, messages.get(3).getPacketIdentifier());
+        assertEquals(QoS.AT_MOST_ONCE, messages.get(3).getQoS());
+        assertEquals(0, messages.get(5).getPacketIdentifier());
+        assertEquals(QoS.AT_MOST_ONCE, messages.get(5).getQoS());
+
+        assertEquals(1, messages.get(0).getPacketIdentifier());
+        assertEquals(QoS.AT_LEAST_ONCE, messages.get(0).getQoS());
+        assertEquals(2, messages.get(2).getPacketIdentifier());
+        assertEquals(QoS.AT_LEAST_ONCE, messages.get(2).getQoS());
+        assertEquals(3, messages.get(4).getPacketIdentifier());
+        assertEquals(QoS.AT_LEAST_ONCE, messages.get(4).getQoS());
     }
 
     @Test
@@ -177,6 +239,54 @@ public class ClientQueueMemoryLocalPersistenceTest {
         assertEquals(7, messages1.get(2).getPacketIdentifier());
     }
 
+    @Test
+    public void test_read_inflight_pubrel() {
+        final PUBREL[] pubrels = new PUBREL[4];
+        for (int i = 0; i < pubrels.length; i++) {
+            pubrels[i] = new PUBREL(i + 1);
+        }
+        for (final PUBREL pubrel : pubrels) {
+            persistence.add("client1", false, createPublish(pubrel.getPacketIdentifier(), QoS.EXACTLY_ONCE, "topic"), 100L, DISCARD, false, 0);
+            persistence.replace("client1", pubrel, 0);
+        }
+
+        final ImmutableList<MessageWithID> messages2 = persistence.readInflight("client1", false, 10, 256000, 0);
+        assertEquals(4, messages2.size());
+    }
+
+    @Test
+    public void test_read_inflight_pubrel_and_publish() {
+        final PUBREL[] pubrels = new PUBREL[4];
+        for (int i = 0; i < pubrels.length; i++) {
+            pubrels[i] = new PUBREL(i + 1);
+        }
+        for (final PUBREL pubrel : pubrels) {
+            persistence.add("client1", false, createPublish(pubrel.getPacketIdentifier(), QoS.EXACTLY_ONCE, "topic"), 100L, DISCARD, false, 0);
+            persistence.replace("client1", pubrel, 0);
+        }
+        final PUBLISH[] publishes = new PUBLISH[4];
+        for (int i = 0; i < publishes.length; i++) {
+            publishes[i] = createPublish(10 + i, (i % 2 == 0) ? QoS.EXACTLY_ONCE : QoS.AT_LEAST_ONCE, "topic" + i);
+        }
+        for (final PUBLISH publish : publishes) {
+            persistence.add("client1", false, publish, 100L, DISCARD, false, 0);
+        }
+
+        // Assign packet ID's
+        persistence.readNew("client1", false, ImmutableIntArray.of(1, 2, 3, 4), 256000, 0);
+
+        final ImmutableList<MessageWithID> messages = persistence.readInflight("client1", false, 10, 256000, 0);
+        assertEquals(8, messages.size());
+        assertTrue(messages.get(0) instanceof PUBREL);
+        assertTrue(messages.get(1) instanceof PUBREL);
+        assertTrue(messages.get(2) instanceof PUBREL);
+        assertTrue(messages.get(3) instanceof PUBREL);
+        assertTrue(messages.get(4) instanceof PUBLISH);
+        assertTrue(messages.get(5) instanceof PUBLISH);
+        assertTrue(messages.get(6) instanceof PUBLISH);
+        assertTrue(messages.get(7) instanceof PUBLISH);
+    }
+
     @Test
     public void test_add_discard() {
         for (int i = 1; i <= 6; i++) {
@@ -195,6 +305,21 @@ public class ClientQueueMemoryLocalPersistenceTest {
         verify(messageDroppedService, times(3)).queueFull(eq("client"), anyString(), anyInt());
     }
 
+    @Test
+    public void test_add_discard_oldest() {
+        for (int i = 1; i <= 6; i++) {
+            persistence.add(
+                    "client", false, createPublish(i, QoS.AT_LEAST_ONCE, "topic" + i), 3L, DISCARD_OLDEST, false, 0);
+        }
+        assertEquals(3, persistence.size("client", false, 0));
+        final ImmutableList<PUBLISH> publishes =
+                persistence.readNew("client", false, ImmutableIntArray.of(1, 2, 3, 4, 5, 6), byteLimit, 0);
+        assertEquals(3, publishes.size());
+        assertEquals("topic4", publishes.get(0).getTopic());
+        assertEquals("topic5", publishes.get(1).getTopic());
+        assertEquals("topic6", publishes.get(2).getTopic());
+        verify(messageDroppedService, times(3)).queueFull(eq("client"), anyString(), anyInt());
+    }
 
     @Test
     public void test_clear() {
@@ -215,6 +340,32 @@ public class ClientQueueMemoryLocalPersistenceTest {
         assertEquals(1, publishes2.size());
     }
 
+    @Test
+    public void test_replace() {
+        for (int i = 0; i < 3; i++) {
+            persistence.add("client", false, createPublish(1, QoS.AT_LEAST_ONCE, "topic", i), 100L, DISCARD, false, 0);
+        }
+        persistence.readNew("client", false, ImmutableIntArray.of(2, 3, 4), 256000, 0);
+        final String uniqueId = persistence.replace("client", new PUBREL(4), 0);
+        assertEquals("hivemqId_pub_2", uniqueId);
+        final ImmutableList<MessageWithID> messages = persistence.readInflight("client", false, 10, byteLimit, 0);
+        assertTrue(messages.get(2) instanceof PUBREL);
+    }
+
+    @Test
+    public void test_replace_pubrel() {
+        for (int i = 0; i < 3; i++) {
+            persistence.add("client", false, createPublish(1, QoS.AT_LEAST_ONCE, "topic", i), 100L, DISCARD, false, 0);
+        }
+        persistence.readNew("client", false, ImmutableIntArray.of(2, 3, 4), 256000, 0);
+        String uniqueId = persistence.replace("client", new PUBREL(4), 0);
+        assertEquals("hivemqId_pub_2", uniqueId);
+        uniqueId = persistence.replace("client", new PUBREL(4), 0);
+        assertNull(uniqueId);
+        final ImmutableList<MessageWithID> messages = persistence.readInflight("client", false, 10, byteLimit, 0);
+        assertTrue(messages.get(2) instanceof PUBREL);
+    }
+
     @Test
     public void test_replca_false_id() {
         persistence.add("client", false, createPublish(1, QoS.AT_LEAST_ONCE, "topic", 1), 100L, DISCARD, false, 0);
@@ -226,6 +377,34 @@ public class ClientQueueMemoryLocalPersistenceTest {
         assertEquals(1, messages.get(0).getPacketIdentifier());
     }
 
+    @Test
+    public void test_replace_not_found() {
+        for (int i = 0; i < 3; i++) {
+            persistence.add("client", false, createPublish(1, QoS.AT_LEAST_ONCE, "topic", i), 100L, DISCARD, false, 0);
+        }
+        final String uniqueId = persistence.replace("client", new PUBREL(4), 0);
+        assertEquals(4, persistence.size("client", false, 0));
+        assertNull(uniqueId);
+    }
+
+    @Test
+    public void test_remove() {
+        for (int i = 0; i < 3; i++) {
+            persistence.add("client", false, createPublish(1, QoS.AT_LEAST_ONCE, "topic", i), 100L, DISCARD, false, 0);
+        }
+        persistence.readNew("client", false, ImmutableIntArray.of(2, 3, 4), 256000, 0);
+        final String uniqueId = persistence.remove("client", 4, 0);
+        assertEquals("hivemqId_pub_2", uniqueId);
+        final ImmutableList<MessageWithID> messages = persistence.readInflight("client", false, 10, byteLimit, 0);
+        assertEquals(2, messages.size());
+        assertEquals(2, messages.get(0).getPacketIdentifier());
+        assertEquals(3, messages.get(1).getPacketIdentifier());
+
+        assertEquals(2, persistence.size("client", false, 0));
+
+        verify(payloadPersistence, times(1)).decrementReferenceCounter(anyLong());
+    }
+
     @Test
     public void test_remove_not_found() {
         for (int i = 0; i < 3; i++) {
@@ -428,6 +607,37 @@ public class ClientQueueMemoryLocalPersistenceTest {
         assertEquals(2, messages3.size());
     }
 
+    @Test
+    public void test_clean_up() {
+        persistence.add("removed", false, createPublish(0, QoS.AT_LEAST_ONCE), 10, DISCARD, false, 0);
+        persistence.clear("removed", false, 0);
+
+        persistence.readNew("empty", false, ImmutableIntArray.of(1), 100000L, 0);
+
+        persistence.add(
+                "client1", false, createPublish(0, QoS.AT_LEAST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
+                DISCARD, false, 0);
+        persistence.add(
+                "client1", false, createPublish(0, QoS.AT_LEAST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
+                DISCARD, false, 0);
+        persistence.add("client1", false, createPublish(0, QoS.AT_LEAST_ONCE, "topic2"), 10, DISCARD, false, 0);
+        persistence.add(
+                "client1", false, createPublish(0, QoS.AT_MOST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
+                DISCARD, false, 0);
+        persistence.add("client1", false, createPublish(0, QoS.AT_MOST_ONCE, "topic2"), 10, DISCARD, false, 0);
+
+        final ImmutableList<PUBLISH> newMessages =
+                persistence.readNew("client1", false, ImmutableIntArray.of(1), 10000L, 0);
+        assertEquals(1, newMessages.size());
+        assertEquals("topic2", newMessages.get(0).getTopic());
+
+        final ImmutableSet<String> sharedQueues = persistence.cleanUp(0);
+
+        assertTrue(sharedQueues.isEmpty());
+        verify(payloadPersistence, times(5)).decrementReferenceCounter(
+                anyLong()); // 3 expired + 1 clear + 1 poll(readNew)
+        assertEquals(1, persistence.size("client1", false, 0));
+    }
 
     @Test
     public void test_clean_up_expired_qos0() {
@@ -465,6 +675,65 @@ public class ClientQueueMemoryLocalPersistenceTest {
         assertEquals(0, persistence.size("client1", false, 0));
     }
 
+    @Test
+    public void test_clean_up_expired_pubrels_not_configured() throws InterruptedException {
+
+        persistence.add(
+                "client1", false, createPublish(1, QoS.EXACTLY_ONCE, 2, System.currentTimeMillis()), 10,
+                DISCARD, false, 0);
+        persistence.add(
+                "client1", false, createPublish(2, QoS.EXACTLY_ONCE, 2, System.currentTimeMillis()), 10,
+                DISCARD, false, 0);
+
+        persistence.readNew("client1", false, createPacketIds(1, 2), byteLimit, 0);
+
+        //let them expire
+        Thread.sleep(3000);
+
+        persistence.replace("client1", new PUBREL(1), 0);
+        persistence.replace("client1", new PUBREL(2), 0);
+
+        final ImmutableSet<String> sharedQueues = persistence.cleanUp(0);
+
+        assertTrue(sharedQueues.isEmpty());
+        verify(payloadPersistence, times(2)).decrementReferenceCounter(
+                anyLong()); // 2 replaces
+        assertEquals(2, persistence.size("client1", false, 0));
+    }
+
+    @Test
+    public void test_clean_up_expired_pubrels_configured() throws InterruptedException {
+
+        InternalConfigurations.EXPIRE_INFLIGHT_PUBRELS = true;
+
+        metricRegistry = new MetricRegistry();
+        persistence = new ClientQueueMemoryLocalPersistence(
+                payloadPersistence,
+                messageDroppedService, metricRegistry);
+
+        persistence.add(
+                "client1", false, createPublish(1, QoS.EXACTLY_ONCE, 2, System.currentTimeMillis()), 10,
+                DISCARD, false, 0);
+        persistence.add(
+                "client1", false, createPublish(2, QoS.EXACTLY_ONCE, 2, System.currentTimeMillis()), 10,
+                DISCARD, false, 0);
+
+        persistence.readNew("client1", false, createPacketIds(1, 2), byteLimit, 0);
+
+        //let them expire
+        Thread.sleep(3000);
+
+        persistence.replace("client1", new PUBREL(1), 0);
+        persistence.replace("client1", new PUBREL(2), 0);
+
+        final ImmutableSet<String> sharedQueues = persistence.cleanUp(0);
+
+        assertTrue(sharedQueues.isEmpty());
+        verify(payloadPersistence, times(2)).decrementReferenceCounter(
+                anyLong()); // 2 replaces
+        assertEquals(0, persistence.size("client1", false, 0));
+    }
+
     @Test
     public void test_clean_up_shared() {
         persistence.add(
@@ -577,6 +846,83 @@ public class ClientQueueMemoryLocalPersistenceTest {
         assertEquals(new PublishWithRetained(messages.get(0), false).getEstimatedSize() + ObjectMemoryEstimation.linkedListNodeOverhead(), gauge.getValue().longValue());
     }
 
+    @Test
+    public void test_batched_add() {
+        final ImmutableList.Builder<PUBLISH> publishes = ImmutableList.builder();
+        for (int i = 0; i < 10; i++) {
+            publishes.add(createPublish(1, QoS.AT_LEAST_ONCE, "topic" + i));
+        }
+        persistence.add("client", false, publishes.build(), 100, DISCARD, false, 0);
+
+        assertEquals(10, persistence.size("client", false, 0));
+
+        final ImmutableList<PUBLISH> all =
+                persistence.readNew("client", false, ImmutableIntArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 10000L, 0);
+
+        assertEquals(10, all.size());
+        assertEquals("topic0", all.get(0).getTopic());
+        assertEquals("topic1", all.get(1).getTopic());
+    }
+
+    @Test
+    public void test_batched_add_discard() {
+        final ImmutableList.Builder<PUBLISH> publishes = ImmutableList.builder();
+        for (int i = 0; i < 10; i++) {
+            publishes.add(createPublish(1, QoS.AT_LEAST_ONCE, "topic" + i));
+        }
+        persistence.add("client", false, publishes.build(), 5, DISCARD, false, 0);
+
+        assertEquals(5, persistence.size("client", false, 0));
+
+        final ImmutableList<PUBLISH> all =
+                persistence.readNew("client", false, ImmutableIntArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 10000L, 0);
+        assertEquals(5, all.size());
+        assertEquals("topic0", all.get(0).getTopic());
+        assertEquals("topic1", all.get(1).getTopic());
+    }
+
+    @Test
+    public void test_batched_add_discard_oldest() {
+        final ImmutableList.Builder<PUBLISH> publishes = ImmutableList.builder();
+
+        persistence.add("client", false, createPublish(1, QoS.AT_LEAST_ONCE, "topicA"), 3, DISCARD_OLDEST, false, 0);
+        persistence.add("client", false, createPublish(1, QoS.AT_LEAST_ONCE, "topicB"), 3, DISCARD_OLDEST, false, 0);
+        persistence.add("client", false, createPublish(1, QoS.AT_LEAST_ONCE, "topicC"), 3, DISCARD_OLDEST, false, 0);
+
+        for (int i = 0; i < 3; i++) {
+            publishes.add(createPublish(1, QoS.AT_LEAST_ONCE, "topic" + i));
+        }
+        persistence.add("client", false, publishes.build(), 3, DISCARD_OLDEST, false, 0);
+
+        assertEquals(3, persistence.size("client", false, 0));
+
+        final ImmutableList<PUBLISH> all =
+                persistence.readNew("client", false, ImmutableIntArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 10000L, 0);
+        assertEquals(3, all.size());
+        assertEquals("topic0", all.get(0).getTopic());
+        assertEquals("topic1", all.get(1).getTopic());
+        assertEquals("topic2", all.get(2).getTopic());
+    }
+
+    @Test
+    public void test_batched_add_larger_than_queue_discard_oldest() {
+        final ImmutableList.Builder<PUBLISH> publishes = ImmutableList.builder();
+
+        for (int i = 0; i < 6; i++) {
+            publishes.add(createPublish(1, QoS.AT_LEAST_ONCE, "topic" + i));
+        }
+        persistence.add("client", false, publishes.build(), 3, DISCARD_OLDEST, false, 0);
+
+        assertEquals(3, persistence.size("client", false, 0));
+
+        final ImmutableList<PUBLISH> all =
+                persistence.readNew("client", false, ImmutableIntArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 10000L, 0);
+        assertEquals(3, all.size());
+        assertEquals("topic3", all.get(0).getTopic());
+        assertEquals("topic4", all.get(1).getTopic());
+        assertEquals("topic5", all.get(2).getTopic());
+    }
+
     @Test
     public void test_batched_drop_qos_0_memory_exceeded() {
 
@@ -595,6 +941,54 @@ public class ClientQueueMemoryLocalPersistenceTest {
         assertEquals(1, all.size());
     }
 
+    @Test
+    public void test_batched_add_retained_dont_discard() {
+        final ImmutableList.Builder<PUBLISH> publishes = ImmutableList.builder();
+        for (int i = 0; i < 5; i++) {
+            publishes.add(createPublish(1, QoS.AT_LEAST_ONCE, "topic" + i));
+        }
+        persistence.add("client", false, publishes.build(), 2, DISCARD, true, 0);
+
+        assertEquals(5, persistence.size("client", false, 0));
+
+        final ImmutableList<PUBLISH> all =
+                persistence.readNew("client", false, ImmutableIntArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 10000L, 0);
+        assertEquals(5, all.size());
+        assertEquals("topic0", all.get(0).getTopic());
+        assertEquals("topic1", all.get(1).getTopic());
+    }
+
+    @Test
+    public void test_batched_add_retained_discard_over_retained_limit() {
+        final ImmutableList.Builder<PUBLISH> publishes1 = ImmutableList.builder();
+        final ImmutableList.Builder<PUBLISH> publishes2 = ImmutableList.builder();
+        for (int i = 0; i < 10; i++) {
+            if (i < 5) {
+                publishes1.add(createPublish(1, QoS.AT_LEAST_ONCE, "topic" + i));
+            } else {
+                publishes2.add(createPublish(1, QoS.AT_LEAST_ONCE, "topic" + i));
+            }
+        }
+        persistence.add("client", false, publishes1.build(), 2, DISCARD, true, 0);
+
+        final Gauge<Long> gauge = metricRegistry.getGauges().get(HiveMQMetrics.QUEUED_MESSAGES_MEMORY_PERSISTENCE_TOTAL_SIZE.name());
+        final Long value = gauge.getValue();
+        assertTrue(value > 0);
+
+        assertEquals(5, persistence.size("client", false, 0));
+
+        persistence.add("client", false, publishes2.build(), 2, DISCARD, true, 0);
+
+        assertEquals(5, persistence.size("client", false, 0));
+        assertEquals(value, gauge.getValue());
+
+        final ImmutableList<PUBLISH> all =
+                persistence.readNew("client", false, ImmutableIntArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 10000L, 0);
+        assertEquals(5, all.size());
+        assertEquals("topic0", all.get(0).getTopic());
+        assertEquals("topic1", all.get(1).getTopic());
+    }
+
     @Test
     public void add_and_poll_mixture_retained() {
         for (int i = 0; i < 12; i++) {
@@ -665,6 +1059,127 @@ public class ClientQueueMemoryLocalPersistenceTest {
 
     }
 
+    @Test
+    public void test_read_byte_limit_respected_qos0() {
+
+        InternalConfigurations.QOS_0_MEMORY_LIMIT_PER_CLIENT.set(1024 * 100);
+
+        metricRegistry = new MetricRegistry();
+        persistence = new ClientQueueMemoryLocalPersistence(
+                payloadPersistence,
+                messageDroppedService, metricRegistry);
+
+        final ImmutableList.Builder<PUBLISH> publishes = ImmutableList.builder();
+        int totalPublishBytes = 0;
+        for (int i = 0; i < 100; i++) {
+            final PUBLISH publish = createPublish(i + 1, QoS.AT_MOST_ONCE, "topic" + i);
+            totalPublishBytes += publish.getEstimatedSizeInMemory();
+            publishes.add(publish);
+        }
+        persistence.add("client", false, publishes.build(), 2, DISCARD, false, 0);
+
+        final Gauge<Long> gauge = metricRegistry.getGauges().get(HiveMQMetrics.QUEUED_MESSAGES_MEMORY_PERSISTENCE_TOTAL_SIZE.name());
+        assertTrue(gauge.getValue() > 0);
+
+        int byteLimit = totalPublishBytes / 2;
+        final ImmutableList<PUBLISH> allReadPublishes = persistence.readNew("client", false, createPacketIds(1, 100), byteLimit, 0);
+        assertEquals(51, allReadPublishes.size());
+
+        final ImmutableList<PUBLISH> allReadPublishes2 = persistence.readNew("client", false, createPacketIds(52, 100), byteLimit, 0);
+        assertEquals(49, allReadPublishes2.size());
+
+        assertEquals(0, gauge.getValue().longValue());
+
+    }
+
+    @Test
+    public void test_read_byte_limit_respected_qos1() {
+
+        InternalConfigurations.QOS_0_MEMORY_LIMIT_PER_CLIENT.set(1024 * 100);
+
+        metricRegistry = new MetricRegistry();
+        persistence = new ClientQueueMemoryLocalPersistence(
+                payloadPersistence,
+                messageDroppedService, metricRegistry);
+
+        final ImmutableList.Builder<PUBLISH> publishes = ImmutableList.builder();
+        int totalPublishBytes = 0;
+        for (int i = 0; i < 100; i++) {
+            final PUBLISH publish = createPublish(i + 1, QoS.AT_LEAST_ONCE, "topic" + i);
+            totalPublishBytes += publish.getEstimatedSizeInMemory();
+            publishes.add(publish);
+        }
+        persistence.add("client", false, publishes.build(), 100, DISCARD, false, 0);
+
+        final Gauge<Long> gauge = metricRegistry.getGauges().get(HiveMQMetrics.QUEUED_MESSAGES_MEMORY_PERSISTENCE_TOTAL_SIZE.name());
+        assertTrue(gauge.getValue() > 0);
+
+        int byteLimit = totalPublishBytes / 2;
+        System.out.println(byteLimit);
+        final ImmutableList<PUBLISH> allReadPublishes = persistence.readNew("client", false, createPacketIds(1, 100), byteLimit, 0);
+        assertEquals(51, allReadPublishes.size());
+
+        final ImmutableList<PUBLISH> allReadPublishes2 = persistence.readNew("client", false, createPacketIds(52, 100), byteLimit, 0);
+        assertEquals(49, allReadPublishes2.size());
+
+        assertTrue(gauge.getValue() > 0);
+
+        for (final PUBLISH pub : allReadPublishes) {
+            persistence.remove("client", pub.getPacketIdentifier(), pub.getUniqueId(), 0);
+        }
+        for (final PUBLISH pub : allReadPublishes2) {
+            persistence.remove("client", pub.getPacketIdentifier(), pub.getUniqueId(), 0);
+        }
+
+        assertEquals(0, gauge.getValue().longValue());
+
+    }
+
+    @Test
+    public void test_read_byte_limit_respected_qos0_and_qos1() {
+
+        InternalConfigurations.QOS_0_MEMORY_LIMIT_PER_CLIENT.set(1024 * 100);
+
+        metricRegistry = new MetricRegistry();
+        persistence = new ClientQueueMemoryLocalPersistence(
+                payloadPersistence,
+                messageDroppedService, metricRegistry);
+
+        final ImmutableList.Builder<PUBLISH> publishes = ImmutableList.builder();
+        int totalPublishBytes = 0;
+        for (int i = 0; i < 100; i++) {
+            final PUBLISH publish = createPublish(i + 1, QoS.valueOf(i % 2), "topic" + i);
+            totalPublishBytes += publish.getEstimatedSizeInMemory();
+            publishes.add(publish);
+        }
+        persistence.add("client", false, publishes.build(), 100, DISCARD, false, 0);
+
+        final Gauge<Long> gauge = metricRegistry.getGauges().get(HiveMQMetrics.QUEUED_MESSAGES_MEMORY_PERSISTENCE_TOTAL_SIZE.name());
+        assertTrue(gauge.getValue() > 0);
+
+        int byteLimit = totalPublishBytes / 2;
+        final ImmutableList<PUBLISH> allReadPublishes = persistence.readNew("client", false, createPacketIds(1, 100), byteLimit, 0);
+        assertEquals(51, allReadPublishes.size());
+
+        for (final PUBLISH pub : allReadPublishes) {
+            persistence.remove("client", pub.getPacketIdentifier(), pub.getUniqueId(), 0);
+        }
+
+        final ImmutableList<PUBLISH> allReadPublishes2 = persistence.readNew("client", false, createPacketIds(52, 100), byteLimit, 0);
+        assertEquals(48, allReadPublishes2.size());
+        assertTrue(gauge.getValue() > 0);
+
+        for (final PUBLISH pub : allReadPublishes2) {
+            persistence.remove("client", pub.getPacketIdentifier(), pub.getUniqueId(), 0);
+        }
+
+        //last qos0 message
+        final ImmutableList<PUBLISH> allReadPublishes3 = persistence.readNew("client", false, createPacketIds(100, 100), byteLimit, 0);
+        assertEquals(1, allReadPublishes3.size());
+        assertEquals(0, gauge.getValue().longValue());
+
+    }
+
     private ImmutableIntArray createPacketIds(final int start, final int size) {
         final ImmutableIntArray.Builder builder = ImmutableIntArray.builder();
         for (int i = start; i < (size + start); i++) {
