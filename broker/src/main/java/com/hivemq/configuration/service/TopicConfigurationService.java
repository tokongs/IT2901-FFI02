package com.hivemq.configuration.service;

import com.hivemq.annotations.ReadOnly;
import com.hivemq.configuration.service.entity.*;
import com.hivemq.configuration.service.exception.ConfigurationValidationException;
import com.hivemq.mqtt.message.QoS;

import java.util.List;

/**
 * The service which allows to inspect Topic configuration at runtime.
 *
 * @author Christoffer Stensrud
 *
 */
public interface TopicConfigurationService {

    /**
     * Adds a new Listener at runtime
     *
     * @param listener the listener
     * @param <T>      the concrete listener subclass
     * @throws ConfigurationValidationException if the validation of the listener wasn't successful
     * @throws IllegalArgumentException
     */
    <T extends Listener> void addListener(final T listener) throws ConfigurationValidationException, IllegalArgumentException;

    /**
     * @return a unmodifiable list of all active listeners
     */
    @ReadOnly
    List<Listener> getListeners();


    /**
     * @return an integer that specifies max topics
     */
    int maxTopics();


    void setMaxTopics(final int maxTopics);
}


