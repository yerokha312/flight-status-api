package dev.yerokha.flightstatusapi.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record CreateFlightRequest(
        @NotNull(message = "Origin must not be null")
        @NotEmpty(message = "Origin must not be empty")
        @Pattern(regexp = "^[A-Z]{3,4}$", message = "Origin must be a valid airport code (3 or 4 uppercase letters)")
        String origin,
        @NotNull(message = "Destination must not be null")
        @NotEmpty(message = "Destination must not be empty")
        @Pattern(regexp = "^[A-Z]{3,4}$", message = "Destination must be a valid airport code (3 or 4 uppercase letters)")
        String destination,
        @NotNull(message = "Departure time should not be null")
        @Future(message = "Departure time should be in future")
        LocalDateTime departure,
        @NotNull(message = "Departure time zone should be included")
        @NotEmpty(message = "Departure time zone should be included")
        String originOffset,
        @NotNull(message = "Arrival time should not be null")
        @Future(message = "Arrival time should be in future")
        LocalDateTime arrival,
        @NotNull(message = "Arrival time zone should be included")
        @NotEmpty(message = "Arrival time zone should be included")
        String destinationOffset,
        @NotNull(message = "Please include current status of the flight")
        @NotEmpty(message = "Current status of the flight should not be empty")
        String flightStatus
) {
}
