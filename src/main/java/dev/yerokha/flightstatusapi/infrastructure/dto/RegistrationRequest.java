package dev.yerokha.flightstatusapi.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegistrationRequest(
        @NotBlank(message = "Username must not be empty")
        @Pattern(regexp = "^[a-zA-Z\\d]{8,15}$", message = "Name must contain only letters and digits. " +
                                                           "Length must be between 8 and 15 characters")
        String username,
        @NotBlank(message = "Password must not be null")
        @Pattern(regexp = "^(?!.*\\s)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;.,<>/?]).{8,30}$",
                message = "Password length must be between 8-30 characters, and should contain at least 1 upper, " +
                          "1 lower and 1 special symbol")
        String password
) {
}
