package dev.yerokha.flightstatusapi.presentation.controller;

import dev.yerokha.flightstatusapi.application.dto.CreateFlightRequest;
import dev.yerokha.flightstatusapi.application.dto.CustomPage;
import dev.yerokha.flightstatusapi.application.service.FlightService;
import dev.yerokha.flightstatusapi.domain.entity.Flight;
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
        return new ResponseEntity<>(flightService.addFlight(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CustomPage<Flight>> getFlights(@RequestParam(required = false) Map<String, String> params) {
        return ResponseEntity.ok(flightService.getFlights(params));
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @PutMapping(value = "/{id}", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Flight> updateFlightStatus(@PathVariable Long id, @RequestBody String status) {
        return ResponseEntity.ok(flightService.updateFlightStatus(id, status));
    }
}
