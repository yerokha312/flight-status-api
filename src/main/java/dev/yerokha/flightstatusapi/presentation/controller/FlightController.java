package dev.yerokha.flightstatusapi.presentation.controller;

import dev.yerokha.flightstatusapi.application.dto.CreateFlightRequest;
import dev.yerokha.flightstatusapi.application.dto.CustomPage;
import dev.yerokha.flightstatusapi.application.exception.InvalidArgumentException;
import dev.yerokha.flightstatusapi.application.exception.InvalidFlightStatusException;
import dev.yerokha.flightstatusapi.application.service.FlightService;
import dev.yerokha.flightstatusapi.domain.entity.Flight;
import dev.yerokha.flightstatusapi.domain.entity.FlightStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @PostMapping
    public ResponseEntity<Flight> createFlight(@Valid @RequestBody CreateFlightRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("page", "0");
        params.put("size", "10"); //TODO push every web client to use the same page size
        flightService.evictCache(params);
        Flight flight = flightService.addFlight(request);
        return new ResponseEntity<>(flight, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CustomPage<Flight>> getFlights(@RequestParam(required = false) Map<String, String> params) {
        return ResponseEntity.ok(flightService.getFlights(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getOneFlight(@PathVariable Long id) {
        if (id < 1) {
            throw new InvalidArgumentException("Invalid flight ID");
        }
        return ResponseEntity.ok(flightService.getFlightById(id));
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @PutMapping(value = "/{id}", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Flight> updateFlightStatus(@PathVariable Long id, @RequestBody String status) {
        if (id < 1) {
            throw new InvalidArgumentException("Invalid flight ID");
        }
        FlightStatus flightStatus;
        try {
            flightStatus = FlightStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidFlightStatusException("Invalid flight status. Try again");
        }
        return ResponseEntity.ok(flightService.updateFlightStatus(id, flightStatus));
    }
}
