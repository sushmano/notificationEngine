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
