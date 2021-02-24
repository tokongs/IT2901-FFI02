package com.hivemq.configuration.service;

import com.hivemq.annotations.ReadOnly;
import com.hivemq.configuration.service.entity.Listener;
import com.hivemq.configuration.service.exception.ConfigurationValidationException;
import com.hivemq.mqtt.message.subscribe.Topic;
import java.util.List;

/**
 * The service which allows to inspect Topic configuration at runtime.
 *
 * @author Christoffer Stensrud
 *
 */
public interface TopicConfigurationService {

    /**
     * Adds a new Topic at runtime
     *
     * @param topic    the topic
     * @param <T>      the concrete topic subclass
     * @throws ConfigurationValidationException if the validation of the topic wasn't successful
     * @throws IllegalArgumentException
     */
    <T extends Topic> void addTopic(final T topic) throws ConfigurationValidationException, IllegalArgumentException;


    /**
     * @return an unmodifiable list of all available topics
     */
    @ReadOnly
    List<Topic> getTopics();

    /**
     * @return an integer that specifies max topics
     */
    int maxTopics();

    void setMaxTopics(final int maxTopics);
}


