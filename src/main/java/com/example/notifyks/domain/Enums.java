package com.example.notifyks.domain;
public class Enums {
    public enum EventPriority { LOW, MEDIUM, HIGH }
    public enum ChannelType { EMAIL, SMS, WEBHOOK }
    public enum NotificationStatus { PENDING, DELIVERED, FAILED, DEAD_LETTER }
}
