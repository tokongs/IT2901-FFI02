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
     * UNUSED
     */
    int maxTopics();

    void setMaxTopics(final int maxTopics);
}


