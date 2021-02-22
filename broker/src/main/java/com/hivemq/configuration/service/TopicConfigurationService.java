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
     * @return an integer that specifies max topics
     */
    int maxTopics();

    void setMaxTopics(final int maxTopics);
}


