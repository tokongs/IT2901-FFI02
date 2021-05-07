index f245d6f..bddb53a 100644
--- a/broker/src/test/java/com/hivemq/persistence/clientqueue/ClientQueueXodusLocalPersistenceTest.java
+++ b/broker/src/test/java/com/hivemq/persistence/clientqueue/ClientQueueXodusLocalPersistenceTest.java
@@ -293,6 +293,38 @@ public class ClientQueueXodusLocalPersistenceTest {
         assertEquals(4, messages2.size());
     }
 
+    @Test
+    public void test_read_inflight_pubrel_and_publish() {
+        final PUBREL[] pubrels = new PUBREL[4];
+        for (int i = 0; i < pubrels.length; i++) {
+            pubrels[i] = new PUBREL(i + 1);
+        }
+        for (final PUBREL pubrel : pubrels) {
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
