package com.example.notifyks.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Random;

@Component
public class EmailChannel implements NotificationChannel {
    private static final Logger log = LoggerFactory.getLogger(EmailChannel.class);
    private final Random rnd = new Random();
    @Override public String name(){ return "EMAIL"; }
    @Override public void send(Map<String,Object> event, Map<String,Object> task) throws Exception {
        Map recip = (Map) event.get("recipient");
        log.info("EMAIL -> {} | subject=Event {} | payload={}", recip.get("email"), event.get("eventType"), event.get("payload"));
        if (rnd.nextDouble() < 0.25) throw new Exception("Simulated EMAIL failure");
    }
}
