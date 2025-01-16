package com.wzn.dronepizza.service.impl;

import com.wzn.dronepizza.entity.Drone;
import com.wzn.dronepizza.entity.DroneStatus;
import com.wzn.dronepizza.entity.Station;
import com.wzn.dronepizza.repository.DroneRepository;
import com.wzn.dronepizza.repository.StationRepository;
import com.wzn.dronepizza.service.DroneService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DroneServiceImpl implements DroneService {

    private final DroneRepository droneRepository;
    private final StationRepository stationRepository;

    public DroneServiceImpl(DroneRepository droneRepository, StationRepository stationRepository) {
        this.droneRepository = droneRepository;
        this.stationRepository = stationRepository;
    }

    /**
     * Returnerer alle droner i databasen
     */
    public List<Drone> getAllDrones() {
        return droneRepository.findAll();
    }

    /**
     * Opretter en ny drone og tildeler den en station.
     * @param drone Dronen, der skal oprettes.
     * @return Den oprettede drone.
     */
    public Drone createDrone(Drone drone) {

        // Finder alle stationer
        List<Station> stations = stationRepository.findAll();
        if (stations.isEmpty()) {
            throw new IllegalStateException("Oprettelsen fejlede. Fandt ingen stationer.");
        }

        //Finder station med færrest antal droner
        Station stationWithFewestDrones = stations.get(0);
        int lowestDroneCount = getDroneCountForStation(stationWithFewestDrones);

        for (Station station : stations) {
            int droneCount = getDroneCountForStation(station);
            if (droneCount < lowestDroneCount) {
                stationWithFewestDrones = station;
                lowestDroneCount = droneCount;
            }
        }

        // Tildeler station med færreste antal droner, til den oprettede drone
        drone.setStation(stationWithFewestDrones);
        drone.setSerialNumber(UUID.randomUUID());
        drone.setStatus(DroneStatus.I_DRIFT);

        // Gemmer i db
        return droneRepository.save(drone);
    }

    /**
     * Returnerer antallet af droner for en given station.
     * @param station Stationen, der skal tælles droner for.
     * @return Antallet af droner.
     */
    private int getDroneCountForStation(Station station) {
        // Finder antallet af droner tilknyttet en station
        return (int) droneRepository.countByStationId(station.getId());
    }
}

