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
package com.hivemq.util;

import com.hivemq.configuration.service.TopicPriorityConfigurationService;
import com.hivemq.extensions.priority.TopicPriority;
import com.hivemq.mqtt.message.publish.PUBLISH;
import com.hivemq.mqtt.topic.TokenizedTopicMatcher;
import com.hivemq.mqtt.topic.TopicMatcher;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.Optional;

public class PublishComparator implements Comparator<PUBLISH> {

    private final TopicPriorityConfigurationService topicPriorityConfigurationService;
    private final TokenizedTopicMatcher topicMatcher;

    @Inject
    public PublishComparator(TopicPriorityConfigurationService topicPriorityConfigurationService, TokenizedTopicMatcher topicMatcher) {
        this.topicPriorityConfigurationService = topicPriorityConfigurationService;
        this.topicMatcher = topicMatcher;
    }

    @Override
    public int compare(PUBLISH t1, PUBLISH t2) {
        // Find priorities matching the topic of the messages
        Optional<TopicPriority> p1 = topicPriorityConfigurationService.getPriorities()
                .stream()
                .filter(p -> topicMatcher.matches(p.getTopicFilter(), t1.getTopic()))
                .max(Comparator.comparingInt(c -> c.getTopicFilter().length()));

        Optional<TopicPriority> p2 = topicPriorityConfigurationService.getPriorities()
                .stream()
                .filter(p -> topicMatcher.matches(p.getTopicFilter(), t2.getTopic()))
                .max(Comparator.comparingInt(c -> c.getTopicFilter().length()));

        if(p1.isEmpty() && p2.isPresent()) return 1; // t1 Does not have a priority, while t2 does
        if(p1.isPresent() && p2.isEmpty()) return -1; // t1 Does have a priority, while t2 does not
        if(p1.isEmpty() && p2.isEmpty()) return 0; // They both do not have priorities


        // If they are not the same priority class use it to compare
        int priorityClassComparison = p1.get().getPriorityClass().compareTo(p2.get().getPriorityClass());
        if( priorityClassComparison != 0) return priorityClassComparison;

        return p2.get().getPriority() - p1.get().getPriority();
    }
}
