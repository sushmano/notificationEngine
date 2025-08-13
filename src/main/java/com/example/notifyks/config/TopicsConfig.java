package com.example.notifyks.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicsConfig {
    @Value("${kafka.streams.input-topic}") private String inputTopic;
    @Value("${kafka.streams.output-topic}") private String outputTopic;

    @Bean
    public NewTopic eventsTopic() { return TopicBuilder.name(inputTopic).partitions(3).replicas(1).build(); }
    @Bean
    public NewTopic notifySendTopic() { return TopicBuilder.name(outputTopic).partitions(6).replicas(1).build(); }
}
