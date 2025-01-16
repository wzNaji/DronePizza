package com.wzn.dronepizza.service;

import com.wzn.dronepizza.entity.Delivery;
import com.wzn.dronepizza.entity.Drone;
import com.wzn.dronepizza.entity.DroneStatus;
import com.wzn.dronepizza.entity.Pizza;
import com.wzn.dronepizza.repository.DeliveryRepository;
import com.wzn.dronepizza.repository.DroneRepository;
import com.wzn.dronepizza.repository.PizzaRepository;
import com.wzn.dronepizza.service.impl.DeliveryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private PizzaRepository pizzaRepository;

    @Mock
    private DroneRepository droneRepository;

    @InjectMocks
    private DeliveryServiceImpl deliveryService; // den vi tester

    private Delivery unfinishedDelivery;
    private Delivery finishedDelivery;
    private Pizza pizza;
    private Drone drone;

    @BeforeEach
    void setUp() {
        pizza = new Pizza();
        pizza.setId(100L);

        drone = new Drone();
        drone.setId(200L);
        drone.setStatus(DroneStatus.I_DRIFT);

        unfinishedDelivery = new Delivery();
        unfinishedDelivery.setId(300L);
        unfinishedDelivery.setAddress("Test Street 123");
        unfinishedDelivery.setExpectedDeliveryTime(LocalDateTime.now().plusHours(1));
        unfinishedDelivery.setPizza(pizza);

        finishedDelivery = new Delivery();
        finishedDelivery.setId(400L);
        finishedDelivery.setAddress("Finished Street 456");
        finishedDelivery.setExpectedDeliveryTime(LocalDateTime.now().minusHours(1));
        finishedDelivery.setActualDeliveryTime(LocalDateTime.now()); // Marker, at den ER afsluttet
        finishedDelivery.setPizza(pizza);
    }

    @Test
    void getAllNonFinishedDeleveries_shouldReturnOnlyDeliveriesWithNullActualDeliveryTime() {
        // given
        Delivery notFinished1 = new Delivery();
        notFinished1.setId(1L);
        notFinished1.setActualDeliveryTime(null);

        Delivery notFinished2 = new Delivery();
        notFinished2.setId(2L);
        notFinished2.setActualDeliveryTime(null);

        Delivery finished = new Delivery();
        finished.setId(3L);
        finished.setActualDeliveryTime(LocalDateTime.now()); // done

        List<Delivery> all = List.of(notFinished1, notFinished2, finished);

        given(deliveryRepository.findAll()).willReturn(all);

        // when
        List<Delivery> result = deliveryService.getAllNonFinishedDeleveries();

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(notFinished1));
        assertTrue(result.contains(notFinished2));
        assertFalse(result.contains(finished));
        verify(deliveryRepository, times(1)).findAll();
    }

    @Test
    void createDelivery_shouldCreateNewDeliveryWithExpectedTimePlus30() {
        // given
        Long pizzaId = 100L;
        String address = "Some Address";
        Pizza foundPizza = new Pizza();
        foundPizza.setId(pizzaId);

        given(pizzaRepository.findById(pizzaId)).willReturn(Optional.of(foundPizza));
        given(deliveryRepository.save(any(Delivery.class)))
                .willAnswer(invocation -> invocation.getArgument(0)); // return what was passed

        // when
        Delivery created = deliveryService.createDelivery(pizzaId, address);

        // then
        assertNotNull(created);
        assertEquals(address, created.getAddress());
        assertEquals(foundPizza, created.getPizza());
        // check time is roughly now + 30 min
        LocalDateTime nowPlus30 = LocalDateTime.now().plusMinutes(30);
        assertTrue(
                !created.getExpectedDeliveryTime().isBefore(nowPlus30.minusSeconds(5)) &&
                        !created.getExpectedDeliveryTime().isAfter(nowPlus30.plusSeconds(5)),
                "Expected delivery time should be within a few seconds of now + 30 min"
        );

        // verify
        verify(pizzaRepository).findById(pizzaId);
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void createDelivery_whenPizzaNotFound_throwsException() {
        // given
        Long invalidPizzaId = 999L;
        given(pizzaRepository.findById(invalidPizzaId)).willReturn(Optional.empty());

        // when + then
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> deliveryService.createDelivery(invalidPizzaId, "Address")
        );
        assertTrue(ex.getMessage().contains("Ingen pizza med id 999"));
    }

    @Test
    void getAllDeliveriesWithoutDrone_shouldReturnDeliveriesWhereDroneIsNull() {
        // given
        Delivery d1 = new Delivery();
        d1.setId(10L);
        d1.setDrone(null);

        Delivery d2 = new Delivery();
        d2.setId(20L);
        d2.setDrone(null);

        List<Delivery> mockList = Arrays.asList(d1, d2);
        given(deliveryRepository.findByDroneIsNullAndActualDeliveryTimeIsNull()).willReturn(mockList);

        // when
        List<Delivery> result = deliveryService.getAllDeliveriesWithoutDrone();

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(d1));
        assertTrue(result.contains(d2));
        verify(deliveryRepository).findByDroneIsNullAndActualDeliveryTimeIsNull();
    }

    @Test
    void scheduleDelivery_whenDeliveryNotFound_throwsException() {
        // given
        given(deliveryRepository.findById(999L)).willReturn(Optional.empty());

        // when + then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> deliveryService.scheduleDelivery(999L, 1L));
        assertTrue(ex.getMessage().contains("Levering med id 999 blev ikke fundet."));
    }

    @Test
    void scheduleDelivery_whenDeliveryAlreadyHasDrone_throwsException() {
        // given
        Delivery existingDelivery = new Delivery();
        existingDelivery.setId(300L);
        existingDelivery.setDrone(new Drone()); // Already has a drone

        given(deliveryRepository.findById(300L)).willReturn(Optional.of(existingDelivery));

        // when + then
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> deliveryService.scheduleDelivery(300L, 400L));
        assertTrue(ex.getMessage().contains("Levering har allerede en drone"));
    }

    @Test
    void scheduleDelivery_whenDroneNotFound_throwsException() {
        // given
        Delivery existingDelivery = new Delivery();
        existingDelivery.setId(300L);
        existingDelivery.setDrone(null);

        given(deliveryRepository.findById(300L)).willReturn(Optional.of(existingDelivery));
        given(droneRepository.findById(999L)).willReturn(Optional.empty());

        // when + then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> deliveryService.scheduleDelivery(300L, 999L));
        assertTrue(ex.getMessage().contains("Drone med id 999 blev ikke fundet."));
    }

    @Test
    void scheduleDelivery_whenDroneIsNotI_Drift_throwsException() {
        // given
        Delivery existingDelivery = new Delivery();
        existingDelivery.setId(300L);

        Drone notActiveDrone = new Drone();
        notActiveDrone.setId(999L);
        notActiveDrone.setStatus(DroneStatus.UDE_AF_DRIFT);

        given(deliveryRepository.findById(300L)).willReturn(Optional.of(existingDelivery));
        given(droneRepository.findById(999L)).willReturn(Optional.of(notActiveDrone));

        // when + then
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> deliveryService.scheduleDelivery(300L, 999L));
        assertTrue(ex.getMessage().contains("Drone er ikke i drift"));
    }

    @Test
    void scheduleDelivery_successfulCase() {
        // given
        Delivery existingDelivery = new Delivery();
        existingDelivery.setId(300L);
        existingDelivery.setDrone(null);

        Drone activeDrone = new Drone();
        activeDrone.setId(999L);
        activeDrone.setStatus(DroneStatus.I_DRIFT);

        given(deliveryRepository.findById(300L)).willReturn(Optional.of(existingDelivery));
        given(droneRepository.findById(999L)).willReturn(Optional.of(activeDrone));
        given(deliveryRepository.save(any(Delivery.class)))
                .willAnswer(invocation -> invocation.getArgument(0)); // return the updated delivery

        // when
        Delivery scheduled = deliveryService.scheduleDelivery(300L, 999L);

        // then
        assertNotNull(scheduled);
        assertEquals(activeDrone, scheduled.getDrone());
        verify(deliveryRepository).save(scheduled);
    }

    @Test
    void finishDelivery_whenDeliveryNotFound_throwsException() {
        // given
        given(deliveryRepository.findById(999L)).willReturn(Optional.empty());

        // when + then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> deliveryService.finishDelivery(999L));
        assertTrue(ex.getMessage().contains("Levering med id 999 blev ikke fundet."));
    }

    @Test
    void finishDelivery_whenNoDroneAssigned_throwsException() {
        // given
        Delivery d = new Delivery();
        d.setId(300L);
        d.setDrone(null); // no drone
        given(deliveryRepository.findById(300L)).willReturn(Optional.of(d));

        // when + then
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> deliveryService.finishDelivery(300L));
        assertTrue(ex.getMessage().contains("ikke har en drone"));
    }

    @Test
    void finishDelivery_whenAlreadyFinished_throwsException() {
        // given
        Delivery d = new Delivery();
        d.setId(300L);
        d.setDrone(new Drone());
        d.setActualDeliveryTime(LocalDateTime.now()); // allerede afsluttet

        given(deliveryRepository.findById(300L)).willReturn(Optional.of(d));

        // when + then
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> deliveryService.finishDelivery(300L));
        assertTrue(ex.getMessage().contains("Levering er allerede afsluttet."));
    }

    @Test
    void finishDelivery_successCase() {
        // given
        Delivery d = new Delivery();
        d.setId(300L);
        d.setDrone(new Drone());
        d.setActualDeliveryTime(null); // ikke afsluttet endnu

        given(deliveryRepository.findById(300L)).willReturn(Optional.of(d));
        given(deliveryRepository.save(any(Delivery.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        Delivery result = deliveryService.finishDelivery(300L);

        // then
        assertNotNull(result.getActualDeliveryTime());
        verify(deliveryRepository).save(d);
    }
}

