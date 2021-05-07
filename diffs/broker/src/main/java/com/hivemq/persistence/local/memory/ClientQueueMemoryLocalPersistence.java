index e825591..50fdd09 100644
--- a/broker/src/main/java/com/hivemq/persistence/local/memory/ClientQueueMemoryLocalPersistence.java
+++ b/broker/src/main/java/com/hivemq/persistence/local/memory/ClientQueueMemoryLocalPersistence.java
@@ -17,6 +17,7 @@ package com.hivemq.persistence.local.memory;
 
 import com.codahale.metrics.Gauge;
 import com.codahale.metrics.MetricRegistry;
+import com.google.common.annotations.VisibleForTesting;
 import com.google.common.collect.ImmutableList;
 import com.google.common.collect.ImmutableSet;
 import com.google.common.primitives.ImmutableIntArray;
@@ -34,14 +35,16 @@ import com.hivemq.mqtt.message.publish.PUBLISH;
 import com.hivemq.mqtt.message.pubrel.PUBREL;
 import com.hivemq.persistence.clientqueue.ClientQueueLocalPersistence;
 import com.hivemq.persistence.payload.PublishPayloadPersistence;
-import com.hivemq.util.*;
+import com.hivemq.util.ObjectMemoryEstimation;
+import com.hivemq.util.PublishUtil;
+import com.hivemq.util.Strings;
+import com.hivemq.util.ThreadPreConditions;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import javax.inject.Inject;
 import java.util.*;
 import java.util.concurrent.atomic.AtomicLong;
-import java.util.stream.Collectors;
 
 import static com.google.common.base.Preconditions.checkNotNull;
 import static com.hivemq.configuration.service.InternalConfigurations.QOS_0_MEMORY_HARD_LIMIT_DIVISOR;
@@ -52,93 +55,62 @@ import static com.hivemq.util.ThreadPreConditions.SINGLE_WRITER_THREAD_PREFIX;
  * @author Silvio Giebl
  */
 @LazySingleton
