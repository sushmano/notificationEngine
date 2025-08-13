package com.example.notifyks.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Random;

@Component
public class SmsChannel implements NotificationChannel {
    private static final Logger log = LoggerFactory.getLogger(SmsChannel.class);
    private final Random rnd = new Random();
    @Override public String name(){ return "SMS"; }
    @Override public void send(Map<String,Object> event, Map<String,Object> task) throws Exception {
        Map recip = (Map) event.get("recipient");
        log.info("SMS -> {} | text=[{}] {}", recip.get("phone"), event.get("eventType"), event.get("payload"));
        if (rnd.nextDouble() < 0.25) throw new Exception("Simulated SMS failure");
    }
}
