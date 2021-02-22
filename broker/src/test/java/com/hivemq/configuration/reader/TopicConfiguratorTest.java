package com.hivemq.configuration.reader;

import com.google.common.io.Files;
import org.junit.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import com.hivemq.configuration.service.TopicConfigurationService;

public class TopicConfiguratorTest extends AbstractConfigurationTest {


    @Test
    public void test_max_topics() throws Exception {

        final int maxTopics = 8;
        final String contents =
                "<hivemq>" +
                        " <mqtt>\n" +
                        "<packets> " +
                        "<max-packet-size>" + maxTopics + "</max-packet-size> " +
                        "</packets> " +
                        "</mqtt>\n" +
                        "</hivemq>";
        Files.write(contents.getBytes(UTF_8), xmlFile);

        reader.applyConfig();

        assertEquals(maxTopics, topicConfigurationService.maxTopics());
    }
}
