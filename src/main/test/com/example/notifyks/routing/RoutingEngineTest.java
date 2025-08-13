package com.example.notifyks.routing;

import com.example.notifyks.config.NotifyProps;
import com.example.notifyks.domain.Enums.ChannelType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RoutingEngineTest {

    @Test
    void routesByEventTypeAndPriority() throws Exception {
        var props = new NotifyProps();
        var rule1 = new NotifyProps.Rule();
        rule1.setWhen(Map.of("eventType","USER_REGISTERED"));
        rule1.setThenChannels(List.of("EMAIL","SMS"));

        var rule2 = new NotifyProps.Rule();
        rule2.setWhen(Map.of("priority","HIGH"));
        rule2.setThenChannels(List.of("WEBHOOK"));

        var routing = new NotifyProps.Routing();
        routing.setRules(List.of(rule1, rule2));

        var m = NotifyProps.class.getDeclaredField("routing");
        m.setAccessible(true);
        m.set(props, routing);

        RoutingEngine engine = new RoutingEngine(props);
        var event = Map.<String,Object>of("eventType","USER_REGISTERED","priority","HIGH");
        Set<ChannelType> out = engine.route(event);
        assertEquals(Set.of(ChannelType.EMAIL, ChannelType.SMS, ChannelType.WEBHOOK), out);
    }
}
