package com.hivemq.configuration.service.impl;

import com.google.common.collect.ImmutableList;
import com.hivemq.configuration.service.TopicConfigurationService;
import com.hivemq.mqtt.message.subscribe.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.hivemq.configuration.entity.topic.TopicConfigurationDefaults.*;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class TopicConfigurationServiceImpl implements TopicConfigurationService {

    private static final Logger log = LoggerFactory.getLogger(TopicConfigurationServiceImpl.class);


    /**
     * The actual topic. Maybe make it a COWAL?
     */
    final List<Topic> topics = new ArrayList<>();

    private final AtomicInteger maxTopics = new AtomicInteger(MAX_TOPICS_MAXIMUM);


    @Override
    public <T extends Topic> void addTopic(final T topic) {
        if (topic.getClass().equals(Topic.class)) {

            log.debug("Adding topic {}, with QoS {} and priority {}.",
                    topic.getTopic(), topic.getQoS(), topic.getPriority());
            topics.add(topic);

            /*final ImmutableList<Topic> allTopics = ImmutableList.copyOf(topics);
            log.trace("Notifying {} update listeners for changes", allTopics.size());*/

            /*//We're calling the Update Listeners in the same thread
            for (final InternalListenerConfigurationService.UpdateListener updateListener : updateListeners) {

                log.trace("Notifying update listener {}", allListeners.getClass());
                updateListener.update(listener, allListeners);
            }*/

        } else {
            throw new IllegalArgumentException(topic.getClass().getName() + " is not a valid listener type");
        }

    }

    @Override
    public ImmutableList<Topic> getTopics() {
        return ImmutableList.copyOf(topics);
    }

    /* UNUSED
    @Override
    public int maxTopics() {
        return maxTopics.get();
    }

    @Override
    public void setMaxTopics(final int maxTopics) {
        log.debug("Setting the maximum number of topics to {}", maxTopics);
        this.maxTopics.set(maxTopics);
    }*/
}
