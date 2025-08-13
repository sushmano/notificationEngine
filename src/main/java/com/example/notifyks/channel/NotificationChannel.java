package com.example.notifyks.channel;

import java.util.Map;
public interface NotificationChannel {
    String name();
    void send(Map<String,Object> event, Map<String,Object> task) throws Exception;
}
