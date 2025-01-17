package com.wzn.dronepizza.service.impl;

import com.wzn.dronepizza.entity.Delivery;
import com.wzn.dronepizza.entity.Drone;
import com.wzn.dronepizza.entity.DroneStatus;
import com.wzn.dronepizza.entity.Pizza;
import com.wzn.dronepizza.repository.DeliveryRepository;
import com.wzn.dronepizza.repository.DroneRepository;
import com.wzn.dronepizza.repository.PizzaRepository;
import com.wzn.dronepizza.service.DeliveryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DeliveryServiceImpl implements DeliveryService {


    private final DeliveryRepository deliveryRepository;
    private final PizzaRepository pizzaRepository;
    private final DroneRepository droneRepository;

    public DeliveryServiceImpl(DeliveryRepository deliveryRepository, PizzaRepository pizzaRepository, DroneRepository droneRepository) {
        this.deliveryRepository = deliveryRepository;
        this.pizzaRepository = pizzaRepository;
        this.droneRepository = droneRepository;
    }

    /**
     * Returnerer alle leveringer, der er unfinished.
     */
    @Override
    public List<Delivery> getAllNonFinishedDeleveries() {
        // "ikke færdig" == actualDeliveryTime = null;
        List<Delivery> allDeliveries = deliveryRepository.findAll();
        List<Delivery> nonFinishedDeliveries = new ArrayList<>();
        for (Delivery delivery : allDeliveries) {
            if (delivery.getActualDeliveryTime() == null) {
                nonFinishedDeliveries.add(delivery);
            }
        }
        return nonFinishedDeliveries;
    }

    /**
     * Tilføjer en bestilling med et pizza Id.
     * Forventet leveringstid = nu + 30 minutter.
     * Ingen drone tilknyttes ved oprettelse.
     */
    @Override
    public Delivery createDelivery(Long pizzaId, String address) {
        Pizza pizza = pizzaRepository.findById(pizzaId)
                .orElseThrow(() -> new IllegalArgumentException("Ingen pizza med id " + pizzaId));

        LocalDateTime expected = LocalDateTime.now().plusMinutes(30);
        Delivery delivery = new Delivery(address, expected, pizza);
        return deliveryRepository.save(delivery);
    }

    /**
     * Returnerer alle leveringer, der mangler en drone (dvs. drone == null).
     */
    @Override
    public List<Delivery> getAllDeliveriesWithoutDrone() {
            return deliveryRepository.findByDroneIsNullAndActualDeliveryTimeIsNull();
    }

    /**
     * Tager en levering, der mangler en drone, og tildeler den en drone.
     * - Levering skal ikke allerede have en drone.
     * - Dronen skal være i drift, ellers kastes fejl.
     * - Hvis levering allerede har en drone, kastes fejl.
     */
    @Override
    public Delivery scheduleDelivery(Long deliveryId) {
        Delivery delivery = findDeliveryOrThrow(deliveryId);

        if (delivery.getDrone() != null) {
            throw new IllegalStateException("Levering har allerede en drone tilknyttet.");
        }

        List<Drone> listOfDrones = droneRepository.findAll();
        List<Drone> availableDrones = new ArrayList<>();

        for (Drone drone : listOfDrones) {
            if (drone.getStatus() == DroneStatus.I_DRIFT) {
                availableDrones.add(drone);
            }
        }

        if (availableDrones.isEmpty()) {
            throw new IllegalStateException("Ingen tilgengelige droner.");
        }

        // Select en random drone fra index 0 til array size
        Drone selectedDrone = availableDrones.get(
                ThreadLocalRandom.current().nextInt(availableDrones.size())
        );

        delivery.setDrone(selectedDrone);

        return deliveryRepository.save(delivery);
    }

    /**
     * Marker en levering som afsluttet i dette øjeblik.
     * - Hvis leveringen ikke har en drone, kastes fejl.
     * - Hvis den allerede er afsluttet, kastes en fejl.
     */
    @Override
    public Delivery finishDelivery(Long deliveryId) {
        Delivery delivery = findDeliveryOrThrow(deliveryId);

        if (delivery.getDrone() == null) {
            throw new IllegalStateException("Kan ikke færdiggøre levering, da den ikke har en drone.");
        }
        if (delivery.getActualDeliveryTime() != null) {
            throw new IllegalStateException("Levering er allerede afsluttet.");
        }

        delivery.setActualDeliveryTime(LocalDateTime.now());
        return deliveryRepository.save(delivery);
    }

    private Delivery findDeliveryOrThrow(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Levering med id " + deliveryId + " blev ikke fundet."));
    }

}
