package com.example.notifyks.web.dto;

import com.example.notifyks.domain.Enums.EventPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;

public class EventRequest {
    @NotBlank public String eventType;
    @NotNull public Map<String,Object> payload;
    @NotNull public Recipient recipient;
    public Instant timestamp;
    public EventPriority priority = EventPriority.MEDIUM;
    public static class Recipient {
        public String email;
        public String phone;
        public String webhookUrl;
    }
}
