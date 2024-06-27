package dev.yerokha.flightstatusapi.application.exception.globalexceptionhandler;

import dev.yerokha.flightstatusapi.application.exception.AlreadyExistsException;
import dev.yerokha.flightstatusapi.application.exception.ErrorResponse;
import dev.yerokha.flightstatusapi.application.exception.InvalidArgumentException;
import dev.yerokha.flightstatusapi.application.exception.InvalidFilterTypeException;
import dev.yerokha.flightstatusapi.application.exception.InvalidFlightStatusException;
import dev.yerokha.flightstatusapi.application.exception.InvalidTokenException;
import dev.yerokha.flightstatusapi.application.exception.NotFoundException;
import dev.yerokha.flightstatusapi.infrastructure.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final TokenService tokenService;

    public GlobalExceptionHandler(TokenService tokenService) {
        this.tokenService = tokenService;
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        String username = tokenService.getCurrentUsername();
        logger.error("User '{}' encountered NotFoundException: {}", username, e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), e.getClass().getSimpleName()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String username = tokenService.getCurrentUsername();
        logger.error("User '{}' encountered MethodArgumentNotValidException: {}", username, Objects.requireNonNull(e.getFieldError()).getDefaultMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(e.getFieldError().getDefaultMessage(), e.getClass().getSimpleName()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFilterTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFilterTypeException(InvalidFilterTypeException e) {
        String username = tokenService.getCurrentUsername();
        logger.error("User '{}' encountered InvalidFilterTypeException: {}", username, e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), e.getClass().getSimpleName()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException e) {
        String username = tokenService.getCurrentUsername();
        logger.error("User '{}' encountered InvalidTokenException: {}", username, e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), e.getClass().getSimpleName()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        String username = tokenService.getCurrentUsername();
        logger.error("User '{}' encountered BadCredentialsException: {}", username, e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), e.getClass().getSimpleName()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(AlreadyExistsException e) {
        String username = tokenService.getCurrentUsername();
        logger.error("User '{}' encountered AlreadyExistsException: {}", username, e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), e.getClass().getSimpleName()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidFlightStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFlightStatusException(InvalidFlightStatusException e) {
        String username = tokenService.getCurrentUsername();
        logger.error("User '{}' encountered InvalidFlightStatusException: {}", username, e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), e.getClass().getSimpleName()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidArgumentException(InvalidArgumentException e) {
        String username = tokenService.getCurrentUsername();
        logger.error("User '{}' encountered InvalidArgumentException: {}", username, e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), e.getClass().getSimpleName()), HttpStatus.BAD_REQUEST);
    }
}