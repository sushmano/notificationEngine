package com.example.notifyks.streams;

import com.example.notifyks.config.NotifyProps;
import com.example.notifyks.domain.Enums.ChannelType;
import com.example.notifyks.routing.RoutingEngine;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
public class TopologyConfig {

    @Value("${kafka.streams.input-topic}") String inputTopic;
    @Value("${kafka.streams.output-topic}") String outputTopic;

    @Bean
    public Topology routingTopology(StreamsBuilder builder, RoutingEngine routing) {
        Serde<String> stringSerde = Serdes.String();
        JsonSerde<Map<String,Object>> jsonSerde = new JsonSerde<>();
        jsonSerde.configure(new HashMap<>(), false);

        KStream<String, Map<String,Object>> events = builder.stream(inputTopic, Consumed.with(stringSerde, jsonSerde));

        KStream<String, Map<String,Object>> tasks = events.flatMap((key, event) -> {
            Set<ChannelType> chans = routing.route(event);
            return chans.stream().map(ch -> {
                Map<String,Object> task = new HashMap<>();
                task.put("eventId", event.get("id"));
                task.put("channel", String.valueOf(ch));
                task.put("notificationId", key + "-" + ch); // deterministic id for demo
                task.put("attempt", 0);
                return KeyValue.pair((String) task.get("notificationId"), task);
            }).toList();
        });

        tasks.to(outputTopic, Produced.with(stringSerde, jsonSerde));
        return builder.build();
    }
}