-public class ClientQueueMemoryLocalPersistence
-        implements ClientQueueLocalPersistence {
+public class ClientQueueMemoryLocalPersistence implements ClientQueueLocalPersistence {
 
-    @NotNull
-    private static final Logger log = LoggerFactory.getLogger(
-            ClientQueueMemoryLocalPersistence.class
-    );
+    private static final @NotNull Logger log = LoggerFactory.getLogger(ClientQueueMemoryLocalPersistence.class);
 
     private static final int NO_PACKET_ID = 0;
 
-    @NotNull
-    private final Map<String, Messages> @NotNull [] buckets;
-
-    @NotNull
-    private final Map<String, Messages> @NotNull [] sharedBuckets;
-
-    static class Messages {
-
-        @NotNull
-        final PriorityQueue<MessageWithID> qos1Or2Messages;
-
-        @NotNull
-        final PriorityQueue<PublishWithRetained> qos0Messages;
-
-        public Messages(Comparator<PUBLISH> comparator) {
-            qos1Or2Messages = new PriorityQueue(comparator);
-            qos0Messages = new PriorityQueue(comparator);
-        }
-
+    private final @NotNull Map<String, Messages> @NotNull [] buckets;
+    private final @NotNull Map<String, Messages> @NotNull [] sharedBuckets;
 
+    private static class Messages {
+        final @NotNull LinkedList<MessageWithID> qos1Or2Messages = new LinkedList<>();
+        final @NotNull LinkedList<PublishWithRetained> qos0Messages = new LinkedList<>();
         int retainedQos1Or2Messages = 0;
         long qos0Memory = 0;
     }
 
-    @NotNull
-    private final PublishPayloadPersistence payloadPersistence;
-
-    @NotNull
-    private final MessageDroppedService messageDroppedService;
+    private final @NotNull PublishPayloadPersistence payloadPersistence;
+    private final @NotNull MessageDroppedService messageDroppedService;
 
     private final long qos0MemoryLimit;
     private final int qos0ClientMemoryLimit;
     private final int retainedMessageMax;
 
-    @NotNull
-    private final AtomicLong qos0MessagesMemory;
-
-    @NotNull
-    private final AtomicLong totalMemorySize;
-
-    @NotNull
-    private final PublishComparator publishComparator;
+    private final @NotNull AtomicLong qos0MessagesMemory;
+    private final @NotNull AtomicLong totalMemorySize;
 
     @Inject
     ClientQueueMemoryLocalPersistence(
             final @NotNull PublishPayloadPersistence payloadPersistence,
             final @NotNull MessageDroppedService messageDroppedService,
-            final @NotNull MetricRegistry metricRegistry,
-            final @NotNull PublishComparator publishComparator
-    ) {
+            final @NotNull MetricRegistry metricRegistry) {
+
         final int bucketCount = InternalConfigurations.PERSISTENCE_BUCKET_COUNT.get();
-        // noinspection unchecked
+        //noinspection unchecked
         buckets = new HashMap[bucketCount];
-        // noinspection unchecked
+        //noinspection unchecked
         sharedBuckets = new HashMap[bucketCount];
         for (int i = 0; i < bucketCount; i++) {
             buckets[i] = new HashMap<>();
             sharedBuckets[i] = new HashMap<>();
         }
-        this.publishComparator = publishComparator;
 
         this.payloadPersistence = payloadPersistence;
         this.messageDroppedService = messageDroppedService;
 
         qos0MemoryLimit = getQos0MemoryLimit();
-        qos0ClientMemoryLimit =
-                InternalConfigurations.QOS_0_MEMORY_LIMIT_PER_CLIENT.get();
-        retainedMessageMax =
-                InternalConfigurations.RETAINED_MESSAGE_QUEUE_SIZE.get();
+        qos0ClientMemoryLimit = InternalConfigurations.QOS_0_MEMORY_LIMIT_PER_CLIENT.get();
+        retainedMessageMax = InternalConfigurations.RETAINED_MESSAGE_QUEUE_SIZE.get();
 
         qos0MessagesMemory = new AtomicLong();
         totalMemorySize = new AtomicLong();
 
         metricRegistry.register(
                 HiveMQMetrics.QUEUED_MESSAGES_MEMORY_PERSISTENCE_TOTAL_SIZE.name(),
-                (Gauge<Long>) totalMemorySize::get
-        );
+                (Gauge<Long>) totalMemorySize::get);
+
     }
 
     private long getQos0MemoryLimit() {
@@ -148,15 +120,12 @@ public class ClientQueueMemoryLocalPersistence
         final int hardLimitDivisor = QOS_0_MEMORY_HARD_LIMIT_DIVISOR.get();
 
         if (hardLimitDivisor < 1) {
-            // fallback to default if config failed
+            //fallback to default if config failed
             maxHardLimit = maxHeap / 4;
         } else {
             maxHardLimit = maxHeap / hardLimitDivisor;
         }
-        log.debug(
-                "{} allocated for qos 0 inflight messages",
-                Strings.convertBytes(maxHardLimit)
-        );
+        log.debug("{} allocated for qos 0 inflight messages", Strings.convertBytes(maxHardLimit));
         return maxHardLimit;
     }
 
@@ -172,22 +141,14 @@ public class ClientQueueMemoryLocalPersistence
             final long max,
             final @NotNull QueuedMessagesStrategy strategy,
             final boolean retained,
-            final int bucketIndex
-    ) {
+            final int bucketIndex) {
+
         checkNotNull(queueId, "Queue ID must not be null");
         checkNotNull(publish, "Publish must not be null");
         checkNotNull(strategy, "Strategy must not be null");
         ThreadPreConditions.startsWith(SINGLE_WRITER_THREAD_PREFIX);
 
-        add(
-                queueId,
-                shared,
-                List.of(publish),
-                max,
-                strategy,
-                retained,
-                bucketIndex
-        );
+        add(queueId, shared, List.of(publish), max, strategy, retained, bucketIndex);
     }
 
     /**
@@ -210,7 +171,7 @@ public class ClientQueueMemoryLocalPersistence
         ThreadPreConditions.startsWith(SINGLE_WRITER_THREAD_PREFIX);
 
         final Map<String, Messages> bucket = shared ? sharedBuckets[bucketIndex] : buckets[bucketIndex];
-        final Messages messages = bucket.computeIfAbsent(queueId, s -> new Messages(publishComparator));
+        final Messages messages = bucket.computeIfAbsent(queueId, s -> new Messages());
 
         for (final PUBLISH publish : publishes) {
             final PublishWithRetained publishWithRetained = new PublishWithRetained(publish, retained);
@@ -255,93 +216,36 @@ public class ClientQueueMemoryLocalPersistence
         }
     }
 
-    /**
-     * @return true if a message was discarded, else false
-     */
-    private boolean discardOldest(
-            final @NotNull String queueId,
-            final boolean shared,
-            final @NotNull Messages messages,
-            final boolean retainedOnly) {
-
-        final Iterator<MessageWithID> iterator = messages.qos1Or2Messages.iterator();
-        while (iterator.hasNext()) {
-            final MessageWithID messageWithID = iterator.next();
-            if (!(messageWithID instanceof PublishWithRetained)) {
-                continue;
-            }
-            final PublishWithRetained publish = (PublishWithRetained) messageWithID;
-            // we must no discard inflight messages
-            if (publish.getPacketIdentifier() != NO_PACKET_ID) {
-                continue;
-            }
-            // Messages that are queued as retained messages are not discarded,
-            // otherwise a client could only receive a limited amount of retained messages per subscription.
-            if ((retainedOnly && !publish.retained) || (!retainedOnly && publish.retained)) {
-                continue;
-            }
-            logAndDecrementPayloadReference(publish, shared, queueId);
-            iterator.remove();
-            return true;
-        }
-        return false;
-
-    }
-
-
     private void addQos0Publish(
             final @NotNull String queueId,
             final boolean shared,
             final @NotNull Messages messages,
-            final @NotNull PublishWithRetained publishWithRetained
-    ) {
+            final @NotNull PublishWithRetained publishWithRetained) {
+
         final long currentQos0MessagesMemory = qos0MessagesMemory.get();
         if (currentQos0MessagesMemory >= qos0MemoryLimit) {
             if (shared) {
                 messageDroppedService.qos0MemoryExceededShared(
-                        queueId,
-                        publishWithRetained.getTopic(),
-                        0,
-                        currentQos0MessagesMemory,
-                        qos0MemoryLimit
-                );
+                        queueId, publishWithRetained.getTopic(), 0, currentQos0MessagesMemory, qos0MemoryLimit);
             } else {
                 messageDroppedService.qos0MemoryExceeded(
-                        queueId,
-                        publishWithRetained.getTopic(),
-                        0,
-                        currentQos0MessagesMemory,
-                        qos0MemoryLimit
-                );
+                        queueId, publishWithRetained.getTopic(), 0, currentQos0MessagesMemory, qos0MemoryLimit);
             }
-            payloadPersistence.decrementReferenceCounter(
-                    publishWithRetained.getPublishId()
-            );
+            payloadPersistence.decrementReferenceCounter(publishWithRetained.getPublishId());
             return;
         }
 
         if (!shared) {
             if (messages.qos0Memory >= qos0ClientMemoryLimit) {
-                messageDroppedService.qos0MemoryExceeded(
-                        queueId,
-                        publishWithRetained.getTopic(),
-                        0,
-                        messages.qos0Memory,
-                        qos0ClientMemoryLimit
-                );
-                payloadPersistence.decrementReferenceCounter(
-                        publishWithRetained.getPublishId()
-                );
+                messageDroppedService.qos0MemoryExceeded(queueId, publishWithRetained.getTopic(), 0, messages.qos0Memory, qos0ClientMemoryLimit);
+                payloadPersistence.decrementReferenceCounter(publishWithRetained.getPublishId());
                 return;
             }
         }
 
         messages.qos0Messages.add(publishWithRetained);
         increaseQos0MessagesMemory(publishWithRetained.getEstimatedSize());
