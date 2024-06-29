package dev.yerokha.flightstatusapi.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record CreateFlightRequest(
        @NotBlank(message = "Origin must not be empty")
        @Pattern(regexp = "^[A-Z]{3,4}$", message = "Origin must be a valid airport code (3 or 4 uppercase letters)")
        String origin,
        @NotBlank(message = "Destination must not be empty")
        @Pattern(regexp = "^[A-Z]{3,4}$", message = "Destination must be a valid airport code (3 or 4 uppercase letters)")
        String destination,
        @NotNull(message = "Departure time should not be null")
        @Future(message = "Departure time should be in future")
        LocalDateTime departure,
        @NotBlank(message = "Departure time zone should be included")
        @Pattern(regexp = "^[+-](0[0-9]|1[0-4]):[0-5][0-9]$", message = "Invalid offset, please try again")
        String originOffset,
        @NotNull(message = "Arrival time should not be null")
        @Future(message = "Arrival time should be in future")
        LocalDateTime arrival,
        @NotBlank(message = "Arrival time zone should be included")
        @Pattern(regexp = "^[+-](0[0-9]|1[0-4]):[0-5][0-9]$", message = "Invalid offset, please try again")
        String destinationOffset,
        @NotBlank(message = "Current status of the flight should not be empty")
        String flightStatus
) {
}
