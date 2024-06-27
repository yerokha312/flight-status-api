package dev.yerokha.flightstatusapi.application.service;

import dev.yerokha.flightstatusapi.application.dto.CreateFlightRequest;
import dev.yerokha.flightstatusapi.application.dto.CustomPage;
import dev.yerokha.flightstatusapi.application.exception.InvalidFilterTypeException;
import dev.yerokha.flightstatusapi.application.exception.NotFoundException;
import dev.yerokha.flightstatusapi.domain.entity.Flight;
import dev.yerokha.flightstatusapi.domain.entity.FlightStatus;
import dev.yerokha.flightstatusapi.domain.repository.FlightRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static dev.yerokha.flightstatusapi.application.mapper.CustomPageMapper.getCustomPage;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public CustomPage<Flight> getFlights(Map<String, String> params) {
        String filter = params.get("filter");

        if (filter == null || filter.isEmpty()) {
            return getAllFlights(params);
        }

        FlightFilterType flightFilterType = getFlightFilterType(filter);

        return switch (flightFilterType) {
            case ORIGIN -> getFlightsByOrigin(params);
            case DESTINATION -> getFlightsByDestination(params);
            case ORIGIN_AND_DESTINATION -> getFlightsByOriginAndDestination(params);
        };
    }

    private CustomPage<Flight> getAllFlights(Map<String, String> params) {
        Pageable pageable = getPageable(params);
        return getCustomPage(flightRepository.findAllPaged(pageable));
    }

    private CustomPage<Flight> getFlightsByOrigin(Map<String, String> params) {
        Pageable pageable = getPageable(params);
        String origin = params.get("origin");
        return getCustomPage(flightRepository.findByOriginIgnoreCase(origin, pageable));
    }

    private CustomPage<Flight> getFlightsByDestination(Map<String, String> params) {
        Pageable pageable = getPageable(params);
        String destination = params.get("destination");
        return getCustomPage(flightRepository.findByDestinationIgnoreCase(destination, pageable));
    }

    private CustomPage<Flight> getFlightsByOriginAndDestination(Map<String, String> params) {
        Pageable pageable = getPageable(params);
        String origin = params.get("origin");
        String destination = params.get("destination");
        return getCustomPage(flightRepository.findByOriginAndDestinationIgnoreCase(origin, destination, pageable));
    }

    @Transactional
    public Flight addFlight(CreateFlightRequest request) {
        Flight flight = new Flight(
                request.origin(),
                request.destination(),
                OffsetDateTime.of(request.departure(), ZoneOffset.of(request.originOffset())),
                OffsetDateTime.of(request.arrival(), ZoneOffset.of(request.destinationOffset())),
                FlightStatus.valueOf(request.flightStatus().toUpperCase())
        );
        return flightRepository.save(flight);
    }

    @Transactional
    public Flight updateFlightStatus(Long id, FlightStatus status) {
        try {
            Flight flight = flightRepository.getReferenceById(id);
            flight.setFlightStatus(status);
            return flightRepository.save(flight);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(String.format("Flight with ID %d not found", id));
        }
    }

    private static PageRequest getPageable(Map<String, String> params) {
        return PageRequest.of(
                Integer.parseInt(params.getOrDefault("page", "0")),
                Integer.parseInt(params.getOrDefault("size", "10")),
                Sort.by(Sort.Direction.DESC, "arrival")
        );
    }

    private static FlightFilterType getFlightFilterType(String filter) {
        FlightFilterType flightFilterType;
        try {
            flightFilterType = FlightFilterType.valueOf(filter.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidFilterTypeException("Invalid filter type: " + filter);
        }
        return flightFilterType;
    }

    public Flight getFlightById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Flight with ID %d not found", id)));
    }
}
