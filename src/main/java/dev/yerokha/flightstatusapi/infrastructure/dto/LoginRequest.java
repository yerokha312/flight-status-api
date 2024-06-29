package dev.yerokha.flightstatusapi.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(
        @NotBlank(message = "Username must not be empty")
        String username,
        @NotBlank(message = "Password must not be empty")
        @Length(min = 8, max = 30, message = "Password length must be between 8 and 30")
        String password
) {
}
