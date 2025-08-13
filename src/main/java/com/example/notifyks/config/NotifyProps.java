package com.example.notifyks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "notify")
public class NotifyProps {
    private Retry retry = new Retry();
    private Routing routing = new Routing();
    public Retry getRetry(){ return retry; }
    public Routing getRouting(){ return routing; }

    public static class Retry {
        private int maxAttempts = 5;
        private long backoffInitialMs = 1000;
        private double backoffMultiplier = 2.0;
        public int getMaxAttempts(){ return maxAttempts; }
        public void setMaxAttempts(int maxAttempts){ this.maxAttempts = maxAttempts; }
        public long getBackoffInitialMs(){ return backoffInitialMs; }
        public void setBackoffInitialMs(long backoffInitialMs){ this.backoffInitialMs = backoffInitialMs; }
        public double getBackoffMultiplier(){ return backoffMultiplier; }
        public void setBackoffMultiplier(double backoffMultiplier){ this.backoffMultiplier = backoffMultiplier; }
    }
    public static class Routing {
        private java.util.List<Rule> rules;
        public java.util.List<Rule> getRules(){ return rules; }
        public void setRules(java.util.List<Rule> rules){ this.rules = rules; }
    }
    public static class Rule {
        private Map<String,String> when;
        private List<String> thenChannels;
        public Map<String,String> getWhen(){ return when; }
        public void setWhen(Map<String,String> when){ this.when = when; }
        public List<String> getThenChannels(){ return thenChannels; }
        public void setThenChannels(List<String> thenChannels){ this.thenChannels = thenChannels; }
    }
}
