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
import com.hivemq.configuration.entity.TopicPriorityEntity;
import com.hivemq.configuration.service.TopicPriorityConfigurationService;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extensions.priority.PriorityClass;
import com.hivemq.extensions.priority.TopicPriority;

import java.util.List;

/**
 *
 * @author Christoffer Stensrud
 *
 */
public class TopicPriorirtyConfigurator {

    private final @NotNull TopicPriorityConfigurationService topicPriorityConfigurationService;

    @Inject
    public TopicPriorirtyConfigurator(final @NotNull TopicPriorityConfigurationService topicPriorityConfigurationService){
        this.topicPriorityConfigurationService = topicPriorityConfigurationService;
    }

    void setTopicPriorityConfig(final @NotNull List<TopicPriorityEntity> entities) {
        final ImmutableList<TopicPriority> priorities = convertPriorityEntities(entities);

        for (final TopicPriority priority : priorities) {
            topicPriorityConfigurationService.addPriority(priority);
        }


    }
    private @NotNull ImmutableList<TopicPriority> convertPriorityEntities(final @NotNull List<TopicPriorityEntity> entities) {
        final ImmutableList.Builder<TopicPriority> builder = ImmutableList.builder();

        for (final TopicPriorityEntity entity : entities) {
            final TopicPriority priority = convertPriority(entity);
            if (priority != null) {
                builder.add(priority);
            }
        }

        return builder.build();
    }

    @NotNull TopicPriority convertPriority(final @NotNull TopicPriorityEntity entity) {
        return new TopicPriority(
                entity.getFilter(),
                PriorityClass.valueOf(entity.getPriorityClass()),
                entity.getPriority());
    }


}
