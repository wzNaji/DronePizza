package com.wzn.dronepizza.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Drone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID serialNumber;

    @Enumerated(EnumType.STRING)
    private DroneStatus status;

    @ManyToOne
    private Station station;

    public Drone(UUID serialNumber, DroneStatus status, Station station) {
        this.serialNumber = serialNumber;
        this.status = status;
        this.station = station;
    }



}
