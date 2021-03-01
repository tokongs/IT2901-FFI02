package com.hivemq.configuration.reader;

import com.google.common.io.Files;
import org.junit.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

/**
 * Currently UNUSED
 */
public class TopicConfiguratorTest extends AbstractConfigurationTest {


    @Test
    public void test_topics_xml() throws Exception {

        final int maxTopics = 255;
        final String contents =
                "<hivemq>" +
                        "<topic>" +
                        "<max-topics>" + maxTopics + "</max-topics> " +
                        "</topic>" +
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

    @Test
    public void test_topics_receive_max_negative_xml() throws Exception {
        final String contents =
                "<hivemq>" +
                        "<topic>" +
                        "<max-topics>-1</max-topics> " +
                        "</topic>" +
                        "</hivemq>";

        Files.write(contents.getBytes(UTF_8), xmlFile);

        reader.applyConfig();

        // default is 8
        assertEquals(8, topicConfigurationService.maxTopics());
    }
}
