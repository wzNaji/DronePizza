package com.wzn.dronepizza.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wzn.dronepizza.entity.Delivery;
import com.wzn.dronepizza.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DeliveryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DeliveryService deliveryService;

    @InjectMocks
    private DeliveryController deliveryController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        this.mockMvc = MockMvcBuilders.standaloneSetup(deliveryController).build(); // Configure MockMvc
    }

    @Test
    void getAllNonFinishedDeliveries_shouldReturnOk() throws Exception {
        // given
        Delivery d1 = new Delivery();
        d1.setId(1L);
        d1.setAddress("Test1");
        d1.setExpectedDeliveryTime(LocalDateTime.now().plusMinutes(30));

        Delivery d2 = new Delivery();
        d2.setId(2L);
        d2.setAddress("Test2");
        d2.setExpectedDeliveryTime(LocalDateTime.now().plusMinutes(40));

        given(deliveryService.getAllNonFinishedDeleveries()).willReturn(List.of(d1, d2));

        // when + then
        mockMvc.perform(get("/deliveries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[1].id").value(2));
    }

    @Test
    void createDelivery_shouldReturnOkAndDelivery() throws Exception {
        // given
        Delivery d = new Delivery();
        d.setId(5L);
        d.setAddress("Test Street");
        given(deliveryService.createDelivery(100L, "Test Street")).willReturn(d);

        // Serialize request payload to JSON
        String requestJson = objectMapper.writeValueAsString(d);

        // when + then
        mockMvc.perform(post("/deliveries/add")
                        .param("pizzaId", "100")
                        .param("address", "Test Street")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)) // Pass JSON payload
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(5))
                .andExpect(jsonPath("$.message").value("Delivery oprettet."));
    }

    @Test
    void createDelivery_whenServiceThrowsException_shouldReturn400() throws Exception {
        // given
        given(deliveryService.createDelivery(anyLong(), anyString()))
                .willThrow(new IllegalArgumentException("Ingen pizza med id 999"));

        // when + then
        mockMvc.perform(post("/deliveries/add")
                        .param("pizzaId", "999")
                        .param("address", "Some Address"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Ingen pizza med id 999"));
    }

    @Test
    void scheduleDelivery_shouldReturnOk() throws Exception {
        Delivery d = new Delivery();
        d.setId(10L);
        given(deliveryService.scheduleDelivery(10L)).willReturn(d);

        // Serialize request payload
        String requestJson = objectMapper.writeValueAsString(d);

        mockMvc.perform(post("/deliveries/schedule")
                        .param("deliveryId", "10")
                        .param("droneId", "20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)) // Pass JSON payload
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.message").value("Delivery scheduled successfully."));
    }

    @Test
    void scheduleDelivery_whenIllegalState_shouldReturn400() throws Exception {
        given(deliveryService.scheduleDelivery(10L))
                .willThrow(new IllegalStateException("Drone er ikke i drift"));

        mockMvc.perform(post("/deliveries/schedule")
                        .param("deliveryId", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Drone er ikke i drift"));
    }

    @Test
    void finishDelivery_shouldReturnOk() throws Exception {
        Delivery d = new Delivery();
        d.setId(30L);
        given(deliveryService.finishDelivery(30L)).willReturn(d);

        // Serialize request payload
        String requestJson = objectMapper.writeValueAsString(d);

        mockMvc.perform(post("/deliveries/finish")
                        .param("deliveryId", "30")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)) // Pass JSON payload
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(30))
                .andExpect(jsonPath("$.message").value("Delivery afleveret."));
    }
}
