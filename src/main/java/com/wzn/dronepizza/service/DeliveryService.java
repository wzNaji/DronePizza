package com.wzn.dronepizza.service;

import com.wzn.dronepizza.entity.Delivery;

import java.util.List;

public interface DeliveryService {

    List<Delivery> getAllNonFinishedDeleveries();

    Delivery createDelivery(Long pizzaId, String address);

    List<Delivery> getAllDeliveriesWithoutDrone();

    Delivery scheduleDelivery(Long deliveryId);

    Delivery finishDelivery(Long deliveryId);
}