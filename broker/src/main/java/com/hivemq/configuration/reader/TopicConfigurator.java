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
package com.hivemq.configuration.reader;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.hivemq.configuration.entity.TopicConfigEntity;
import com.hivemq.configuration.entity.listener.*;
import com.hivemq.configuration.entity.topic.TopicEntity;
import com.hivemq.configuration.service.TopicConfigurationService;
import com.hivemq.configuration.service.entity.Listener;
import com.hivemq.configuration.service.entity.TcpListener;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import com.hivemq.mqtt.message.QoS;
import com.hivemq.mqtt.message.subscribe.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.hivemq.configuration.entity.topic.TopicConfigurationDefaults.*;

/**
 *
 * @author Christoffer Stensrud
 *
 */
public class TopicConfigurator {

    private static final Logger log = LoggerFactory.getLogger(ListenerConfigurator.class);

    private final @NotNull TopicConfigurationService topicConfigurationService;

    private final @NotNull List<String> chosenTopics;

    @Inject
    public TopicConfigurator(final @NotNull TopicConfigurationService topicConfigurationService){
        this.topicConfigurationService = topicConfigurationService;
        this.chosenTopics = new ArrayList<>();
    }

    void setTopicConfig(/*NotNull final TopicConfigEntity topicConfigEntity,*/ final @NotNull List<TopicEntity> entities) {
        final ImmutableList<Topic> topics = convertTopicEntities(entities);

        for (final Topic topic : topics) {
            topicConfigurationService.addTopic(topic);
        }

        /*final int maxTopics = topicConfigEntity.getMaxTopicsConfigEntity().getMaxTopics(); */ //old
        /*final int maxTopics = topicConfigEntity.getMaxTopics();
        topicConfigurationService.setMaxTopics(validateMaxTopics(maxTopics));*/

    }
    private @NotNull ImmutableList<Topic> convertTopicEntities(final @NotNull List<TopicEntity> entities) {
        final ImmutableList.Builder<Topic> builder = ImmutableList.builder();

        for (final TopicEntity entity : entities) {
            final Topic topic = convertTopic(entity);
            if (topic != null) {
                builder.add(topic);
            }
        }

        return builder.build();
    }

    @NotNull Topic convertTopic(final @NotNull TopicEntity entity) {
        return new Topic(
                getName(entity),
                getQoS(entity.getQoS()),
                entity.getPriority(),
                entity.getNoLocal(),
                entity.getRetainAsPublished());
    }

    @NotNull
    private QoS getQoS(int qosAsInt) {
        return QoS.valueOf(qosAsInt);
    }

    @NotNull
    private String getName(final @NotNull TopicEntity entity) {

        final String chosenTopic =
                (entity.getName() == null || entity.getName().trim().isEmpty()) ? "topic_with_priority_" + entity.getPriority() :
                        entity.getName();

        if (chosenTopics.contains(chosenTopic)) {

            int count = 1;
            String newTopic = chosenTopic + "-" + count++;
            while (chosenTopics.contains(newTopic)) {
                newTopic = chosenTopic + "-" + count++;
            }

            log.warn(
                    "Topic '{}' already in use. Renaming topic with QoS '{}' and priority '{}' to: '{}'",
                    chosenTopic,
                    entity.getQoS(),
                    entity.getPriority(),
                    newTopic);
            chosenTopics.add(newTopic);
            return newTopic;
        } else {
            chosenTopics.add(chosenTopic);
            return chosenTopic;
        }

    }
    
    /* UNUSED
    private int validateMaxTopics(final int maxTopics) {
        if (maxTopics < MAX_TOPICS_MINIMUM) {
            log.warn("The configured maximum topics ({}) is too small. It was set to {} instead.", maxTopics, MAX_TOPICS_DEFAULT);
            return MAX_TOPICS_DEFAULT;
        }
        if (maxTopics > MAX_TOPICS_MAXIMUM) {
            log.warn("The configured maximum topics ({}) is too large. It was set to {} instead.", maxTopics, MAX_TOPICS_DEFAULT);
            return MAX_TOPICS_DEFAULT;
        }
        return maxTopics;
    }*/

}
