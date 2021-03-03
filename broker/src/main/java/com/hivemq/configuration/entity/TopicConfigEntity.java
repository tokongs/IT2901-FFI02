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
package com.hivemq.configuration.entity;

import com.hivemq.configuration.entity.topic.NestedTopicConfigEntity;
import com.hivemq.extension.sdk.api.annotations.NotNull;

import javax.xml.bind.annotation.*;


import static com.hivemq.configuration.entity.topic.TopicConfigurationDefaults.MAX_TOPICS_DEFAULT;

/**
 * @author Christoffer Stensrud
 * Currently UNUSED
 */
@XmlRootElement(name = "topic-config")
@XmlAccessorType(XmlAccessType.NONE)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class TopicConfigEntity {

    /**
     * Extra stuff if needed.
     */
    @XmlElementRef(required = false)
    private @NotNull NestedTopicConfigEntity nestedTopicConfigEntity = new NestedTopicConfigEntity();
    public @NotNull NestedTopicConfigEntity getNestedTopicConfigEntity() { return nestedTopicConfigEntity; }



    @XmlElement(name = "max-topics", defaultValue = "8")
    private int maxTopics = MAX_TOPICS_DEFAULT;

    public int getMaxTopics() {
        return maxTopics;
    }

}