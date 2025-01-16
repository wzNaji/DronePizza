package com.wzn.dronepizza.service.impl;

import com.wzn.dronepizza.entity.Drone;
import com.wzn.dronepizza.entity.DroneStatus;
import com.wzn.dronepizza.entity.Station;
import com.wzn.dronepizza.repository.DroneRepository;
import com.wzn.dronepizza.repository.StationRepository;
import com.wzn.dronepizza.service.DroneService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
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

        // Cacher dronecount for hver station
        Map<Long, Integer> droneCounts = new HashMap<>();
        for (Station station : stations) {
            droneCounts.put(station.getId(), getDroneCountForStation(station));
        }

        // finder station med laveste dronecount
        Station stationWithFewestDrones = stations.get(0);
        int lowestDroneCount = droneCounts.get(stationWithFewestDrones.getId());
        for (Station station : stations) {
            int droneCount = droneCounts.get(station.getId());
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
     * Ændr en Drone til status "i drift"
     */
    @Override
    public Drone enableDrone(long droneId) {
        Drone drone = findDroneOrThrow(droneId);
        drone.setStatus(DroneStatus.I_DRIFT);
        return droneRepository.save(drone);
    }


    /**
     * Ændr en Drone til status "ude af drift"
     */
    @Override
    public Drone disableDrone(long droneId) {
            Drone drone = findDroneOrThrow(droneId);
            drone.setStatus(DroneStatus.UDE_AF_DRIFT);
            return droneRepository.save(drone);
    }


    /**
     * Ændr en Drone til status "udfaset"
     */
    @Override
    public Drone retireDrone(long droneId) {
        Drone drone = findDroneOrThrow(droneId);
        drone.setStatus(DroneStatus.UDFASET);
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

    /**
     * Hjælpemetode til at slå en Drone op eller kaste en fejl
     */
    private Drone findDroneOrThrow(Long droneId) {
        return droneRepository.findById(droneId)
                .orElseThrow(() -> new IllegalArgumentException("Drone med id " + droneId + " blev ikke fundet."));
    }
}

