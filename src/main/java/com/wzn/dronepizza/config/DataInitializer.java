package com.wzn.dronepizza.config;


import com.wzn.dronepizza.entity.*;
import com.wzn.dronepizza.repository.DeliveryRepository;
import com.wzn.dronepizza.repository.DroneRepository;
import com.wzn.dronepizza.repository.PizzaRepository;
import com.wzn.dronepizza.repository.StationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData( // cmdLineRunner gør det muligt at udføre kode efter app er startet, via dens 'run' metode
            StationRepository stationRepo,
            PizzaRepository pizzaRepo,
            DroneRepository droneRepo,
            DeliveryRepository deliveryRepo
    ) {
        return args -> {

            // 1) Opret stationer
            Station station1 = new Station(55.41, 12.34);       // "Centrum"
            Station station2 = new Station(55.42, 12.33);       // Lidt nordvest
            Station station3 = new Station(55.40, 12.36);       // Lidt sydøst
            stationRepo.saveAll(List.of(station1, station2, station3));

            // 2) Opret pizzaer
            Pizza pizza1 = new Pizza("Margherita", 65);
            Pizza pizza2 = new Pizza("Pepperoni", 75);
            Pizza pizza3 = new Pizza("Hawaii", 80);
            Pizza pizza4 = new Pizza("Vegetariana", 70);
            Pizza pizza5 = new Pizza("Meat Lovers", 90);
            pizzaRepo.saveAll(List.of(pizza1, pizza2, pizza3, pizza4, pizza5));

            // 3) Opret Droner
            Drone drone1 = new Drone(UUID.randomUUID(), DroneStatus.I_DRIFT, station1);
            Drone drone2 = new Drone(UUID.randomUUID(), DroneStatus.UDE_AF_DRIFT, station2);
            Drone drone3 = new Drone(UUID.randomUUID(), DroneStatus.UDFASET, station3);
            droneRepo.saveAll(List.of(drone1, drone2, drone3));

            // 4) Opret eksempler på Leveringer
            Delivery delivery1 = new Delivery(
                    "Nørrebrogade 10, 2200 København N",
                    LocalDateTime.now().plusHours(1),  // Forventet levering om 1 time
                    pizza1
            );
            // Delivery starter uden drone og uden actualDeliveryTime
            deliveryRepo.save(delivery1);

            // Eksempel på en levering der får tildelt en drone og bliver leveret
            Delivery delivery2 = new Delivery(
                    "Østerbrogade 20, 2100 København Ø",
                    LocalDateTime.now().plusHours(2),
                    pizza2
            );
            delivery2.setDrone(drone1);
            delivery2.setActualDeliveryTime(LocalDateTime.now().plusHours(2));
            deliveryRepo.save(delivery2);


            System.out.println("Testdata er indlæst i databasen!");
        };
    }
}
