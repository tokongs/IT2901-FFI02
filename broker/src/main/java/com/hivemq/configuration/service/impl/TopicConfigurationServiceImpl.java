package com.hivemq.configuration.service.impl;

import com.hivemq.configuration.service.MqttConfigurationService;
import com.hivemq.configuration.service.TopicConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.hivemq.configuration.entity.topic.TopicConfigurationDefaults.*;

import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class TopicConfigurationServiceImpl implements TopicConfigurationService {

    private static final Logger log = LoggerFactory.getLogger(MqttConfigurationServiceImpl.class);

    private final AtomicInteger maxTopics = new AtomicInteger(MAX_TOPICS_MAXIMUM);

    @Override
    public int maxTopics() {
        return maxTopics.get();
    }

    @Override
    public void setMaxTopics(final int maxTopics) {
        log.debug("Setting the maximum number of topics to {}", maxTopics);
        this.maxTopics.set(maxTopics);
    }
}
