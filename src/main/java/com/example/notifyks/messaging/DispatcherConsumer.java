package com.example.notifyks.messaging;

import com.example.notifyks.channel.NotificationChannel;
import com.example.notifyks.domain.NotificationEntity;
import com.example.notifyks.domain.Enums.NotificationStatus;
import com.example.notifyks.repo.NotificationRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
    import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class DispatcherConsumer {
    private static final Logger log = LoggerFactory.getLogger(DispatcherConsumer.class);
    private final NotificationRepository notifications;
    private final ApplicationContext ctx;

    public DispatcherConsumer(NotificationRepository notifications, ApplicationContext ctx) {
        this.notifications = notifications;
        this.ctx = ctx;
    }

    @RetryableTopic(
        attempts = "5",
        backoff = @org.springframework.retry.annotation.Backoff(delay = 1000, multiplier = 2.0),
        dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR,
        include = { Exception.class }
    )
    @KafkaListener(topics = "${kafka.streams.output-topic}", groupId = "dispatcher")
    public void onTask(@Payload Map<String,Object> task, ConsumerRecord<String, Map<String,Object>> record,
                       @Header(name="kafka_receivedTopic") String topic) throws Exception {
        String notificationId = String.valueOf(task.get("notificationId"));
        String channel = String.valueOf(task.get("channel"));
        String eventId = String.valueOf(task.get("eventId"));

        NotificationEntity n = notifications.findById(notificationId).orElseGet(() -> {
            NotificationEntity x = new NotificationEntity();
            x.id = notificationId;
            x.eventId = eventId;
            x.status = NotificationStatus.PENDING;
            x.lastUpdated = Instant.now();
            return notifications.save(x);
        });

        Map<String,Object> event = Map.of(
            "id", eventId,
            "eventType", record.headers().lastHeader("eventType") != null ? new String(record.headers().lastHeader("eventType").value()) : "UNKNOWN",
            "recipient", Map.of() // omitted for brevity
        );

        NotificationChannel ch = (NotificationChannel) ctx.getBean(
                switch (channel) { case "EMAIL" -> "emailChannel"; case "SMS" -> "smsChannel"; default -> "webhookChannel"; }
        );

        try {
            ch.send(event, task);
            n.status = NotificationStatus.DELIVERED;
            n.lastError = null;
            n.lastUpdated = Instant.now();
            notifications.save(n);
            log.info("Delivered {} via {}", notificationId, channel);
        } catch (Exception ex) {
            n.status = NotificationStatus.PENDING;
            n.lastError = ex.getMessage();
            n.attempts += 1;
            n.lastUpdated = Instant.now();
            notifications.save(n);
            log.warn("Delivery failed {} via {} attempt {}: {}", notificationId, channel, n.attempts, ex.toString());
            throw ex;
        }
    }
}
