package com.wzn.dronepizza.controller;

import com.wzn.dronepizza.entity.Pizza;
import com.wzn.dronepizza.service.PizzaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pizzas")
public class PizzaController {

    private final PizzaService pizzaService;


    public PizzaController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    /**
     * GET /pizzas
     * Returnerer liste af alle pizzaer.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPizzas() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Pizza> pizzas = pizzaService.getAllPizzas();
            response.put("success", true);
            response.put("message", "Hentet alle pizzaer fra databasen");
            response.put("data", pizzas);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Fejl ved hetning af pizzaer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
