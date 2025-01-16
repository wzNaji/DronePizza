package com.wzn.dronepizza.repository;

import com.wzn.dronepizza.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery,Long> {
}
