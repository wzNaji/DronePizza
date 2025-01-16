package com.wzn.dronepizza.repository;

import com.wzn.dronepizza.entity.Delivery;
import com.wzn.dronepizza.entity.Drone;
import com.wzn.dronepizza.entity.DroneStatus;
import com.wzn.dronepizza.entity.Station;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

public class DeliveryRepositoryTest {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private DroneRepository droneRepository;

    @Test
    public void testFindByDroneIsNullAndActualDeliveryTimeIsNull() {

        deliveryRepository.deleteAll();
        droneRepository.deleteAll();
        stationRepository.deleteAll();
        // Arrange: Create test data
        Delivery deliveryWithDrone = new Delivery("Address 1", LocalDateTime.now(), null);
        Station station = stationRepository.save(new Station(55.12, 12.34));
        Drone drone = droneRepository.save(new Drone(UUID.randomUUID(), DroneStatus.I_DRIFT, station));
        deliveryWithDrone.setDrone(drone);
        deliveryWithDrone.setActualDeliveryTime(LocalDateTime.now());
        deliveryRepository.save(deliveryWithDrone);

        Delivery deliveryWithoutDrone = new Delivery("Address 2", LocalDateTime.now(), null);
        Delivery deliveryWithoutDrone2 = new Delivery("Address 3", LocalDateTime.now(), null);
        deliveryRepository.saveAll(List.of(deliveryWithoutDrone, deliveryWithoutDrone2));



        // Act: Call the repository method
        List<Delivery> result = deliveryRepository.findByDroneIsNullAndActualDeliveryTimeIsNull();

        // Assert: Verify the results
        assertEquals(2, result.size());



    }
}
