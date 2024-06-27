package dev.yerokha.flightstatusapi.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegistrationRequest(
        @NotNull(message = "Username must not be null")
        @Pattern(regexp = "^[a-zA-Z\\d]+$", message = "Name must contain only letters and digits")
        String username,
        @NotNull(message = "Password must not be null")
        @Pattern(regexp = "^(?!.*\\s)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;.,<>/?]).{8,30}$",
                message = "Password length must be between 8-30 characters, and should contain at least 1 upper, " +
                          "1 lower and 1 special symbol")
        String password
) {
}
