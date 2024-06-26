package dev.yerokha.flightstatusapi.infrastructure.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(
        @NotNull(message = "Username must not be null")
        @NotEmpty(message = "Username must not be empty")
        String username,
        @NotNull(message = "Password must not be null")
        @NotEmpty(message = "Password must not be empty")
        @Length(min = 8, max = 30, message = "Password length must be between 8 and 30")
        String password
) {
}
