package com.example.notifyks.routing;

import com.example.notifyks.config.NotifyProps;
import com.example.notifyks.domain.Enums.ChannelType;
import com.example.notifyks.streams.TopologyConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.junit.jupiter.api.Test;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RoutingEngineTest {

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

        JsonSerializer<Map<String, Object>> jsonSerializer = new JsonSerializer<>();
        JsonDeserializer<Map<String, Object>> jsonDeserializer = new JsonDeserializer<>(Map.class, new ObjectMapper(), false);

        in = driver.createInputTopic("events", stringSerde.serializer(), jsonSerializer);
        out = driver.createOutputTopic("notify-send", stringSerde.deserializer(), jsonDeserializer);
    }

    @Test
    void routesByEventTypeAndPriority() throws Exception {
        var props = new NotifyProps();
        var rule1 = new NotifyProps.Rule();
        rule1.setWhen(Map.of("eventType","USER_REGISTERED"));
        rule1.setThenChannels(List.of("EMAIL","SMS"));

        var rule2 = new NotifyProps.Rule();
        rule2.setWhen(Map.of("priority","HIGH"));
        rule2.setThenChannels(List.of("WEBHOOK"));

        var routing = new NotifyProps.Routing();
        routing.setRules(List.of(rule1, rule2));

        var m = NotifyProps.class.getDeclaredField("routing");
        m.setAccessible(true);
        m.set(props, routing);

        RoutingEngine engine = new RoutingEngine(props);
        var event = Map.<String,Object>of("eventType","USER_REGISTERED","priority","HIGH");
        Set<ChannelType> result = engine.route(event);
        assertEquals(Set.of(ChannelType.EMAIL, ChannelType.SMS, ChannelType.WEBHOOK), result);
    }

    @AfterEach
    void teardown() {
        if (driver != null) driver.close();
    }
}
