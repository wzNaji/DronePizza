package com.wzn.dronepizza.repository;

import com.wzn.dronepizza.entity.Drone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {
    long countByStationId(Long stationId);
}
