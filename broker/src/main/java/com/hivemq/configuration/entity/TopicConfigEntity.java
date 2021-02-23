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

import com.hivemq.configuration.entity.topic.MaxTopicsConfigEntity;
import com.hivemq.configuration.service.TopicConfigurationService;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.configuration.entity.mqtt.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Christoffer Stensrud
 */
@XmlRootElement(name = "topic")
@XmlAccessorType(XmlAccessType.NONE)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class TopicConfigEntity {

    @XmlElementRef(required = false)
    private @NotNull MaxTopicsConfigEntity maxTopicsConfigEntity = new MaxTopicsConfigEntity();
    public @NotNull MaxTopicsConfigEntity getMaxTopicsConfigEntity() {
        return maxTopicsConfigEntity;
    }

}
