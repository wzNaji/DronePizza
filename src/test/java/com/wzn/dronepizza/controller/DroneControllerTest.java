package com.wzn.dronepizza.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wzn.dronepizza.entity.Drone;
import com.wzn.dronepizza.entity.DroneStatus;
import com.wzn.dronepizza.service.DroneService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DroneControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DroneService droneService;

    @InjectMocks
    private DroneController droneController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    DroneControllerTest() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        this.mockMvc = MockMvcBuilders.standaloneSetup(droneController).build(); // Set up MockMvc
    }

    @Test
    void getAllDrones_shouldReturnOkAndList() throws Exception {
        // given
        Drone drone1 = new Drone();
        drone1.setId(1L);
        drone1.setSerialNumber(UUID.randomUUID());
        drone1.setStatus(DroneStatus.I_DRIFT);

        Drone drone2 = new Drone();
        drone2.setId(2L);
        drone2.setSerialNumber(UUID.randomUUID());
        drone2.setStatus(DroneStatus.UDFASET);

        given(droneService.getAllDrones()).willReturn(List.of(drone1, drone2));

        // when + then
        mockMvc.perform(get("/drones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[1].id").value(2));
    }

    @Test
    void createNewDrone_shouldReturnOkAndCreatedDrone() throws Exception {
        // given
        Drone drone = new Drone();
        drone.setId(100L);
        drone.setStatus(DroneStatus.I_DRIFT);
        given(droneService.createDrone(any(Drone.class))).willReturn(drone);

        // when + then
        mockMvc.perform(post("/drones/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(drone)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(100));
    }

    @Test
    void createNewDrone_whenServiceThrowsError_shouldReturn500() throws Exception {
        // given
        given(droneService.createDrone(any(Drone.class))).willThrow(new RuntimeException("Testfejl"));

        // when + then
        mockMvc.perform(post("/drones/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Drone())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Internal error: Testfejl"));
    }

    @Test
    void enableDrone_whenValidId_shouldReturnOk() throws Exception {
        // given
        Drone drone = new Drone();
        drone.setId(10L);
        drone.setStatus(DroneStatus.I_DRIFT);

        given(droneService.enableDrone(10L)).willReturn(drone);

        // when + then
        mockMvc.perform(post("/drones/enable").param("droneId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.message").value("Drone status: i drift."));
    }

    @Test
    void enableDrone_whenNotFound_shouldReturn404() throws Exception {
        // given
        given(droneService.enableDrone(999L)).willThrow(new IllegalArgumentException("Drone ikke fundet"));

        // when + then
        mockMvc.perform(post("/drones/enable").param("droneId", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Drone ikke fundet"));
    }
}
