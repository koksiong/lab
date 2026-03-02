package com.lab.app.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ObjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPostObject_basic_and_get() throws Exception {
        final Map<String, String> inputs = new HashMap<>();
        final String key = UUID.randomUUID().toString();
        inputs.put(key, "value1");
        mockMvc.perform(MockMvcRequestBuilders.post("/object")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputs)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].key").value(key))
                .andExpect(jsonPath("$[0].value").value("value1"));

        mockMvc.perform(MockMvcRequestBuilders.get("/object/" + key)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("value1"));
    }

    @Test
    void testPostObject_basic_update_and_get() throws Exception {
        final Map<String, String> inputs = new HashMap<>();
        final String key = UUID.randomUUID().toString();
        inputs.put(key, "value1");
        mockMvc.perform(MockMvcRequestBuilders.post("/object")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputs)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].key").value(key))
                .andExpect(jsonPath("$[0].value").value("value1"))
                .andExpect(jsonPath("$[0].version").value(1));

        inputs.put(key, "value2");
        mockMvc.perform(MockMvcRequestBuilders.post("/object")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputs)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].key").value(key))
                .andExpect(jsonPath("$[0].value").value("value2"))
                .andExpect(jsonPath("$[0].version").value(2));

        mockMvc.perform(MockMvcRequestBuilders.get("/object/" + key)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("value2"));
    }

    @Test
    @SuppressWarnings("java:S2925")
    void testGetObject_with_timestamp() throws Exception {
        final Map<String, String> inputs = new HashMap<>();
        final String key = UUID.randomUUID().toString();
        inputs.put(key, "value1");
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/object")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputs)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].key").value(key))
                .andExpect(jsonPath("$[0].value").value("value1"))
                .andExpect(jsonPath("$[0].version").value(1))
                .andReturn();

        String timestamp1 = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$[0].timestamp");
        LocalDateTime localDateTime = LocalDateTime.parse(timestamp1);
        long unixTimestamp1 = localDateTime.toEpochSecond(ZoneOffset.UTC) + 1;  // plus 1 second

        // introduce a time delay to differentiate the queries
        TimeUnit.SECONDS.sleep(2);

        inputs.put(key, "value2");
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/object")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputs)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].key").value(key))
                .andExpect(jsonPath("$[0].value").value("value2"))
                .andExpect(jsonPath("$[0].version").value(2))
                .andReturn();

        String timestamp2 = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$[0].timestamp");
        localDateTime = LocalDateTime.parse(timestamp2);
        long unixTimestamp2 = localDateTime.toEpochSecond(ZoneOffset.UTC) + 1;  // plus 1 second

        mockMvc.perform(MockMvcRequestBuilders.get("/object/" + key + "?timestamp=" + unixTimestamp1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("value1"));

        mockMvc.perform(MockMvcRequestBuilders.get("/object/" + key + "?timestamp=" + unixTimestamp2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("value2"));
    }


    @Test
    void testGetAllObjects_basic() throws Exception {
        final Map<String, String> inputs = new HashMap<>();
        final String key = UUID.randomUUID().toString();
        inputs.put(key, "value1");
        mockMvc.perform(MockMvcRequestBuilders.post("/object")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputs)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].key").value(key))
                .andExpect(jsonPath("$[0].value").value("value1"))
                .andExpect(jsonPath("$[0].version").value(1));

        inputs.put(key, "value2");
        mockMvc.perform(MockMvcRequestBuilders.post("/object")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputs)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].key").value(key))
                .andExpect(jsonPath("$[0].value").value("value2"))
                .andExpect(jsonPath("$[0].version").value(2));

        final Map<String, String> inputs2 = new HashMap<>();
        final String key2 = UUID.randomUUID().toString();
        inputs2.put(key2, "value22");
        mockMvc.perform(MockMvcRequestBuilders.post("/object")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputs2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].key").value(key2))
                .andExpect(jsonPath("$[0].value").value("value22"))
                .andExpect(jsonPath("$[0].version").value(1));

        mockMvc.perform(MockMvcRequestBuilders.get("/object/get_all_records")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.key == '" + key + "')]", hasSize(2)))
                .andExpect(jsonPath("$[?(@.key == '" + key2 + "')]", hasSize(1)));
    }


}
