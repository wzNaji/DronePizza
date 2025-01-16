package com.wzn.dronepizza.service;


import com.wzn.dronepizza.entity.Drone;
import com.wzn.dronepizza.entity.DroneStatus;
import com.wzn.dronepizza.entity.Station;
import com.wzn.dronepizza.repository.DroneRepository;
import com.wzn.dronepizza.repository.StationRepository;
import com.wzn.dronepizza.service.impl.DroneServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class DroneServiceImplTest {

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private DroneServiceImpl droneService; // Den vi tester

    private Station stationA;
    private Station stationB;

    @BeforeEach
    void setUp() {
        stationA = new Station();
        stationA.setId(1L);

        stationB = new Station();
        stationB.setId(2L);
    }

    @Test
    void getAllDrones_shouldReturnListOfDrones() {
        // given
        Drone drone1 = new Drone();
        Drone drone2 = new Drone();
        List<Drone> mockDrones = List.of(drone1, drone2);
        given(droneRepository.findAll()).willReturn(mockDrones);

        // when
        List<Drone> result = droneService.getAllDrones();

        // then
        assertEquals(2, result.size());
        verify(droneRepository, times(1)).findAll();
    }

    @Test
    void createDrone_whenNoStations_thenThrowException() {
        // given
        Drone drone = new Drone();
        // stationRepository.findAll() returns an empty list
        given(stationRepository.findAll()).willReturn(new ArrayList<>());

        // when + then
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> droneService.createDrone(drone)
        );
        assertTrue(ex.getMessage().contains("ingen stationer"));
        verify(stationRepository, times(1)).findAll();
        verify(droneRepository, never()).save(any());
    }

    @Test
    void createDrone_whenStationsExist_shouldAssignStationWithFewestDrones() {
        // given
        Drone drone = new Drone();
        stationA.setId(1L);
        stationB.setId(2L);

        // Stations
        given(stationRepository.findAll()).willReturn(List.of(stationA, stationB));

        // For stationA: 2 drones
        // For stationB: 1 drone
        given(droneRepository.countByStationId(1L)).willReturn(2L);
        given(droneRepository.countByStationId(2L)).willReturn(1L);

        // Stubs for saving
        given(droneRepository.save(any(Drone.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Drone createdDrone = droneService.createDrone(drone);

        // then
        assertEquals(2L, createdDrone.getStation().getId());
        verify(stationRepository).findAll();
        verify(droneRepository).countByStationId(1L);
        verify(droneRepository).countByStationId(2L);
        verify(droneRepository).save(createdDrone);
    }


    @Test
    void enableDrone_whenExists_setStatusIDrift() {
        // given
        Drone drone = new Drone();
        drone.setId(10L);
        drone.setStatus(DroneStatus.UDE_AF_DRIFT);

        // Mock find
        given(droneRepository.findById(10L)).willReturn(Optional.of(drone));
        // Mock save
        given(droneRepository.save(drone)).willReturn(drone);

        // when
        Drone updated = droneService.enableDrone(10L);

        // then
        assertEquals(DroneStatus.I_DRIFT, updated.getStatus());
        verify(droneRepository).findById(10L);
        verify(droneRepository).save(drone);
    }

    @Test
    void enableDrone_whenNotFound_throwsException() {
        // given
        given(droneRepository.findById(999L)).willReturn(Optional.empty());

        // when + then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> droneService.enableDrone(999L)
        );
        assertTrue(ex.getMessage().contains("Drone med id 999"));
    }

    @Test
    void disableDrone_shouldSetStatusUdeAfDrift() {
        // given
        Drone drone = new Drone();
        drone.setId(5L);
        drone.setStatus(DroneStatus.I_DRIFT);

        given(droneRepository.findById(5L)).willReturn(Optional.of(drone));
        given(droneRepository.save(any(Drone.class))).willReturn(drone);

        // when
        Drone updated = droneService.disableDrone(5L);

        // then
        assertEquals(DroneStatus.UDE_AF_DRIFT, updated.getStatus());
        verify(droneRepository).findById(5L);
        verify(droneRepository).save(drone);
    }

    @Test
    void retireDrone_shouldSetStatusUdFaset() {
        // given
        Drone drone = new Drone();
        drone.setId(5L);
        drone.setStatus(DroneStatus.I_DRIFT);

        given(droneRepository.findById(5L)).willReturn(Optional.of(drone));
        given(droneRepository.save(any(Drone.class))).willReturn(drone);

        // when
        Drone updated = droneService.retireDrone(5L);

        // then
        assertEquals(DroneStatus.UDFASET, updated.getStatus());
        verify(droneRepository).findById(5L);
        verify(droneRepository).save(drone);
    }
}