-        increaseClientQos0MessagesMemory(
-                messages,
-                publishWithRetained.getEstimatedSize()
-        );
+        increaseClientQos0MessagesMemory(messages, publishWithRetained.getEstimatedSize());
         increaseMessagesMemory(publishWithRetained.getEstimatedSize());
     }
 
@@ -355,19 +259,18 @@ public class ClientQueueMemoryLocalPersistence
             final boolean shared,
             final @NotNull ImmutableIntArray packetIds,
             final long bytesLimit,
-            final int bucketIndex
-    ) {
+            final int bucketIndex) {
+
         checkNotNull(queueId, "Queue ID must not be null");
         checkNotNull(packetIds, "Packet IDs must not be null");
         ThreadPreConditions.startsWith(SINGLE_WRITER_THREAD_PREFIX);
 
-        final Map<String, Messages> bucket = shared
-                ? sharedBuckets[bucketIndex]
-                : buckets[bucketIndex];
+        final Map<String, Messages> bucket = shared ? sharedBuckets[bucketIndex] : buckets[bucketIndex];
         final Messages messages = bucket.get(queueId);
         if (messages == null) {
             return ImmutableList.of();
         }
+
         // In case there are only qos 0 messages
         if (messages.qos1Or2Messages.size() == 0) {
             return getQos0Publishes(messages, packetIds, bytesLimit);
@@ -379,36 +282,26 @@ public class ClientQueueMemoryLocalPersistence
         int bytes = 0;
         final ImmutableList.Builder<PUBLISH> publishes = ImmutableList.builder();
 
-        PriorityQueue<PublishWithRetained> allPublishes = new PriorityQueue(publishComparator);
-        allPublishes.addAll(messages.qos0Messages);
-        allPublishes.addAll(messages.qos1Or2Messages
-                .stream()
-                .filter(m -> m instanceof PublishWithRetained)
-                .map(m -> (PublishWithRetained) m)
-                .collect(Collectors.toList()));
-
-        while (!allPublishes.isEmpty()) {
-            final PublishWithRetained publishWithRetained = allPublishes.poll();
-
+        final Iterator<MessageWithID> iterator = messages.qos1Or2Messages.iterator();
+        while (iterator.hasNext()) {
+            final MessageWithID messageWithID = iterator.next();
+            if (!(messageWithID instanceof PublishWithRetained)) {
+                continue;
+            }
+            final PublishWithRetained publishWithRetained = (PublishWithRetained) messageWithID;
             if (publishWithRetained.getPacketIdentifier() != NO_PACKET_ID) {
                 //already inflight
                 continue;
             }
 
-            if(publishWithRetained.getQoS() == QoS.AT_MOST_ONCE  && !PublishUtil.checkExpiry(publishWithRetained.getTimestamp(), publishWithRetained.getMessageExpiryInterval()) ){
-                messages.qos0Messages.poll();
-                publishes.add(publishWithRetained);
-                    messageCount++;
-                    bytes += publishWithRetained.getEstimatedSizeInMemory();
-                    continue;
-            }
-
             if (PublishUtil.checkExpiry(publishWithRetained.getTimestamp(), publishWithRetained.getMessageExpiryInterval())) {
+                iterator.remove();
                 payloadPersistence.decrementReferenceCounter(publishWithRetained.getPublishId());
                 if (publishWithRetained.retained) {
                     messages.retainedQos1Or2Messages--;
                 }
                 increaseMessagesMemory(-publishWithRetained.getEstimatedSize());
+                //do not return here, because we could have a QoS 0 message left
             } else {
 
                 final int packetId = packetIds.get(packetIdIndex);
@@ -422,6 +315,13 @@ public class ClientQueueMemoryLocalPersistence
                 }
             }
 
+            // poll a qos 0 message
+            final PUBLISH qos0Publish = pollQos0Message(messages);
+            if ((qos0Publish != null) && !PublishUtil.checkExpiry(qos0Publish.getTimestamp(), qos0Publish.getMessageExpiryInterval())) {
+                publishes.add(qos0Publish);
+                messageCount++;
+                bytes += qos0Publish.getEstimatedSizeInMemory();
+            }
             if ((messageCount == countLimit) || (bytes > bytesLimit)) {
                 break;
             }
@@ -430,10 +330,8 @@ public class ClientQueueMemoryLocalPersistence
     }
 
     private @NotNull ImmutableList<PUBLISH> getQos0Publishes(
-            final @NotNull Messages messages,
-            final @NotNull ImmutableIntArray packetIds,
-            final long bytesLimit
-    ) {
+            final @NotNull Messages messages, final @NotNull ImmutableIntArray packetIds, final long bytesLimit) {
+
         final ImmutableList.Builder<PUBLISH> publishes = ImmutableList.builder();
         int qos0MessagesFound = 0;
         int qos0Bytes = 0;
@@ -442,12 +340,7 @@ public class ClientQueueMemoryLocalPersistence
             if (qos0Publish == null) {
                 break;
             }
-            if (
-                    !PublishUtil.checkExpiry(
-                            qos0Publish.getTimestamp(),
-                            qos0Publish.getMessageExpiryInterval()
-                    )
-            ) {
+            if (!PublishUtil.checkExpiry(qos0Publish.getTimestamp(), qos0Publish.getMessageExpiryInterval())) {
                 publishes.add(qos0Publish);
                 qos0MessagesFound++;
                 qos0Bytes += qos0Publish.getEstimatedSizeInMemory();
@@ -466,9 +359,7 @@ public class ClientQueueMemoryLocalPersistence
         increaseQos0MessagesMemory(-estimatedSize);
         increaseClientQos0MessagesMemory(messages, -estimatedSize);
         increaseMessagesMemory(-estimatedSize);
-        payloadPersistence.decrementReferenceCounter(
-                publishWithRetained.getPublishId()
-        );
+        payloadPersistence.decrementReferenceCounter(publishWithRetained.getPublishId());
         return publishWithRetained;
     }
 
@@ -479,14 +370,12 @@ public class ClientQueueMemoryLocalPersistence
             final boolean shared,
             final int batchSize,
             final long bytesLimit,
-            final int bucketIndex
-    ) {
+            final int bucketIndex) {
+
         checkNotNull(queueId, "client id must not be null");
         ThreadPreConditions.startsWith(SINGLE_WRITER_THREAD_PREFIX);
 
-        final Map<String, Messages> bucket = shared
-                ? sharedBuckets[bucketIndex]
-                : buckets[bucketIndex];
+        final Map<String, Messages> bucket = shared ? sharedBuckets[bucketIndex] : buckets[bucketIndex];
         final Messages messages = bucket.get(queueId);
         if (messages == null) {
             return ImmutableList.of();
@@ -524,10 +413,8 @@ public class ClientQueueMemoryLocalPersistence
     @Override
     @ExecuteInSingleWriter
     public @Nullable String replace(
-            final @NotNull String queueId,
-            final @NotNull PUBREL pubrel,
-            final int bucketIndex
-    ) {
+            final @NotNull String queueId, final @NotNull PUBREL pubrel, final int bucketIndex) {
+
         checkNotNull(queueId, "client id must not be null");
         checkNotNull(pubrel, "pubrel must not be null");
         ThreadPreConditions.startsWith(SINGLE_WRITER_THREAD_PREFIX);
@@ -569,12 +456,12 @@ public class ClientQueueMemoryLocalPersistence
                 break;
             }
         }
-        final PubrelWithRetained pubrelWithRetained = new PubrelWithRetained(
-                pubrel,
-                retained
-        );
+        final PubrelWithRetained pubrelWithRetained = new PubrelWithRetained(pubrel, retained);
         if (packetIdFound) {
-            messages.qos1Or2Messages.add(pubrelWithRetained);
+            messages.qos1Or2Messages.set(messageIndexInQueue, pubrelWithRetained);
+        } else {
+            // Ensure unknown PUBRELs are always first in queue
+            messages.qos1Or2Messages.addFirst(pubrelWithRetained);
         }
         increaseMessagesMemory(pubrelWithRetained.getEstimatedSize());
         return replacedId;
@@ -585,11 +472,7 @@ public class ClientQueueMemoryLocalPersistence
      */
     @Override
     @ExecuteInSingleWriter
-    public @Nullable String remove(
-            final @NotNull String queueId,
-            final int packetId,
-            final int bucketIndex
-    ) {
+    public @Nullable String remove(final @NotNull String queueId, final int packetId, final int bucketIndex) {
         return remove(queueId, packetId, null, bucketIndex);
     }
 
@@ -599,11 +482,8 @@ public class ClientQueueMemoryLocalPersistence
     @Override
     @ExecuteInSingleWriter
     public @Nullable String remove(
-            final @NotNull String queueId,
-            final int packetId,
-            final @Nullable String uniqueId,
-            final int bucketIndex
-    ) {
+            final @NotNull String queueId, final int packetId, final @Nullable String uniqueId, final int bucketIndex) {
+
         checkNotNull(queueId, "client id must not be null");
         ThreadPreConditions.startsWith(SINGLE_WRITER_THREAD_PREFIX);
 
@@ -637,26 +517,19 @@ public class ClientQueueMemoryLocalPersistence
         return null;
     }
 
+
     /**
      * {@inheritDoc}
      */
     @Override
     @ExecuteInSingleWriter
-    public int size(
-            final @NotNull String queueId,
-            final boolean shared,
-            final int bucketIndex
-    ) {
+    public int size(final @NotNull String queueId, final boolean shared, final int bucketIndex) {
         checkNotNull(queueId, "Queue ID must not be null");
         ThreadPreConditions.startsWith(SINGLE_WRITER_THREAD_PREFIX); // QueueSizes are not thread save
 
-        final Map<String, Messages> bucket = shared
-                ? sharedBuckets[bucketIndex]
-                : buckets[bucketIndex];
+        final Map<String, Messages> bucket = shared ? sharedBuckets[bucketIndex] : buckets[bucketIndex];
         final Messages messages = bucket.get(queueId);
-        return (messages == null)
-                ? 0
-                : (messages.qos1Or2Messages.size() + messages.qos0Messages.size());
+        return (messages == null) ? 0 : (messages.qos1Or2Messages.size() + messages.qos0Messages.size());
     }
 
     /**
@@ -664,17 +537,11 @@ public class ClientQueueMemoryLocalPersistence
      */
     @Override
     @ExecuteInSingleWriter
-    public int qos0Size(
-            final @NotNull String queueId,
-            final boolean shared,
-            final int bucketIndex
-    ) {
+    public int qos0Size(final @NotNull String queueId, final boolean shared, final int bucketIndex) {
         checkNotNull(queueId, "Queue ID must not be null");
         ThreadPreConditions.startsWith(SINGLE_WRITER_THREAD_PREFIX); // QueueSizes are not thread save
 
-        final Map<String, Messages> bucket = shared
-                ? sharedBuckets[bucketIndex]
-                : buckets[bucketIndex];
+        final Map<String, Messages> bucket = shared ? sharedBuckets[bucketIndex] : buckets[bucketIndex];
         final Messages messages = bucket.get(queueId);
         return (messages == null) ? 0 : messages.qos0Messages.size();
     }
@@ -684,17 +551,11 @@ public class ClientQueueMemoryLocalPersistence
      */
     @Override
     @ExecuteInSingleWriter
-    public void clear(
-            final @NotNull String queueId,
-            final boolean shared,
-            final int bucketIndex
-    ) {
+    public void clear(final @NotNull String queueId, final boolean shared, final int bucketIndex) {
         checkNotNull(queueId, "Queue ID must not be null");
         ThreadPreConditions.startsWith(SINGLE_WRITER_THREAD_PREFIX);
 
-        final Map<String, Messages> bucket = shared
-                ? sharedBuckets[bucketIndex]
-                : buckets[bucketIndex];
+        final Map<String, Messages> bucket = shared ? sharedBuckets[bucketIndex] : buckets[bucketIndex];
         final Messages messages = bucket.remove(queueId);
         if (messages == null) {
             return;
@@ -702,9 +563,7 @@ public class ClientQueueMemoryLocalPersistence
 
         for (final MessageWithID messageWithID : messages.qos1Or2Messages) {
             if (messageWithID instanceof PublishWithRetained) {
-                payloadPersistence.decrementReferenceCounter(
-                        ((PublishWithRetained) messageWithID).getPublishId()
-                );
+                payloadPersistence.decrementReferenceCounter(((PublishWithRetained) messageWithID).getPublishId());
             }
             increaseMessagesMemory(-getMessageSize(messageWithID));
         }
@@ -713,8 +572,7 @@ public class ClientQueueMemoryLocalPersistence
             payloadPersistence.decrementReferenceCounter(qos0Message.getPublishId());
             final int estimatedSize = qos0Message.getEstimatedSize();
             increaseQos0MessagesMemory(-estimatedSize);
-            // increaseClientQos0MessagesMemory not necessary as messages are removed
-            // completely
+            // increaseClientQos0MessagesMemory not necessary as messages are removed completely
             increaseMessagesMemory(-estimatedSize);
         }
     }
@@ -724,29 +582,20 @@ public class ClientQueueMemoryLocalPersistence
      */
     @Override
     @ExecuteInSingleWriter
-    public void removeAllQos0Messages(
-            final @NotNull String queueId,
-            final boolean shared,
-            final int bucketIndex
-    ) {
+    public void removeAllQos0Messages(final @NotNull String queueId, final boolean shared, final int bucketIndex) {
         checkNotNull(queueId, "Queue id must not be null");
         ThreadPreConditions.startsWith(SINGLE_WRITER_THREAD_PREFIX);
 
-        final Map<String, Messages> bucket = shared
-                ? sharedBuckets[bucketIndex]
-                : buckets[bucketIndex];
+        final Map<String, Messages> bucket = shared ? sharedBuckets[bucketIndex] : buckets[bucketIndex];
         final Messages messages = bucket.get(queueId);
         if (messages == null) {
             return;
         }
 
         for (final PublishWithRetained publishWithRetained : messages.qos0Messages) {
-            payloadPersistence.decrementReferenceCounter(
-                    publishWithRetained.getPublishId()
-            );
+            payloadPersistence.decrementReferenceCounter(publishWithRetained.getPublishId());
             increaseQos0MessagesMemory(-publishWithRetained.getEstimatedSize());
-            // increaseClientQos0MessagesMemory not necessary as messages.qos0Memory = 0
-            // below
+            // increaseClientQos0MessagesMemory not necessary as messages.qos0Memory = 0 below
             increaseMessagesMemory(-publishWithRetained.getEstimatedSize());
         }
         messages.qos0Messages.clear();
@@ -776,10 +625,8 @@ public class ClientQueueMemoryLocalPersistence
     @Override
     @ExecuteInSingleWriter
     public void removeShared(
-            final @NotNull String sharedSubscription,
-            final @NotNull String uniqueId,
-            final int bucketIndex
-    ) {
+            final @NotNull String sharedSubscription, final @NotNull String uniqueId, final int bucketIndex) {
+
         checkNotNull(sharedSubscription, "Shared subscription must not be null");
         checkNotNull(uniqueId, "Unique id must not be null");
         ThreadPreConditions.startsWith(SINGLE_WRITER_THREAD_PREFIX);
@@ -814,10 +661,8 @@ public class ClientQueueMemoryLocalPersistence
     @Override
     @ExecuteInSingleWriter
     public void removeInFlightMarker(
-            final @NotNull String sharedSubscription,
-            final @NotNull String uniqueId,
-            final int bucketIndex
-    ) {
+            final @NotNull String sharedSubscription, final @NotNull String uniqueId, final int bucketIndex) {
+
         checkNotNull(sharedSubscription, "Shared subscription must not be null");
         checkNotNull(uniqueId, "Unique id must not be null");
         ThreadPreConditions.startsWith(SINGLE_WRITER_THREAD_PREFIX);
@@ -871,148 +716,101 @@ public class ClientQueueMemoryLocalPersistence
     }
 
     private void logMessageDropped(
-            final @NotNull PUBLISH publish,
-            final boolean shared,
-            final @NotNull String queueId
-    ) {
+            final @NotNull PUBLISH publish, final boolean shared, final @NotNull String queueId) {
+
         if (shared) {
-            messageDroppedService.queueFullShared(
-                    queueId,
-                    publish.getTopic(),
-                    publish.getQoS().getQosNumber()
-            );
+            messageDroppedService.queueFullShared(queueId, publish.getTopic(), publish.getQoS().getQosNumber());
         } else {
-            messageDroppedService.queueFull(
-                    queueId,
-                    publish.getTopic(),
-                    publish.getQoS().getQosNumber()
-            );
+            messageDroppedService.queueFull(queueId, publish.getTopic(), publish.getQoS().getQosNumber());
         }
     }
 
     /**
-     * @param size the amount of bytes the currently used qos 0 memory will be
-     *             increased by. May be negative.
+     * @param size the amount of bytes the currently used qos 0 memory will be increased by. May be negative.
      */
     private void increaseQos0MessagesMemory(final int size) {
         if (size < 0) {
-            qos0MessagesMemory.addAndGet(
-                    size - ObjectMemoryEstimation.linkedListNodeOverhead()
-            );
+            qos0MessagesMemory.addAndGet(size - ObjectMemoryEstimation.linkedListNodeOverhead());
         } else {
-            qos0MessagesMemory.addAndGet(
-                    size + ObjectMemoryEstimation.linkedListNodeOverhead()
-            );
+            qos0MessagesMemory.addAndGet(size + ObjectMemoryEstimation.linkedListNodeOverhead());
         }
     }
 
     /**
-     * @param size the amount of bytes the currently used memory will be increased
-     *             by. May be negative.
+     * @param size the amount of bytes the currently used memory will be increased by. May be negative.
      */
     private void increaseMessagesMemory(final int size) {
         if (size < 0) {
-            totalMemorySize.addAndGet(
-                    size - ObjectMemoryEstimation.linkedListNodeOverhead()
-            );
+            totalMemorySize.addAndGet(size - ObjectMemoryEstimation.linkedListNodeOverhead());
         } else {
-            totalMemorySize.addAndGet(
-                    size + ObjectMemoryEstimation.linkedListNodeOverhead()
-            );
+            totalMemorySize.addAndGet(size + ObjectMemoryEstimation.linkedListNodeOverhead());
         }
     }
 
     /**
-     * @param size the amount of bytes the currently used qos 0 memory will be
-     *             increased by. May be negative.
+     * @param size the amount of bytes the currently used qos 0 memory will be increased by. May be negative.
      */
-    private void increaseClientQos0MessagesMemory(
-            final @NotNull Messages messages,
-            final int size
-    ) {
+    private void increaseClientQos0MessagesMemory(final @NotNull Messages messages, final int size) {
         if (size < 0) {
-            messages.qos0Memory +=
-                    size - ObjectMemoryEstimation.linkedListNodeOverhead();
+            messages.qos0Memory += size - ObjectMemoryEstimation.linkedListNodeOverhead();
         } else {
-            messages.qos0Memory +=
-                    size + ObjectMemoryEstimation.linkedListNodeOverhead();
+            messages.qos0Memory += size + ObjectMemoryEstimation.linkedListNodeOverhead();
         }
         if (messages.qos0Memory < 0) {
             messages.qos0Memory = 0;
         }
     }
 
-
     /**
-     * Discards the message if it exists.
-     *
-     * @param queueId  queueId
-     * @param shared   if the topic is shared
-     * @param messages all the messages
      * @return true if a message was discarded, else false
      */
-    private boolean discardPublishWithRetained(
+    private boolean discardOldest(
             final @NotNull String queueId,
             final boolean shared,
             final @NotNull Messages messages,
-            final @NotNull PublishWithRetained publishWithRetained
-    ) {
+            final boolean retainedOnly) {
 
-        if (publishWithRetained == null) {
-            return false;
-        } else if (messages.qos0Messages.contains(publishWithRetained)) {
-            logAndDecrementPayloadReference(
-                    publishWithRetained,
-                    shared,
-                    queueId
-            );
-            messages.qos0Messages.remove(publishWithRetained);
-            return true;
-        } else if (messages.qos1Or2Messages.contains(publishWithRetained)) {
-            logAndDecrementPayloadReference(
-                    publishWithRetained,
-                    shared,
-                    queueId
-            );
-            if (isRetained(publishWithRetained)) {
-                messages.retainedQos1Or2Messages--;
+        final Iterator<MessageWithID> iterator = messages.qos1Or2Messages.iterator();
+        while (iterator.hasNext()) {
+            final MessageWithID messageWithID = iterator.next();
+            if (!(messageWithID instanceof PublishWithRetained)) {
+                continue;
+            }
+            final PublishWithRetained publish = (PublishWithRetained) messageWithID;
+            // we must no discard inflight messages
+            if (publish.getPacketIdentifier() != NO_PACKET_ID) {
+                continue;
             }
-            increaseMessagesMemory(-getMessageSize(publishWithRetained));
-            messages.qos1Or2Messages.remove(publishWithRetained);
+            // Messages that are queued as retained messages are not discarded,
+            // otherwise a client could only receive a limited amount of retained messages per subscription.
+            if ((retainedOnly && !publish.retained) || (!retainedOnly && publish.retained)) {
+                continue;
+            }
+            logAndDecrementPayloadReference(publish, shared, queueId);
+            iterator.remove();
             return true;
         }
-
         return false;
+
     }
 
     private void logAndDecrementPayloadReference(
-            final @NotNull PUBLISH publish,
-            final boolean shared,
-            final @NotNull String queueId
-    ) {
+            final @NotNull PUBLISH publish, final boolean shared, final @NotNull String queueId) {
+
         logMessageDropped(publish, shared, queueId);
         payloadPersistence.decrementReferenceCounter(publish.getPublishId());
     }
 
     private void cleanExpiredMessages(final @NotNull Messages messages) {
+
         final Iterator<PublishWithRetained> iterator = messages.qos0Messages.iterator();
         while (iterator.hasNext()) {
             final PublishWithRetained publishWithRetained = iterator.next();
-            if (
-                    PublishUtil.checkExpiry(
-                            publishWithRetained.getTimestamp(),
-                            publishWithRetained.getMessageExpiryInterval()
-                    )
-            ) {
+            if (PublishUtil.checkExpiry(publishWithRetained.getTimestamp(), publishWithRetained.getMessageExpiryInterval())) {
                 increaseQos0MessagesMemory(-publishWithRetained.getEstimatedSize());
-                increaseClientQos0MessagesMemory(
-                        messages,
-                        -publishWithRetained.getEstimatedSize()
-                );
+                increaseClientQos0MessagesMemory(messages, -publishWithRetained.getEstimatedSize());
                 increaseMessagesMemory(-publishWithRetained.getEstimatedSize());
-                payloadPersistence.decrementReferenceCounter(
-                        publishWithRetained.getPublishId()
-                );
+                payloadPersistence.decrementReferenceCounter(publishWithRetained.getPublishId());
                 iterator.remove();
             }
         }
@@ -1025,18 +823,10 @@ public class ClientQueueMemoryLocalPersistence
                 if (!InternalConfigurations.EXPIRE_INFLIGHT_PUBRELS) {
                     continue;
                 }
-                if (
-                        pubrel.getExpiryInterval() == null ||
-                                pubrel.getPublishTimestamp() == null
-                ) {
+                if (pubrel.getExpiryInterval() == null || pubrel.getPublishTimestamp() == null) {
                     continue;
                 }
-                if (
-                        !PublishUtil.checkExpiry(
-                                pubrel.getPublishTimestamp(),
-                                pubrel.getExpiryInterval()
-                        )
-                ) {
+                if (!PublishUtil.checkExpiry(pubrel.getPublishTimestamp(), pubrel.getExpiryInterval())) {
                     continue;
                 }
                 if (pubrel.retained) {
@@ -1044,15 +834,12 @@ public class ClientQueueMemoryLocalPersistence
                 }
                 increaseMessagesMemory(-pubrel.getEstimatedSize());
                 qos12iterator.remove();
+
             } else if (messageWithID instanceof PublishWithRetained) {
                 final PublishWithRetained publish = (PublishWithRetained) messageWithID;
-                final boolean expireInflight =
-                        InternalConfigurations.EXPIRE_INFLIGHT_MESSAGES;
-                final boolean isInflight =
-                        publish.getQoS() == QoS.EXACTLY_ONCE &&
-                                publish.getPacketIdentifier() > 0;
-                final boolean drop =
-                        PublishUtil.checkExpiry(publish) && (!isInflight || expireInflight);
+                final boolean expireInflight = InternalConfigurations.EXPIRE_INFLIGHT_MESSAGES;
+                final boolean isInflight = publish.getQoS() == QoS.EXACTLY_ONCE && publish.getPacketIdentifier() > 0;
+                final boolean drop = PublishUtil.checkExpiry(publish) && (!isInflight || expireInflight);
                 if (drop) {
                     payloadPersistence.decrementReferenceCounter(publish.getPublishId());
                     if (publish.retained) {
@@ -1065,53 +852,41 @@ public class ClientQueueMemoryLocalPersistence
         }
     }
 
+    @VisibleForTesting
     static class PublishWithRetained extends PUBLISH {
 
         private final boolean retained;
 
-        PublishWithRetained(
-                final @NotNull PUBLISH publish,
-                final boolean retained
-        ) {
+        PublishWithRetained(final @NotNull PUBLISH publish, final boolean retained) {
             super(publish, publish.getPersistence());
             this.retained = retained;
         }
 
         int getEstimatedSize() {
-            return (
-                    getEstimatedSizeInMemory() + // publish
-                            ObjectMemoryEstimation.objectShellSize() + // the object itself
-                            ObjectMemoryEstimation.booleanSize()
-            ); // retain flag
+            return getEstimatedSizeInMemory()  // publish
+                    + ObjectMemoryEstimation.objectShellSize() // the object itself
+                    + ObjectMemoryEstimation.booleanSize(); // retain flag
         }
     }
 
-    static class PubrelWithRetained extends PUBREL {
+    private static class PubrelWithRetained extends PUBREL {
 
         private final boolean retained;
 
-        private PubrelWithRetained(
-                final @NotNull PUBREL pubrel,
-                final boolean retained
-        ) {
-            super(
-                    pubrel.getPacketIdentifier(),
+        private PubrelWithRetained(final @NotNull PUBREL pubrel, final boolean retained) {
+            super(pubrel.getPacketIdentifier(),
                     pubrel.getReasonCode(),
                     pubrel.getReasonString(),
                     pubrel.getUserProperties(),
                     pubrel.getPublishTimestamp(),
-                    pubrel.getExpiryInterval()
-            );
+                    pubrel.getExpiryInterval());
             this.retained = retained;
         }
 
         private int getEstimatedSize() {
-            return (
-                    getEstimatedSizeInMemory() + // publish
-                            ObjectMemoryEstimation.objectShellSize() + // the object itself
-                            ObjectMemoryEstimation.booleanSize()
-            ); // retain flag
+            return getEstimatedSizeInMemory()  // publish
+                    + ObjectMemoryEstimation.objectShellSize() // the object itself
+                    + ObjectMemoryEstimation.booleanSize(); // retain flag
         }
     }
-
 }
