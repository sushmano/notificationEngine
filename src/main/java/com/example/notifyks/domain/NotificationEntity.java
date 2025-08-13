package com.example.notifyks.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

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
