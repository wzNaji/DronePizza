package com.wzn.dronepizza.entity;

import jakarta.persistence.*;


import java.util.UUID;

@Entity
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
    public Drone () {

    }

    public Drone(long id, DroneStatus droneStatus) {
        this.id = id;
        this.status = droneStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(UUID serialNumber) {
        this.serialNumber = serialNumber;
    }

    public DroneStatus getStatus() {
        return status;
    }

    public void setStatus(DroneStatus status) {
        this.status = status;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }
}
