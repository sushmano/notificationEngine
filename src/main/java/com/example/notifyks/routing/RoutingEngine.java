package com.example.notifyks.routing;

import com.example.notifyks.config.NotifyProps;
import com.example.notifyks.domain.Enums.ChannelType;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class RoutingEngine {
    private final NotifyProps props;
    public RoutingEngine(NotifyProps props){ this.props = props; }

    public Set<ChannelType> route(Map<String,Object> event) {
        Set<ChannelType> out = new LinkedHashSet<>();
        if (props.getRouting().getRules() == null) return out;
        for (NotifyProps.Rule r : props.getRouting().getRules()) {
            if (matches(r.getWhen(), event)) {
                for (String c : r.getThenChannels()) out.add(ChannelType.valueOf(c));
            }
        }
        return out;
    }

    private boolean matches(Map<String,String> when, Map<String,Object> e) {
        if (when == null) return false;
        for (var en : when.entrySet()) {
            String key = en.getKey(); String val = en.getValue();
            Object actual = switch (key) {
                case "eventType" -> e.get("eventType");
                case "priority" -> e.get("priority");
                default -> null;
            };
            if (!Objects.equals(String.valueOf(actual), val)) return false;
        }
        return true;
    }
}
