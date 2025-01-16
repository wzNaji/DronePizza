package com.wzn.dronepizza.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Data
@NoArgsConstructor
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

    public Delivery(String address, LocalDateTime expectedDeliveryTime, LocalDateTime actualDeliveryTime, Drone drone, Pizza pizza) {
        this.address = address;
        this.expectedDeliveryTime = expectedDeliveryTime;
        this.actualDeliveryTime = actualDeliveryTime;
        this.drone = drone;
        this.pizza = pizza;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setExpectedDeliveryTime(LocalDateTime expectedDeliveryTime) {
        this.expectedDeliveryTime = expectedDeliveryTime;
    }

    public void setActualDeliveryTime(LocalDateTime actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;
    }

    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }
}
