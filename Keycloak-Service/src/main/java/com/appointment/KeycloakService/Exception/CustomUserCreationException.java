package com.appointment.KeycloakService.Exception;

public class CustomUserCreationException extends RuntimeException {
    public CustomUserCreationException(String message) {
        super(message);
    }
}