package com.hivemq.configuration.entity.topic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static com.hivemq.configuration.entity.topic.TopicConfigurationDefaults.MAX_TOPICS_DEFAULT;

/**
 * @author Christoffer Stensrud
 */
@XmlRootElement(name = "nested-topic-config")
@XmlAccessorType(XmlAccessType.NONE)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class NestedTopicConfigEntity {

    @XmlElement(name = "max-topics", defaultValue = "8")
    private int maxTopics = MAX_TOPICS_DEFAULT;

    public int getMaxTopics() {
        return maxTopics;
    }
}
