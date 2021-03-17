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
package com.hivemq.configuration.reader;

import com.google.common.io.Files;
import org.junit.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

/**
 * Currently UNUSED
 */
public class TopicConfiguratorTest extends AbstractConfigurationTest {

/*
    @Test
    public void test_topics_xml() throws Exception {

        final String name = "topic";
        final int qos = 0;
        final int priority = 1;
        final boolean nolocal = false;
        final boolean retainaspublished = true;

        final String contents =
                "<hivemq>" +
                        "<topics>" +
                        "<topic>" +
                        "<name>"+name+"</name>"
                        "<qos-as-int>"+qos+"</qos-as-int>"
                        "<priority>"+priority+"</priority>"
                        "<no-local>"+nolocal+"</no-local>"
                        "<retain-as-published>"+retainaspublished+"</retain-as-published>"
                        "</topic> " +
                        "</topics>" +
                        "</hivemq>";
        Files.write(contents.getBytes(UTF_8), xmlFile);

        reader.applyConfig();

        assertEquals(maxTopics, topicConfigurationService.getTopics());
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
    }*/
}
