package com.hivemq.configuration.service;

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


