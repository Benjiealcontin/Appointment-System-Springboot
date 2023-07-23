package com.appoinment.DoctorService.Exception;

public class AddDoctorException extends RuntimeException {
    public AddDoctorException(String message) {
        super(message);
    }

    public AddDoctorException(String message, Throwable cause) {
        super(message, cause);
    }
}

