package dev.yerokha.flightstatusapi.application.exception;

public class InvalidFilterTypeException extends RuntimeException {
    public InvalidFilterTypeException(String message) {
        super(message);
    }
}
