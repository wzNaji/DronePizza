package com.wzn.dronepizza.service.impl;

import com.wzn.dronepizza.entity.Pizza;
import com.wzn.dronepizza.repository.PizzaRepository;
import com.wzn.dronepizza.service.PizzaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PizzaServiceImpl implements PizzaService {

    private final PizzaRepository pizzaRepository;

    public PizzaServiceImpl(PizzaRepository pizzaRepository) {
        this.pizzaRepository = pizzaRepository;
    }


    @Override
    public List<Pizza> getAllPizzas() {
        return pizzaRepository.findAll();
    }
}
