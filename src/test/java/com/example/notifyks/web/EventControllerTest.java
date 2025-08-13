package com.example.notifyks.web;

import com.example.notifyks.repo.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = EventController.class)
@ContextConfiguration(classes = EventController.class)
public class EventControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private EventRepository events;
    @MockBean private KafkaTemplate<String, Map<String,Object>> kafka;

    @Test
    void acceptsEvent() throws Exception {
        String body = """
        {
          "eventType": "USER_REGISTERED",
          "payload": {"name":"Susheela"},
          "recipient": {"email":"susheela@example.com","phone":"+971500000000","webhookUrl":"https://example.com"},
          "priority": "HIGH"
        }
        """;
        mvc.perform(post("/events").contentType("application/json").content(body))
                .andExpect(status().isAccepted());
    }
}
