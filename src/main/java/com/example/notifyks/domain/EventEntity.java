package com.example.notifyks.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="events")
public class EventEntity {
    @Id public String id = UUID.randomUUID().toString();
    public String eventType;
    @Lob public String payloadJson;
    public String recipientEmail;
    public String recipientPhone;
    public String recipientWebhookUrl;
    public Instant timestamp = Instant.now();
    @Enumerated(EnumType.STRING)
    public com.example.notifyks.domain.Enums.EventPriority priority = com.example.notifyks.domain.Enums.EventPriority.MEDIUM;
}

@Entity @Table(name="notifications")
public class NotificationEntity {
    @Id public String id = UUID.randomUUID().toString();
    public String eventId;
    @Enumerated(EnumType.STRING)
    public com.example.notifyks.domain.Enums.ChannelType channel;
    @Enumerated(EnumType.STRING)
    public com.example.notifyks.domain.Enums.NotificationStatus status = com.example.notifyks.domain.Enums.NotificationStatus.PENDING;
    public int attempts = 0;
    public Instant lastUpdated = Instant.now();
    @Lob public String lastError;
}
