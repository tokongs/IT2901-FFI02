/*
 * Copyright 2019-present HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hivemq.persistence.local.memory;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.ImmutableIntArray;
import com.hivemq.configuration.service.InternalConfigurations;
import com.hivemq.metrics.HiveMQMetrics;
import com.hivemq.mqtt.message.MessageWithID;
import com.hivemq.mqtt.message.QoS;
import com.hivemq.mqtt.message.dropping.MessageDroppedService;
import com.hivemq.mqtt.message.publish.PUBLISH;
import com.hivemq.mqtt.message.publish.PUBLISHFactory;
import com.hivemq.mqtt.message.pubrel.PUBREL;
import com.hivemq.persistence.local.memory.ClientQueueMemoryLocalPersistence.PublishWithRetained;
import com.hivemq.persistence.local.xodus.bucket.BucketUtils;
import com.hivemq.persistence.payload.PublishPayloadPersistence;
import com.hivemq.util.ObjectMemoryEstimation;
import com.hivemq.util.PublishComparator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hivemq.configuration.service.MqttConfigurationService.QueuedMessagesStrategy.DISCARD;
import static com.hivemq.configuration.service.MqttConfigurationService.QueuedMessagesStrategy.DISCARD_OLDEST;
import static com.hivemq.persistence.clientqueue.ClientQueuePersistenceImpl.SHARED_IN_FLIGHT_MARKER;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Florian Limpöck
 */
