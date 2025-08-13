package com.example.notifyks.web;

import com.example.notifyks.repo.EventRepository;
import com.example.notifyks.web.dto.EventRequest;
import com.example.notifyks.domain.EventEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventRepository events;
    private final ObjectMapper mapper;
    private final KafkaTemplate<String, Map<String,Object>> kafka;

    public EventController(EventRepository events, ObjectMapper mapper, KafkaTemplate<String, Map<String,Object>> kafka) {
        this.events = events; this.mapper = mapper; this.kafka = kafka;
    }

    @PostMapping
    public ResponseEntity<?> ingest(@Valid @RequestBody EventRequest req) throws Exception {
        EventEntity e = new EventEntity();
        e.eventType = req.eventType;
        e.payloadJson = mapper.writeValueAsString(req.payload);
        e.recipientEmail = req.recipient.email;
        e.recipientPhone = req.recipient.phone;
        e.recipientWebhookUrl = req.recipient.webhookUrl;
        e.timestamp = req.timestamp == null ? Instant.now() : req.timestamp;
        e.priority = req.priority;
        events.save(e);

        Map<String,Object> msg = Map.of(
            "id", e.id,
            "eventType", e.eventType,
            "payload", req.payload,
            "recipient", Map.of("email", e.recipientEmail, "phone", e.recipientPhone, "webhookUrl", e.recipientWebhookUrl),
            "priority", String.valueOf(e.priority),
            "timestamp", e.timestamp.toString()
        );
        kafka.send("events", e.id, msg);
        return ResponseEntity.accepted().body(e.id);
    }
}
