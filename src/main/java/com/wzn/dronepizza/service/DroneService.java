package com.wzn.dronepizza.service;

import com.wzn.dronepizza.entity.Drone;

import java.util.List;

public interface DroneService {

    List<Drone> getAllDrones();

    Drone createDrone(Drone drone);

    Drone enableDrone(long droneId);

    Drone disableDrone(long droneId);

    Drone retireDrone(long droneId);

}
