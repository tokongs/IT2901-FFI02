package com.hivemq.configuration.reader;

import com.google.inject.Inject;
import com.hivemq.configuration.entity.TopicConfigEntity;
import com.hivemq.configuration.service.TopicConfigurationService;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.hivemq.configuration.entity.topic.TopicConfigurationDefaults.*;

/**
 *
 * @author Christoffer Stensrud
 *
 */
public class TopicConfigurator {

    private final @NotNull TopicConfigurationService topicConfigurationService;
    private static final Logger log = LoggerFactory.getLogger(ListenerConfigurator.class);

    @Inject
    public TopicConfigurator(final @NotNull TopicConfigurationService topicConfigurationService){
        this.topicConfigurationService = topicConfigurationService;
    }

    void setTopicConfig(@NotNull final TopicConfigEntity topicConfigEntity) {

        final int maxTopics = topicConfigEntity.getMaxTopicsConfigEntity().getMaxTopics();
        topicConfigurationService.setMaxTopics(validateMaxTopics(maxTopics));

    }

    private int validateMaxTopics(final int maxTopics) {
        if (maxTopics < MAX_TOPICS_MINIMUM) {
            log.warn("The configured topic maximum ({}) is too small. It was set to {} instead.", maxTopics, MAX_TOPICS_MINIMUM);
            return MAX_TOPICS_MINIMUM;
        }
        if (maxTopics > MAX_TOPICS_MAXIMUM) {
            log.warn("The configured topic maximum ({}) is too large. It was set to {} instead.", maxTopics, MAX_TOPICS_MAXIMUM);
            return MAX_TOPICS_MAXIMUM;
        }
        return maxTopics;
    }

}
