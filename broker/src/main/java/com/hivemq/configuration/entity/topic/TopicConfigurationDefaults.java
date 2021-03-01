package com.hivemq.configuration.entity.topic;

import com.hivemq.codec.encoder.mqtt5.UnsignedDataTypes;

/**
 * @author Christoffer Stensrud
 * Currently UNUSED
 */
public class TopicConfigurationDefaults {

    public static final int MAX_TOPICS_DEFAULT = 8;
    public static final int MAX_TOPICS_MINIMUM = 1;
    public static final int MAX_TOPICS_MAXIMUM = UnsignedDataTypes.UNSIGNED_SHORT_MAX_VALUE;

}
