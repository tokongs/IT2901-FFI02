package com.hivemq.configuration.reader;

import com.google.common.io.Files;
import org.junit.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import com.hivemq.configuration.service.TopicConfigurationService;

public class TopicConfiguratorTest extends AbstractConfigurationTest {


    @Test
    public void test_topics_xml() throws Exception {

        final int maxTopics = 255;
        final String contents =
                "<hivemq>" +
                        " <mqtt>\n" +
                        "<topics> " +
                        "<max-topics>" + maxTopics + "</max-topics> " +
                        "</topics> " +
                        "</mqtt>\n" +
                        "</hivemq>";
        Files.write(contents.getBytes(UTF_8), xmlFile);

        reader.applyConfig();

        assertEquals(maxTopics, topicConfigurationService.maxTopics());
    }

    @Test
    public void test_topics_defaults() throws Exception {
        final String contents =
                "<hivemq>" +
                "</hivemq>";

        Files.write(contents.getBytes(UTF_8), xmlFile);

        reader.applyConfig();

        assertEquals(8, topicConfigurationService.maxTopics());
    }
}
