package com.hivemq.client.extensions;

import com.hivemq.client.mqtt.datatypes.MqttTopicFilter;
import org.jetbrains.annotations.NotNull;

public class TopicPriority {

    private final @NotNull MqttTopicFilter filter;
    private final @NotNull PriorityClass priority;

    public TopicPriority(@NotNull MqttTopicFilter filter, @NotNull PriorityClass priority) {
        this.filter = filter;
        this.priority = priority;
    }

    public MqttTopicFilter getTopicFilter(){
        return filter;
    }

    public PriorityClass getPriorityClass(){
        return priority;
    }
}
