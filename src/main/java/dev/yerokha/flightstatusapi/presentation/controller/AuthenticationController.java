package dev.yerokha.flightstatusapi.presentation.controller;

import dev.yerokha.flightstatusapi.application.service.UserService;
import dev.yerokha.flightstatusapi.infrastructure.dto.LoginRequest;
import dev.yerokha.flightstatusapi.infrastructure.dto.LoginResponse;
import dev.yerokha.flightstatusapi.infrastructure.dto.RegistrationRequest;
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

    @PostMapping("/registration")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegistrationRequest request) {
        userService.createUser(request);
        return new ResponseEntity<>(new ApiResponse("Registration success! Please login"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.authenticateUser(request));
    }

    @PostMapping(value = "/refresh-token", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody String refreshToken) {
        return ResponseEntity.ok(userService.refreshToken(refreshToken));
    }
}
