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
package com.hivemq.configuration.entity.topic;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import com.hivemq.mqtt.message.QoS;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Christoffer Stensrud
 */
@XmlRootElement(name = "topic")
@XmlAccessorType(XmlAccessType.NONE)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class TopicEntity {

    @XmlElement(name = "name")
    private @Nullable String name;

    @XmlElement(name = "qos-as-int", required = true)
    private @NotNull int qosAsInt;

    @XmlElement(name = "priority", required = true)
    private @NotNull int priority;

    @XmlElement(name = "no-local", required = true)
    private boolean noLocal;

    @XmlElement(name = "retain-as-published", required = true)
    private boolean retainAsPublished;



    public @Nullable String getName() {
        return name;
    }

    public @NotNull int getQoS() {
        return qosAsInt;
    }

    public @NotNull int getPriority() {
        return priority;
    }

    public boolean getNoLocal() {
        return noLocal;
    }

    public boolean getRetainAsPublished() {
        return retainAsPublished;
    }
}