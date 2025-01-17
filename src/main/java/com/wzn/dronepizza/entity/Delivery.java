package com.wzn.dronepizza.entity;

import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    private LocalDateTime expectedDeliveryTime;
    private LocalDateTime actualDeliveryTime;

    @ManyToOne
    private Drone drone;

    @ManyToOne
    private Pizza pizza;

    public Delivery(String address, LocalDateTime expectedDeliveryTime, Pizza pizza) {
        this.address = address;
        this.expectedDeliveryTime = expectedDeliveryTime;
        this.pizza = pizza;
    }

    public Delivery() {

    }
    public Delivery(String address, LocalDateTime expectedDeliveryTime, LocalDateTime actualDeliveryTime, Drone drone, Pizza pizza) {
        this.address = address;
        this.expectedDeliveryTime = expectedDeliveryTime;
        this.actualDeliveryTime = actualDeliveryTime;
        this.drone = drone;
        this.pizza = pizza;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getExpectedDeliveryTime() {
        return expectedDeliveryTime;
    }

    public void setExpectedDeliveryTime(LocalDateTime expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
    }

    public LocalDateTime getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    public void setActualDeliveryTime(LocalDateTime actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }

    public Drone getDrone() {
        return drone;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;
    }

    public Pizza getPizza() {
        return pizza;
    }

    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }
}
