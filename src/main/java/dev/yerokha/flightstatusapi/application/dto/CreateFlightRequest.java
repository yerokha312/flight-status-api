package dev.yerokha.flightstatusapi.application.dto;

import dev.yerokha.flightstatusapi.domain.entity.Status;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateFlightRequest(
        @NotNull @NotEmpty String origin,
        @NotNull @NotEmpty String destination,
        @NotNull LocalDateTime departure,
        @NotNull LocalDateTime arrival,
        @NotNull Status status
) {
}
