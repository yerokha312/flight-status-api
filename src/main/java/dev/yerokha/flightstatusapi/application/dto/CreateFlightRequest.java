package dev.yerokha.flightstatusapi.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public record CreateFlightRequest(
        @NotNull(message = "Origin must not be null")
        @NotEmpty(message = "Origin must not be empty")
        @Length(max = 256, message = "Origin length must not exceed 256 symbols")
        @Pattern(regexp = "^[A-Z]{3,4}$", message = "Origin must be a valid airport code (3 or 4 uppercase letters)")
        String origin,
        @NotNull(message = "Destination must not be null")
        @NotEmpty(message = "Destination must not be empty")
        @Length(max = 256, message = "Destination length must not exceed 256 symbols")
        @Pattern(regexp = "^[A-Z]{3,4}$", message = "Destination must be a valid airport code (3 or 4 uppercase letters)")
        String destination,
        @NotNull @Future LocalDateTime departure,
        @NotNull @NotEmpty String originOffset,
        @NotNull @Future LocalDateTime arrival,
        @NotNull @NotEmpty String destinationOffset,
        @NotNull String flightStatus
) {
}
