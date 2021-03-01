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
