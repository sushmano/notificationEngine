package com.example.notifyks.web;

import com.example.notifyks.repo.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventController.class)
public class EventControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private EventRepository events;
    @MockBean private KafkaTemplate<String, Map<String,Object>> kafka;

    @Test
    void acceptsEvent() throws Exception {
        String body = """        {
          "eventType": "USER_REGISTERED",
          "payload": {"name":"Asha"},
          "recipient": {"email":"a@a.com","phone":"+9715","webhookUrl":"https://x"},
          "priority": "HIGH"
        }
        """;
        mvc.perform(post("/events").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isAccepted());
    }
}
