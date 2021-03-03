package com.hivemq.extensions.priority;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.mqtt.topic.TopicFilter;

public class TopicPriority {

    private final @NotNull String filter;
    private final @NotNull PriorityClass priorityClass;
    private final @NotNull int priority;

    public TopicPriority(@NotNull String filter, @NotNull PriorityClass priorityClass, @NotNull int priority) {
        this.filter = filter;
        this.priorityClass = priorityClass;
        this.priority = priority;
    }

    public String getTopicFilter(){
        return filter;
    }

    public PriorityClass getPriorityClass(){
        return priorityClass;
    }

    public int getPriority(){
        return priority;
    }
}