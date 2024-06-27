package dev.yerokha.flightstatusapi.presentation.controller;

import dev.yerokha.flightstatusapi.application.dto.CreateFlightRequest;
import dev.yerokha.flightstatusapi.application.dto.CustomPage;
import dev.yerokha.flightstatusapi.application.exception.InvalidArgumentException;
import dev.yerokha.flightstatusapi.application.exception.InvalidFlightStatusException;
import dev.yerokha.flightstatusapi.application.service.FlightService;
import dev.yerokha.flightstatusapi.domain.entity.Flight;
import dev.yerokha.flightstatusapi.domain.entity.FlightStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary = "Add a new flight", tags = {"post", "flight", "moderator"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Add flight success"),
                    @ApiResponse(responseCode = "400", description = "Validation exception", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Has no authority", content = @Content)
            }
    )
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

    @Operation(
            summary = "Get flights list", tags = {"get", "flight"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Request success"),
                    @ApiResponse(responseCode = "400", description = "Invalid filter type", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
            },
            parameters = {
                    @Parameter(name = "page", allowEmptyValue = true, description = "Page number, default 0"),
                    @Parameter(name = "size", allowEmptyValue = true, description = "Page size, default 10"),
                    @Parameter(name = "filter", allowEmptyValue = true, description = "Filtering property. If enabled " +
                                                            "origin or/and destination property is mandatory"),
                    @Parameter(name = "origin", allowEmptyValue = true, description = "IATA code of departing airport"),
                    @Parameter(name = "destination", allowEmptyValue = true, description = "IATA code of arriving airport"),
            }
    )
    @GetMapping
    public ResponseEntity<CustomPage<Flight>> getFlights(@RequestParam(required = false) Map<String, String> params) {
        return ResponseEntity.ok(flightService.getFlights(params));
    }

    @Operation(
            summary = "Get flight by id", description = "Get one Flight details by it's ID",
            tags = {"get", "flight"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Request success"),
                    @ApiResponse(responseCode = "400", description = "Invalid ID (0 or negative)", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Flight not found", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Flight> getOneFlight(@PathVariable Long id) {
        if (id < 1) {
            throw new InvalidArgumentException("Invalid flight ID");
        }
        return ResponseEntity.ok(flightService.getFlightById(id));
    }

    @Operation(
            summary = "Update flight status", description = "Update flight's status by it's ID",
            tags = {"put", "flight"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Request success"),
                    @ApiResponse(responseCode = "400", description = "Invalid ID (0 or negative)", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid flight status", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Flight not found", content = @Content)
            }
    )
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
