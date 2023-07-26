package com.appointment.Approve_Service.Exception;

public class ConnectionRefusedException extends RuntimeException {

    public ConnectionRefusedException(String message) {
        super(message);
    }
}

