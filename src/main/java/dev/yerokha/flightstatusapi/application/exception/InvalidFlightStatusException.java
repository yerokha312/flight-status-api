package dev.yerokha.flightstatusapi.application.exception;

public class InvalidFlightStatusException extends RuntimeException {
    public InvalidFlightStatusException(String message) {
        super(message);
    }
}
