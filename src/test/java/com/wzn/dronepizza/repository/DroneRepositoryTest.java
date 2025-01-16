package com.wzn.dronepizza.repository;

import com.wzn.dronepizza.entity.Drone;
import com.wzn.dronepizza.entity.DroneStatus;
import com.wzn.dronepizza.entity.Station;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DroneRepositoryTest {

    @Autowired
    private DroneRepository droneRepository;

    @Autowired
    private StationRepository stationRepository;

    @Test
    public void testCountByStationId() {
        // Arrange: Opret en station og droner
        Station testStation = new Station(55.12, 12.34);
        stationRepository.save(testStation);

        Drone drone1 = new Drone(UUID.randomUUID(), DroneStatus.I_DRIFT, testStation);
        Drone drone2 = new Drone(UUID.randomUUID(), DroneStatus.I_DRIFT, testStation);
        droneRepository.saveAll(List.of(drone1, drone2));

        // Act: Kald metoden
        long droneCount = droneRepository.countByStationId(testStation.getId());

        // Assert: Bekræft resultatet
        assertEquals(2, droneCount);
    }
}

