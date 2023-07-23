package com.appoinment.DoctorService.Exception;

public class DoctorsNotFoundException extends RuntimeException {
    public DoctorsNotFoundException(String message) {
        super(message);
    }
}

