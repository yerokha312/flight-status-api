package dev.yerokha.flightstatusapi.application.service;

import dev.yerokha.flightstatusapi.application.dto.CreateFlightRequest;
import dev.yerokha.flightstatusapi.application.dto.CustomPage;
import dev.yerokha.flightstatusapi.application.exception.InvalidFilterTypeException;
import dev.yerokha.flightstatusapi.application.exception.NotFoundException;
import dev.yerokha.flightstatusapi.application.mapper.CustomPageMapper;
import dev.yerokha.flightstatusapi.domain.entity.Flight;
import dev.yerokha.flightstatusapi.domain.entity.FlightStatus;
import dev.yerokha.flightstatusapi.domain.repository.FlightRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final CustomPageMapper customPageMapper;

    public FlightService(FlightRepository flightRepository, CustomPageMapper customPageMapper) {
        this.flightRepository = flightRepository;
        this.customPageMapper = customPageMapper;
    }

    @Cacheable(value = "flightsLists", key = "#params.hashCode()")
    public CustomPage<Flight> getFlights(Map<String, String> params) {
        String filter = params.get("filter");

        Page<Flight> result;

        if (filter != null && !filter.isEmpty()) {
            FlightFilterType flightFilterType = getFlightFilterType(filter);

            result = switch (flightFilterType) {
                case ORIGIN -> getFlightsByOrigin(params);
                case DESTINATION -> getFlightsByDestination(params);
                case ORIGIN_AND_DESTINATION -> getFlightsByOriginAndDestination(params);
            };
        } else {
            result = getAllFlights(params);
        }

        return customPageMapper.putIntoCustomPage(result);
    }
// we can use Slice instead of Page to prevent additional count() after every query, if we do not need total pages number
    private Page<Flight> getAllFlights(Map<String, String> params) {
        Pageable pageable = getPageable(params);
        return flightRepository.findAllPaged(pageable);
    }

    private Page<Flight> getFlightsByOrigin(Map<String, String> params) {
        Pageable pageable = getPageable(params);
        String origin = params.get("origin");
        return flightRepository.findByOriginIgnoreCase(origin, pageable);
    }

    private Page<Flight> getFlightsByDestination(Map<String, String> params) {
        Pageable pageable = getPageable(params);
        String destination = params.get("destination");
        return flightRepository.findByDestinationIgnoreCase(destination, pageable);
    }

    private Page<Flight> getFlightsByOriginAndDestination(Map<String, String> params) {
        Pageable pageable = getPageable(params);
        String origin = params.get("origin");
        String destination = params.get("destination");
        return flightRepository.findByOriginAndDestinationIgnoreCase(origin, destination, pageable);
    }

    @Cacheable(value = "flight", key = "#id")
    public Flight getFlightById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Flight with ID %d not found", id)));
    }

    // TODO we potentially can add a column to persist the time zone, since PG auto-converts timestampz to UTC
    @CachePut(value = "flight", key = "#result.id")
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

    @CachePut(value = "flight", key = "#id")
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

    // supposed to evict cached first page of getAllFlights() since created Flight probably be the latest one
    @CacheEvict(value = "flightsLists", key = "#params.hashCode()")
    public void evictCache(Map<String, String> params) {
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
}
