package com.wzn.dronepizza.controller;

import com.wzn.dronepizza.entity.Delivery;
import com.wzn.dronepizza.service.DeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller for leveringer, der returnerer JSON-objekter
 * med "success", "message", og "data".
 */
@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    /**
     * GET /deliveries
     * Returnerer alle "ikke-færdige" leveringer.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllNonFinishedDeliveries() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Delivery> list = deliveryService.getAllNonFinishedDeleveries();
            response.put("success", true);
            response.put("message", "Fetched all non-finished deliveries.");
            response.put("data", list);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /deliveries/add
     * Tilføjer en bestilling af en given pizza.
     * Request param: pizzaId, address
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createDelivery(
            @RequestParam Long pizzaId,
            @RequestParam String address
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Delivery delivery = deliveryService.createDelivery(pizzaId, address);
            response.put("success", true);
            response.put("message", "Delivery oprettet.");
            response.put("data", delivery);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Internal error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /deliveries/queue
     * Returnerer alle leveringer, der mangler en drone.
     */
    @GetMapping("/queue")
    public ResponseEntity<Map<String, Object>> getAllDeliveriesWithoutDrone() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Delivery> list = deliveryService.getAllDeliveriesWithoutDrone();
            response.put("success", true);
            response.put("message", "Hentet alle deliveries uden droner.");
            response.put("data", list);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /deliveries/schedule
     * Tildeler en drone til en levering, der mangler en drone.
     * Request param: deliveryId, droneId
     */
    @PostMapping("/schedule")
    public ResponseEntity<Map<String, Object>> scheduleDelivery(@RequestParam Long deliveryId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Delivery scheduled = deliveryService.scheduleDelivery(deliveryId);
            response.put("success", true);
            response.put("message", "Delivery scheduled successfully.");
            response.put("data", scheduled);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Fx "Levering har allerede en drone", "Drone er ikke i drift", "Drone med id X ikke fundet", ...
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Internal error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /deliveries/finish
     * Afslutter en given levering her og nu.
     * Request param: deliveryId
     */
    @PostMapping("/finish")
    public ResponseEntity<Map<String, Object>> finishDelivery(@RequestParam Long deliveryId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Delivery finished = deliveryService.finishDelivery(deliveryId);
            response.put("success", true);
            response.put("message", "Delivery afleveret.");
            response.put("data", finished);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Fx "Kan ikke færdiggøre levering, da den ikke har en drone"
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Internal error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
