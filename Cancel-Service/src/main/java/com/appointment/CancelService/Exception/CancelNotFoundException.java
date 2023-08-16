package com.appointment.CancelService.Exception;

public class CancelNotFoundException extends RuntimeException{

    public CancelNotFoundException(String message) {
        super(message);
    }
}
