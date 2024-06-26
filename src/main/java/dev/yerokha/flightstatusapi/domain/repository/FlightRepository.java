package dev.yerokha.flightstatusapi.domain.repository;

import dev.yerokha.flightstatusapi.domain.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Page<Flight> findByOrigin(String origin, Pageable pageable);
    Page<Flight> findByDestination(String destination, Pageable pageable);
    Page<Flight> findByOriginAndDestination(String origin, String destination, Pageable pageable);
}

