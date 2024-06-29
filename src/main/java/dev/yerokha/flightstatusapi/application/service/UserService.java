package dev.yerokha.flightstatusapi.application.service;

import dev.yerokha.flightstatusapi.application.exception.AlreadyExistsException;
import dev.yerokha.flightstatusapi.application.exception.NotFoundException;
import dev.yerokha.flightstatusapi.domain.entity.UserEntity;
import dev.yerokha.flightstatusapi.domain.repository.RoleRepository;
import dev.yerokha.flightstatusapi.domain.repository.UserRepository;
import dev.yerokha.flightstatusapi.infrastructure.dto.LoginRequest;
import dev.yerokha.flightstatusapi.infrastructure.dto.LoginResponse;
import dev.yerokha.flightstatusapi.infrastructure.dto.RegistrationRequest;
import dev.yerokha.flightstatusapi.infrastructure.service.TokenService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, @Lazy AuthenticationManager authenticationManager, TokenService tokenService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public void createUser(RegistrationRequest request) {
        String username = request.username().toLowerCase();
        boolean usernameExists = userRepository.existsByUsernameIgnoreCase(username);
        if (usernameExists) {
            throw new AlreadyExistsException("Username is already taken. Try another one.");
        }
        UserEntity user = new UserEntity(request.username(),
                passwordEncoder.encode(request.password()),
                roleRepository.getReferenceById(1));
        userRepository.save(user);
    }

    public LoginResponse authenticateUser(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(), request.password()));

            UserEntity entity = (UserEntity) authentication.getPrincipal();

            return new LoginResponse(
                    tokenService.generateAccessToken(entity),
                    tokenService.generateRefreshToken(entity)
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public LoginResponse refreshToken(String refreshToken) {
        return tokenService.refreshAccessToken(refreshToken);
    }
}

