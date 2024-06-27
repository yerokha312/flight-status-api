package dev.yerokha.flightstatusapi.infrastructure.dto;

import jakarta.validation.constraints.NotNull;

public record LoginResponse(
        @NotNull String accessToken,
        @NotNull String refreshToken
) {
}

