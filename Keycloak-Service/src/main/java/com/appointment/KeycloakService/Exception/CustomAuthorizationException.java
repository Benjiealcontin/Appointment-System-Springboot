package com.appointment.KeycloakService.Exception;

public class CustomAuthorizationException extends RuntimeException {
    public CustomAuthorizationException(String message) {
        super(message);
    }
}
