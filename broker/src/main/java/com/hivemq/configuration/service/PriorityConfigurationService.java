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
import com.hivemq.extensions.priority.TopicPriority;
import com.hivemq.mqtt.message.subscribe.Topic;
import java.util.List;

/**
 * The service which allows to inspect Topic configuration at runtime.
 *
 * @author Christoffer Stensrud
 *
 */
public interface PriorityConfigurationService {

    /**
     * Adds a new Priority at runtime
     *
     * @param priority    the priority
     * @throws ConfigurationValidationException if the validation of the priority wasn't successful
     */
    void addPriority(final TopicPriority priority) throws ConfigurationValidationException;

    /**
     * @return an unmodifiable list of all available priorities
     */
    @ReadOnly
    List<TopicPriority> getPriorities();







}


