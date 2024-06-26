package dev.yerokha.flightstatusapi.infrastructure.dto;

public record LoginResponse(
        String accessToken, String refreshToken
) {
}

