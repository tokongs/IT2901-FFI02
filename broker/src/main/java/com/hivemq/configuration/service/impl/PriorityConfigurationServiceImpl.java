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
package com.hivemq.configuration.service.impl;

import com.google.common.collect.ImmutableList;
import com.hivemq.configuration.service.PriorityConfigurationService;
import com.hivemq.configuration.service.exception.ConfigurationValidationException;
import com.hivemq.extensions.priority.TopicPriority;
import com.hivemq.mqtt.message.subscribe.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.hivemq.configuration.entity.topic.TopicConfigurationDefaults.*;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class PriorityConfigurationServiceImpl implements PriorityConfigurationService {

    private static final Logger log = LoggerFactory.getLogger(PriorityConfigurationServiceImpl.class);


    /**
     * The actual topic. Maybe make it a COWAL?
     */
    final List<TopicPriority> priorities = new ArrayList<>();

    @Override
    public void addPriority(TopicPriority priority) throws ConfigurationValidationException {
        priorities.add(priority);
    }

    @Override
    public List<TopicPriority> getPriorities() {
        return priorities;
    }
}