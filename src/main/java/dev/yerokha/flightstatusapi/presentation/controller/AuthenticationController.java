package dev.yerokha.flightstatusapi.presentation.controller;

import dev.yerokha.flightstatusapi.application.service.UserService;
import dev.yerokha.flightstatusapi.infrastructure.dto.LoginRequest;
import dev.yerokha.flightstatusapi.infrastructure.dto.LoginResponse;
import dev.yerokha.flightstatusapi.infrastructure.dto.RegistrationRequest;
import dev.yerokha.flightstatusapi.presentation.controller.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Registration", description = "Register as s new user",
            tags = {"post", "auth", "user"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Registration success"),
                    @ApiResponse(responseCode = "400", description = "Validation exception", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Username is already taken", content = @Content),
            }
    )
    @PostMapping("/registration")
    public ResponseEntity<ResponseDto> register(@Valid @RequestBody RegistrationRequest request) {
        userService.createUser(request);
        return new ResponseEntity<>(new ResponseDto("Registration success! Please login"), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Login", description = "User login/password authentication",
            tags = {"post", "user", "auth", "token"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login success"),
                    @ApiResponse(responseCode = "400", description = "Validation exception", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Invalid username or password", content = @Content)
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.authenticateUser(request));
    }

    @Operation(
            summary = "Get refresh token", description = "Send refreshToken and get a new accessToken",
            tags = {"post", "token", "auth"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Request success", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Invalid token", content = @Content)
            }
    )
    @PostMapping(value = "/refresh-token", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody String refreshToken) {
        return ResponseEntity.ok(userService.refreshToken(refreshToken));
    }
}
