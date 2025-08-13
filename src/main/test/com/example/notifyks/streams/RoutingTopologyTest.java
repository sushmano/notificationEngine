package com.example.notifyks.streams;

import com.example.notifyks.config.NotifyProps;
import com.example.notifyks.domain.Enums.ChannelType;
import com.example.notifyks.routing.RoutingEngine;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RoutingTopologyTest {

    private TopologyTestDriver driver;
    private TestInputTopic<String, Map<String,Object>> in;
    private TestOutputTopic<String, Map<String,Object>> out;

    @BeforeEach
    void setup() {
        StreamsBuilder builder = new StreamsBuilder();

        RoutingEngine routing = new RoutingEngine(new NotifyProps()) {
            @Override public Set<ChannelType> route(Map<String, Object> event) {
                return new LinkedHashSet<>(Arrays.asList(ChannelType.EMAIL, ChannelType.SMS));
            }
        };
        TopologyConfig cfg = new TopologyConfig();
        Topology topology = cfg.routingTopology(builder, routing);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:9092");

        driver = new TopologyTestDriver(topology, props);

        Serde<String> stringSerde = Serdes.String();
        in = driver.createInputTopic("events", stringSerde.serializer(), new JsonSerializer<>());
        out = driver.createOutputTopic("notify-send", stringSerde.deserializer(), new JsonDeserializer<>());
    }

    @AfterEach
    void teardown() {
        if (driver != null) driver.close();
    }

    @Test
    void fansOutOneTaskPerChannel() {
        Map<String,Object> event = new HashMap<>();
        event.put("id", "e1");
        event.put("eventType", "USER_REGISTERED");
        event.put("priority", "HIGH");

        in.pipeInput("e1", event);

        var records = out.readKeyValuesToList();
        assertEquals(2, records.size(), "Should produce two tasks (EMAIL, SMS)");

        var channels = new HashSet<String>();
        for (var kv : records) {
            Map task = (Map) kv.value;
            channels.add((String) task.get("channel"));
            assertEquals("e1", task.get("eventId"));
            assertTrue(((String) task.get("notificationId")).contains("-"));
        }
        assertEquals(Set.of("EMAIL","SMS"), channels);
    }
}
