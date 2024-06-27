package dev.yerokha.flightstatusapi.domain.repository;

import dev.yerokha.flightstatusapi.domain.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Page<Flight> findByOriginIgnoreCase(String origin, Pageable pageable);

    Page<Flight> findByDestinationIgnoreCase(String destination, Pageable pageable);

    Page<Flight> findByOriginAndDestinationIgnoreCase(String origin, String destination, Pageable pageable);

    @Query("SELECT f FROM Flight f")
    Page<Flight> findAllPaged(Pageable pageable);
}