@SuppressWarnings("NullabilityAnnotations")
public class ClientQueueMemoryLocalPersistenceTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private PublishPayloadPersistence payloadPersistence;

    @Mock
    private MessageDroppedService messageDroppedService;

    @Mock
    private PublishComparator publishComparator;


    private ClientQueueMemoryLocalPersistence persistence;

    private final int bucketCount = 4;

    private final long byteLimit = 5 * 1024 * 1024;
    private MetricRegistry metricRegistry;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        InternalConfigurations.PERSISTENCE_BUCKET_COUNT.set(bucketCount);
        InternalConfigurations.QOS_0_MEMORY_HARD_LIMIT_DIVISOR.set(10000);
        InternalConfigurations.QOS_0_MEMORY_LIMIT_PER_CLIENT.set(1024);
        InternalConfigurations.RETAINED_MESSAGE_QUEUE_SIZE.set(5);

        metricRegistry = new MetricRegistry();
        persistence = new ClientQueueMemoryLocalPersistence(
                payloadPersistence,
                messageDroppedService, metricRegistry, publishComparator);
    }

    @After
    public void tearDown() throws Exception {
        InternalConfigurations.EXPIRE_INFLIGHT_PUBRELS = false;
    }

    @Test
    public void test_readNew_lessAvailable() {
        final PUBLISH publish = createPublish(10, QoS.AT_LEAST_ONCE, "topic1");
        final PUBLISH otherPublish = createPublish(11, QoS.EXACTLY_ONCE, "topic2");
        persistence.add("client10", false, otherPublish, 100L, DISCARD, false, 0);
        persistence.add("client1", false, publish, 100L, DISCARD, false, 0);
        persistence.add("client01", false, otherPublish, 100L, DISCARD, false, 0);
        final ImmutableList<PUBLISH> publishes =
                persistence.readNew("client1", false, ImmutableIntArray.of(2, 3, 4), 256000, 0);
        assertEquals(1, publishes.size());
        assertEquals(2, publishes.get(0).getPacketIdentifier());
        assertEquals(publish.getQoS(), publishes.get(0).getQoS());
        assertEquals(publish.getTopic(), publishes.get(0).getTopic());
    }

    @Test
    public void test_readNew_qos0() {
        final PUBLISH[] publishes = new PUBLISH[4];
        for (int i = 0; i < publishes.length; i++) {
            final PUBLISH publish = createPublish(0, QoS.AT_MOST_ONCE, "topic" + i);
            publishes[i] = publish;
            persistence.add("client", false, publish, 100L, DISCARD, false, 0);
        }

        final ImmutableList<PUBLISH> messages =
                persistence.readNew("client", false, ImmutableIntArray.of(1, 2, 3), 256000, 0);

        assertEquals(1, persistence.size("client", false, 0));
        assertEquals(3, messages.size());
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            assertTrue(Arrays.stream(publishes).anyMatch(x -> x.getTopic() == messages.get(finalI).getTopic()));
        }
    }

    @Test
    public void test_readNew_qos0_and_qos1() {
        final PUBLISH[] qos0Publishes = new PUBLISH[3];
        for (int i = 0; i < qos0Publishes.length; i++) {
            final PUBLISH publish = createPublish(0, QoS.AT_MOST_ONCE, "topic" + i);
            qos0Publishes[i] = publish;
            persistence.add("client", false, publish, 100L, DISCARD, false, 0);
        }

        final PUBLISH[] qos1Publishes = new PUBLISH[3];
        for (int i = 0; i < qos1Publishes.length; i++) {
            final PUBLISH publish = createPublish(1 + i, QoS.AT_LEAST_ONCE, "topic" + i);
            qos1Publishes[i] = publish;
            persistence.add("client", false, publish, 100L, DISCARD, false, 0);
        }

        final ImmutableList<PUBLISH> messages =
                persistence.readNew("client", false, ImmutableIntArray.of(1, 2, 3, 4, 5, 6, 7), 256000, 0);

        assertEquals(3, persistence.size("client", false, 0));
        assertEquals(6, messages.size());

    }

    @Test
    public void test_read_inflight() {
        final PUBLISH[] publishes = new PUBLISH[4];
        for (int i = 0; i < publishes.length; i++) {
            publishes[i] = createPublish(10 + i, (i % 2 == 0) ? QoS.EXACTLY_ONCE : QoS.AT_LEAST_ONCE, "topic" + i);
        }
        for (final PUBLISH publish : publishes) {
            persistence.add("client1", false, publish, 100L, DISCARD, false, 0);
        }

        final ImmutableList<PUBLISH> messages1 =
                persistence.readNew("client1", false, ImmutableIntArray.of(5, 6, 7), 256000, 0);

        assertEquals(3, messages1.size());
        assertEquals(5, messages1.get(0).getPacketIdentifier());
        assertEquals(6, messages1.get(1).getPacketIdentifier());
        assertEquals(7, messages1.get(2).getPacketIdentifier());
    }

    @Test
    public void test_add_discard() {
        for (int i = 1; i <= 6; i++) {
            persistence.add("client", false, createPublish(i, QoS.AT_LEAST_ONCE, "topic" + i), 3L, DISCARD, false, 0);
        }
        assertEquals(3, persistence.size("client", false, 0));

        final ImmutableList<PUBLISH> publishes =
                persistence.readNew("client", false, ImmutableIntArray.of(1, 2, 3, 4, 5, 6), byteLimit, 0);

        assertEquals(3, publishes.size());
        assertEquals(1, publishes.get(0).getPacketIdentifier());
        assertEquals(2, publishes.get(1).getPacketIdentifier());
        assertEquals(3, publishes.get(2).getPacketIdentifier());

        verify(messageDroppedService, times(3)).queueFull(eq("client"), anyString(), anyInt());
    }


    @Test
    public void test_clear() {
        for (int i = 0; i < 5; i++) {
            persistence.add("client1", false, createPublish(1, QoS.AT_LEAST_ONCE), 100L, DISCARD, false, 0);
        }

        persistence.add("client1", false, createPublish(0, QoS.AT_MOST_ONCE), 100L, DISCARD, false, 0);
        persistence.add("client2", false, createPublish(1, QoS.AT_LEAST_ONCE), 100L, DISCARD, false, 0);
        persistence.clear("client1", false, 0);

        final ImmutableList<PUBLISH> publishes1 =
                persistence.readNew("client1", false, ImmutableIntArray.of(1, 2, 3, 4, 5, 6), byteLimit, 0);
        assertEquals(0, publishes1.size());

        final ImmutableList<PUBLISH> publishes2 =
                persistence.readNew("client2", false, ImmutableIntArray.of(1, 2, 3, 4, 5, 6), byteLimit, 0);
        assertEquals(1, publishes2.size());
    }

    @Test
    public void test_replca_false_id() {
        persistence.add("client", false, createPublish(1, QoS.AT_LEAST_ONCE, "topic", 1), 100L, DISCARD, false, 0);
        persistence.readNew("client", false, ImmutableIntArray.of(1), 256000, 0);
        final String uniqueId = persistence.remove("client", 1, "hivemqId_pub_2", 0);
        assertNull(uniqueId);
        final ImmutableList<MessageWithID> messages = persistence.readInflight("client", false, 10, byteLimit, 0);
        assertEquals(1, messages.size());
        assertEquals(1, messages.get(0).getPacketIdentifier());
    }

    @Test
    public void test_remove_not_found() {
        for (int i = 0; i < 3; i++) {
            persistence.add("client", false, createPublish(1, QoS.AT_LEAST_ONCE, "topic", i), 100L, DISCARD, false, 0);
        }
        final String uniqueId = persistence.remove("client", 1, 0);
        assertNull(uniqueId);
    }

    @Test
    public void test_remove_false_id() {
        persistence.add("client", false, createPublish(1, QoS.AT_LEAST_ONCE, "topic", 1), 100L, DISCARD, false, 0);
        persistence.readNew("client", false, ImmutableIntArray.of(1), 256000, 0);
        final String uniqueId = persistence.remove("client", 1, "hivemqId_pub_2", 0);
        assertNull(uniqueId);
        final ImmutableList<MessageWithID> messages = persistence.readInflight("client", false, 10, byteLimit, 0);
        assertEquals(1, messages.size());
        assertEquals(1, messages.get(0).getPacketIdentifier());
    }

    @Test
    public void test_drop_qos_0_memory_exceeded() {

        final int queueLimit = (int) (Runtime.getRuntime().maxMemory() / 10000);

        persistence.add(
                "client", false, createBigPublish(0, QoS.AT_MOST_ONCE, "topic1", 1, queueLimit), 100L, DISCARD, false,
                0);
        persistence.add(
                "client", false, createBigPublish(1, QoS.AT_MOST_ONCE, "topic5", 2, queueLimit), 100L, DISCARD, false,
                0);

        verify(payloadPersistence).decrementReferenceCounter(2);
        verify(messageDroppedService).qos0MemoryExceeded(eq("client"), eq("topic5"), eq(0), anyLong(), anyLong());
    }

    @Test
    public void test_drop_qos_0_memory_exceeded_shared() {

        final int queueLimit = (int) (Runtime.getRuntime().maxMemory() / 10000);

        persistence.add(
                "client", false, createBigPublish(0, QoS.AT_MOST_ONCE, "topic1", 1, queueLimit), 100L, DISCARD, false,
                0);
        persistence.add(
                "group", true, createBigPublish(1, QoS.AT_MOST_ONCE, "topic5", 2, queueLimit), 100L, DISCARD, false, 0);

        verify(payloadPersistence).decrementReferenceCounter(2);
        verify(messageDroppedService).qos0MemoryExceededShared(eq("group"), eq("topic5"), eq(0), anyLong(), anyLong());
    }

    @Test
    public void test_read_new_expired_mixed_qos() {
        persistence.add(
                "client1", false, createPublish(0, QoS.AT_MOST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client1", false, createPublish(0, QoS.AT_LEAST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);

        persistence.add(
                "client2", false, createPublish(0, QoS.AT_MOST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);

        final ImmutableList<PUBLISH> messages1 =
                persistence.readNew("client1", false, ImmutableIntArray.of(1, 2), 10000L, 0);
        final ImmutableList<PUBLISH> messages2 =
                persistence.readNew("client2", false, ImmutableIntArray.of(1, 2), 10000L, 0);

        assertEquals(0, messages1.size());
        assertEquals(0, messages2.size());
    }

    @Test
    public void test_read_new_part_expired_qos0() {
        persistence.add(
                "client1", false, createPublish(0, QoS.AT_MOST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client1", false, createPublish(0, QoS.AT_MOST_ONCE, 100, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);

        persistence.add(
                "client2", false, createPublish(0, QoS.AT_MOST_ONCE, 100, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client2", false, createPublish(0, QoS.AT_MOST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client2", false, createPublish(0, QoS.AT_MOST_ONCE, 110, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);

        final ImmutableList<PUBLISH> messages1 =
                persistence.readNew("client1", false, ImmutableIntArray.of(1, 2), 10000L, 0);
        final ImmutableList<PUBLISH> messages2 =
                persistence.readNew("client2", false, ImmutableIntArray.of(1, 2), 10000L, 0);

        assertEquals(1, messages1.size());
        assertEquals(2, messages2.size());
    }

    @Test
    public void test_read_new_part_expired_qos1() {
        persistence.add(
                "client1", false, createPublish(0, QoS.AT_LEAST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client1", false, createPublish(0, QoS.AT_LEAST_ONCE, 100, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);

        persistence.add(
                "client2", false, createPublish(0, QoS.AT_LEAST_ONCE, 100, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client2", false, createPublish(0, QoS.AT_LEAST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client2", false, createPublish(0, QoS.AT_LEAST_ONCE, 110, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);

        final ImmutableList<PUBLISH> messages1 =
                persistence.readNew("client1", false, ImmutableIntArray.of(1, 2), 10000L, 0);
        final ImmutableList<PUBLISH> messages2 =
                persistence.readNew("client2", false, ImmutableIntArray.of(1, 2), 10000L, 0);

        assertEquals(1, messages1.size());
        assertEquals(2, messages2.size());
    }

    @Test
    public void test_read_new_part_expired_qos2() {
        persistence.add(
                "client1", false, createPublish(0, QoS.EXACTLY_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client1", false, createPublish(0, QoS.EXACTLY_ONCE, 100, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);

        persistence.add(
                "client2", false, createPublish(0, QoS.EXACTLY_ONCE, 100, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client2", false, createPublish(0, QoS.EXACTLY_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client2", false, createPublish(0, QoS.EXACTLY_ONCE, 110, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);

        final ImmutableList<PUBLISH> messages1 =
                persistence.readNew("client1", false, ImmutableIntArray.of(1, 2, 3), 10000L, 0);
        final ImmutableList<PUBLISH> messages2 =
                persistence.readNew("client2", false, ImmutableIntArray.of(1, 2, 3), 10000L, 0);

        assertEquals(1, messages1.size());
        assertEquals(2, messages2.size());
    }

    @Test
    public void test_read_new_part_expired_mixed_qos() {
        persistence.add(
                "client1", false, createPublish(0, QoS.AT_LEAST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client1", false, createPublish(0, QoS.AT_MOST_ONCE, 100, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);

        persistence.add(
                "client2", false, createPublish(0, QoS.AT_MOST_ONCE, 100, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client2", false, createPublish(0, QoS.AT_LEAST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client2", false, createPublish(0, QoS.AT_LEAST_ONCE, 110, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);


        persistence.add(
                "client3", false, createPublish(0, QoS.EXACTLY_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client3", false, createPublish(0, QoS.EXACTLY_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client3", false, createPublish(0, QoS.EXACTLY_ONCE, 100, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client3", false, createPublish(0, QoS.EXACTLY_ONCE, 110, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);

        final ImmutableList<PUBLISH> messages1 =
                persistence.readNew("client1", false, ImmutableIntArray.of(1, 2, 3), 10000L, 0);
        final ImmutableList<PUBLISH> messages2 =
                persistence.readNew("client2", false, ImmutableIntArray.of(1, 2, 3), 10000L, 0);
        final ImmutableList<PUBLISH> messages3 =
                persistence.readNew("client3", false, ImmutableIntArray.of(1, 2, 3), 10000L, 0);

        assertEquals(1, messages1.size());
        assertEquals(2, messages2.size());
        assertEquals(2, messages3.size());
    }


    @Test
    public void test_clean_up_expired_qos0() {

        persistence.add(
                "client1", false, createPublish(0, QoS.AT_MOST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client1", false, createPublish(0, QoS.AT_MOST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);

        final ImmutableSet<String> sharedQueues = persistence.cleanUp(0);

        assertTrue(sharedQueues.isEmpty());
        verify(payloadPersistence, times(2)).decrementReferenceCounter(
                anyLong()); // 2 expired
        assertEquals(0, persistence.size("client1", false, 0));
    }

    @Test
    public void test_clean_up_expired_qos1() {

        persistence.add(
                "client1", false, createPublish(0, QoS.AT_LEAST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);
        persistence.add(
                "client1", false, createPublish(0, QoS.AT_LEAST_ONCE, 10, System.currentTimeMillis() - 10000), 10,
                DISCARD, false, 0);

        final ImmutableSet<String> sharedQueues = persistence.cleanUp(0);

        assertTrue(sharedQueues.isEmpty());
        verify(payloadPersistence, times(2)).decrementReferenceCounter(
                anyLong()); // 2 expired
        assertEquals(0, persistence.size("client1", false, 0));
    }

    @Test
    public void test_clean_up_shared() {
        persistence.add(
                "name/topic1", true, createPublish(0, QoS.AT_LEAST_ONCE, 1000, System.currentTimeMillis()), 10, DISCARD,
                false,
                0);
        persistence.add(
                "name/topic2", true, createPublish(1, QoS.AT_LEAST_ONCE, 1000, System.currentTimeMillis()), 10, DISCARD,
                false,
                0);

        final ImmutableSet<String> sharedQueues = persistence.cleanUp(0);
        assertEquals(2, sharedQueues.size());
    }

    @Test
    public void test_overlapping_ids() {

        persistence.add("id", false, createPublish(1, QoS.AT_LEAST_ONCE, "not_shared"), 10, DISCARD, false, 0);
        persistence.add("id", false, createPublish(0, QoS.AT_MOST_ONCE, "not_shared"), 10, DISCARD, false, 0);

        persistence.add("id", true, createPublish(1, QoS.AT_LEAST_ONCE, "shared"), 10, DISCARD, false, 0);
        persistence.add("id", true, createPublish(0, QoS.AT_MOST_ONCE, "shared"), 10, DISCARD, false, 0);

        final ImmutableList<PUBLISH> notSharedMessages =
                persistence.readNew("id", false, ImmutableIntArray.of(1, 2, 3), 10000L, 0);
        final ImmutableList<PUBLISH> sharedMessages =
                persistence.readNew("id", true, ImmutableIntArray.of(1, 2, 3), 10000L, 0);

        assertEquals(2, notSharedMessages.size());
        assertEquals(2, sharedMessages.size());

        assertEquals("not_shared", notSharedMessages.get(0).getTopic());
        assertEquals("not_shared", notSharedMessages.get(1).getTopic());

        assertEquals("shared", sharedMessages.get(0).getTopic());
        assertEquals("shared", sharedMessages.get(1).getTopic());

        assertEquals(1, persistence.size("id", false, 0));
        assertEquals(1, persistence.size("id", true, 0));
    }

    @Test
    public void test_remove_shared() {
        for (int i = 0; i < 3; i++) {
            persistence.add(
                    "group/topic", true, createPublish(1, QoS.AT_LEAST_ONCE, "topic", i), 100L, DISCARD, false, 0);
        }
        persistence.removeShared("group/topic", "hivemqId_pub_2", 0);
        final ImmutableList<PUBLISH> messages =
                persistence.readNew("group/topic", true, ImmutableIntArray.of(1, 2, 3), 10000L, 0);

        assertEquals(2, messages.size());

        assertEquals(2, persistence.size("group/topic", true, 0));

        verify(payloadPersistence, times(1)).decrementReferenceCounter(anyLong());
    }

    @Test
    public void test_remove_in_flight_marker() {
        for (int i = 0; i < 3; i++) {
            persistence.add(
                    "group/topic", true, createPublish(1, QoS.AT_LEAST_ONCE, "topic", i), 100L, DISCARD, false, 0);
        }
        persistence.readNew("group/topic", true,
                ImmutableIntArray.of(SHARED_IN_FLIGHT_MARKER, SHARED_IN_FLIGHT_MARKER, SHARED_IN_FLIGHT_MARKER),
                256000, 0);

        persistence.removeInFlightMarker("group/topic", "hivemqId_pub_2", 0);
        final ImmutableList<MessageWithID> messages = persistence.readInflight("group/topic", true, 10, byteLimit, 0);

        assertEquals(2, messages.size());
        assertEquals(SHARED_IN_FLIGHT_MARKER, messages.get(0).getPacketIdentifier());
        assertEquals(SHARED_IN_FLIGHT_MARKER, messages.get(1).getPacketIdentifier());

        assertEquals(3, persistence.size("group/topic", true, 0));

        verify(payloadPersistence, never()).decrementReferenceCounter(anyLong());
    }

    @Test
    public void test_remove_all_qos_0_messages() {

        final Gauge<Long> gauge = metricRegistry.getGauges().get(HiveMQMetrics.QUEUED_MESSAGES_MEMORY_PERSISTENCE_TOTAL_SIZE.name());

        final PUBLISH publish1 = createPublish(1, QoS.AT_LEAST_ONCE, "topic1", 1);
        final PUBLISH publish2 = createPublish(0, QoS.AT_MOST_ONCE, "topic2", 1);
        final PUBLISH publish3 = createPublish(0, QoS.AT_MOST_ONCE, "topic3", 1);

        persistence.add("client1", false, publish1, 100L, DISCARD, false, 0);
        persistence.add("client1", false, publish2, 100L, DISCARD, false, 0);
        persistence.add("client1", false, publish3, 100L, DISCARD, false, 0);

        final int size = new PublishWithRetained(publish1, false).getEstimatedSize() + ObjectMemoryEstimation.linkedListNodeOverhead() +
                new PublishWithRetained(publish2, false).getEstimatedSize() + ObjectMemoryEstimation.linkedListNodeOverhead() +
                new PublishWithRetained(publish3, false).getEstimatedSize() + ObjectMemoryEstimation.linkedListNodeOverhead();

        assertEquals(size, gauge.getValue().longValue());

        persistence.removeAllQos0Messages("client1", false, 0);

        final ImmutableList<PUBLISH> messages =
                persistence.readNew("client1", false, ImmutableIntArray.of(1, 2, 3), 10000L, 0);
        assertEquals(1, messages.size());

        verify(payloadPersistence, times(2)).decrementReferenceCounter(anyLong());

        assertTrue(gauge.getValue() > 0);
        assertEquals(new PublishWithRetained(messages.get(0), false).getEstimatedSize() + ObjectMemoryEstimation.linkedListNodeOverhead(), gauge.getValue().longValue());
    }

    @Test
    public void test_batched_drop_qos_0_memory_exceeded() {

        final int queueLimit = (int) (Runtime.getRuntime().maxMemory() / 10000);
        final ImmutableList.Builder<PUBLISH> publishes = ImmutableList.builder();
        publishes.add(createBigPublish(0, QoS.AT_MOST_ONCE, "topic1", 1, queueLimit));
        publishes.add(createBigPublish(1, QoS.AT_MOST_ONCE, "topic2", 2, queueLimit));
        persistence.add("client", false, publishes.build(), 100L, DISCARD, false, 0);

        verify(payloadPersistence).decrementReferenceCounter(2);
        verify(messageDroppedService).qos0MemoryExceeded(eq("client"), eq("topic2"), eq(0), anyLong(), anyLong());

        assertEquals(1, persistence.size("client", false, 0));
        final ImmutableList<PUBLISH> all =
                persistence.readNew("client", false, ImmutableIntArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 10000L, 0);
        assertEquals(1, all.size());
    }

    @Test
    public void add_and_poll_mixture_retained() {
        for (int i = 0; i < 12; i++) {
            persistence.add(
                    "client", false, createPublish(1, QoS.EXACTLY_ONCE, "topic" + i), 5, DISCARD_OLDEST, i % 2 != 0, 0);
        }
        final ImmutableList<PUBLISH> all = persistence.readNew("client", false, ImmutableIntArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12), 10000L, 0);
        assertEquals(10, persistence.size("client", false, 0));
        assertEquals(10, all.size());

        final Set<PUBLISH> notExpectedMessages = all.stream()
                .filter(publish -> publish.getTopic().equals("10") || publish.getTopic().equals("11"))
                .collect(Collectors.toSet());
        assertTrue(notExpectedMessages.isEmpty());

        final Gauge<Long> gauge = metricRegistry.getGauges().get(HiveMQMetrics.QUEUED_MESSAGES_MEMORY_PERSISTENCE_TOTAL_SIZE.name());
        assertTrue(gauge.getValue() > 0);
    }

    @Test(timeout = 5000)
    public void test_add_qos_0_per_client_exceeded() {

        persistence.add("client", false, createBigPublish(1, QoS.AT_MOST_ONCE, "topic", 1, 500), 1000, DISCARD, false, BucketUtils.getBucket("client", 4));
        persistence.add("client", false, createBigPublish(1, QoS.AT_MOST_ONCE, "topic", 1, 500), 1000, DISCARD, false, BucketUtils.getBucket("client", 4));

        verify(messageDroppedService).qos0MemoryExceeded(eq("client"), eq("topic"), eq(0), anyLong(), eq(1024L));

        final Gauge<Long> gauge = metricRegistry.getGauges().get(HiveMQMetrics.QUEUED_MESSAGES_MEMORY_PERSISTENCE_TOTAL_SIZE.name());
        assertTrue(gauge.getValue() > 0);

    }

    @Test(timeout = 5000)
    public void test_add_qos_0_per_client_exactly_exceeded() {


        final PUBLISH exactly1024bytesPublish = createPublish(1, QoS.AT_MOST_ONCE, "topic", 1, new byte[745]);

        assertEquals(1024, exactly1024bytesPublish.getEstimatedSizeInMemory());

        persistence.add("client", false, exactly1024bytesPublish, 1000, DISCARD, false, BucketUtils.getBucket("client", 4));
        persistence.add("client", false, createPublish(2, QoS.AT_MOST_ONCE, "topic", 2), 1000, DISCARD, false, BucketUtils.getBucket("client", 4));

        verify(messageDroppedService).qos0MemoryExceeded(eq("client"), eq("topic"), eq(0), anyLong(), eq(1024L));

        final Gauge<Long> gauge = metricRegistry.getGauges().get(HiveMQMetrics.QUEUED_MESSAGES_MEMORY_PERSISTENCE_TOTAL_SIZE.name());
        assertTrue(gauge.getValue() > 0);

    }

    @Test
    public void test_add_close_add() {

        final ImmutableList.Builder<PUBLISH> publishes = ImmutableList.builder();
        for (int i = 0; i < 100; i++) {
            publishes.add(createPublish(1, QoS.AT_LEAST_ONCE, "topic" + i));
        }
        persistence.add("client", false, publishes.build(), 2, DISCARD, true, 0);

        final Gauge<Long> gauge = metricRegistry.getGauges().get(HiveMQMetrics.QUEUED_MESSAGES_MEMORY_PERSISTENCE_TOTAL_SIZE.name());
        assertTrue(gauge.getValue() > 0);

        for (int i = 0; i < bucketCount; i++) {
            persistence.closeDB(i);
        }

        assertEquals(0, gauge.getValue().longValue());

    }

    private ImmutableIntArray createPacketIds(final int start, final int size) {
        final ImmutableIntArray.Builder builder = ImmutableIntArray.builder();
        for (int i = start; i < (size + start); i++) {
            builder.add(i);
        }
        return builder.build();
    }

    private PUBLISH createPublish(final int packetId, final QoS qos) {
        return createPublish(packetId, qos, "topic");
    }

    private PUBLISH createPublish(final int packetId, final QoS qos, final long expiryInterval, final long timestamp) {
        return new PUBLISHFactory.Mqtt5Builder().withPacketIdentifier(packetId)
                .withQoS(qos)
                .withPublishId(1L)
                .withPayload("message".getBytes())
                .withTopic("topic")
                .withHivemqId("hivemqId")
                .withPersistence(payloadPersistence)
                .withMessageExpiryInterval(expiryInterval)
                .withTimestamp(timestamp)
                .build();
    }

    private PUBLISH createPublish(final int packetId, final QoS qos, final String topic) {
        return new PUBLISHFactory.Mqtt5Builder().withPacketIdentifier(packetId)
                .withQoS(qos)
                .withPublishId(1L)
                .withPayload("message".getBytes())
                .withTopic(topic)
                .withHivemqId("hivemqId")
                .withPersistence(payloadPersistence)
                .build();
    }

    private PUBLISH createPublish(final int packetId, final QoS qos, final String topic, final int publishId) {
        return new PUBLISHFactory.Mqtt5Builder().withPacketIdentifier(packetId)
                .withQoS(qos)
                .withPublishId(1L)
                .withPayload("message".getBytes())
                .withTopic(topic)
                .withHivemqId("hivemqId")
                .withPersistence(payloadPersistence)
                .withPublishId(publishId)
                .build();
    }


    private PUBLISH createPublish(final int packetId, final QoS qos, final String topic, final int publishId, final byte[] message) {
        return new PUBLISHFactory.Mqtt5Builder().withPacketIdentifier(packetId)
                .withQoS(qos)
                .withPublishId(1L)
                .withPayload(message)
                .withTopic(topic)
                .withHivemqId("hivemqId")
                .withPersistence(payloadPersistence)
                .withPublishId(publishId)
                .build();
    }

    private PUBLISH createBigPublish(
            final int packetId, final QoS qos, final String topic, final int publishId, final int queueLimit) {
        return new PUBLISHFactory.Mqtt5Builder().withPacketIdentifier(packetId)
                .withQoS(qos)
                .withPublishId(1L)
                .withPayload(RandomStringUtils.randomAlphanumeric(queueLimit).getBytes())
                .withCorrelationData(RandomStringUtils.randomAlphanumeric(65000).getBytes())
                .withResponseTopic(RandomStringUtils.randomAlphanumeric(65000))
                .withTopic(topic)
                .withHivemqId("hivemqId")
                .withPublishId(publishId)
                .withPersistence(payloadPersistence)
                .build();
    }
}