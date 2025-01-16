package com.wzn.dronepizza.controller;

import com.wzn.dronepizza.entity.Drone;
import com.wzn.dronepizza.service.DroneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/drones")
public class DroneController {

    private final DroneService droneService;

    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    /**
     * GET /drones
     * Returnerer liste af alle droner.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDrones() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Drone> drones = droneService.getAllDrones();
            response.put("success", true);
            response.put("message", "Hentet alle droner fra databasen");
            response.put("data", drones);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Fejl ved hetning af droner: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /drones/add
     * Opretter en ny Drone og kobler den til stationen med færrest droner.
     * Hvis ingen stationer, kastes fejl.
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createNewDrone() {
        Map<String, Object> response = new HashMap<>();
        try {
            Drone drone = new Drone(); // blot et tomt Drone-objekt
            Drone created = droneService.createDrone(drone);
            response.put("success", true);
            response.put("message", "Drone oprettet.");
            response.put("data", created);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Internal error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /drones/enable?droneId=123
     * Ændr en Drone til status "i drift".
     */
    @PostMapping("/enable")
    public ResponseEntity<Map<String, Object>> enableDrone(@RequestParam long droneId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Drone updated = droneService.enableDrone(droneId);
            response.put("success", true);
            response.put("message", "Drone status: i drift.");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Internal error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /drones/disable?droneId=123
     * Ændr en Drone til status "ude af drift".
     */
    @PostMapping("/disable")
    public ResponseEntity<Map<String, Object>> disableDrone(@RequestParam long droneId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Drone updated = droneService.disableDrone(droneId);
            response.put("success", true);
            response.put("message", "Drone status: Ude af drift.");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Internal error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /drones/retire?droneId=123
     * Ændr en Drone til status "udfaset".
     */
    @PostMapping("/retire")
    public ResponseEntity<Map<String, Object>> retireDrone(@RequestParam long droneId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Drone updated = droneService.retireDrone(droneId);
            response.put("success", true);
            response.put("message", "Drone status: Udfaset.");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Internal error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
