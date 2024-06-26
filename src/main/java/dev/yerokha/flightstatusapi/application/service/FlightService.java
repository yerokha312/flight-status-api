package dev.yerokha.flightstatusapi.application.service;

import dev.yerokha.flightstatusapi.application.dto.CreateFlightRequest;
import dev.yerokha.flightstatusapi.application.dto.CustomPage;
import dev.yerokha.flightstatusapi.domain.entity.Flight;
import dev.yerokha.flightstatusapi.domain.entity.Status;
import dev.yerokha.flightstatusapi.domain.repository.FlightRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;

import static dev.yerokha.flightstatusapi.application.mapper.CustomPageMapper.getCustomPage;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public CustomPage<Flight> getAllFlights(Map<String, String> params) {
        Pageable pageable = getPageable(params);
        return getCustomPage(flightRepository.findAllPaged(pageable));
    }

    public CustomPage<Flight> getFlightsByOrigin(String origin, Map<String, String> params) {
        Pageable pageable = getPageable(params);
        return getCustomPage(flightRepository.findByOrigin(origin, pageable));
    }

    public CustomPage<Flight> getFlightsByDestination(String destination, Map<String, String> params) {
        Pageable pageable = getPageable(params);
        return getCustomPage(flightRepository.findByDestination(destination, pageable));
    }

    public CustomPage<Flight> getFlightsByOriginAndDestination(String origin, String destination, Map<String, String> params) {
        Pageable pageable = getPageable(params);
        return getCustomPage(flightRepository.findByOriginAndDestination(origin, destination, pageable));
    }

    @Transactional
    public Flight addFlight(CreateFlightRequest request) {
        Flight flight = new Flight(

        );
        return flightRepository.save(flight);
    }

    @Transactional
    public Flight updateFlightStatus(Long id, Status status) {
        Flight flight = flightRepository.findById(id).orElseThrow(() -> new RuntimeException("Flight not found"));
        flight.setStatus(status);
        return flightRepository.save(flight);
    }

    private static PageRequest getPageable(Map<String, String> params) {
        return PageRequest.of(
                Integer.parseInt(params.getOrDefault("page", "0")),
                Integer.parseInt(params.getOrDefault("size", "10")),
                Sort.by(Sort.Direction.DESC, "arrival")
        );
    }
}
