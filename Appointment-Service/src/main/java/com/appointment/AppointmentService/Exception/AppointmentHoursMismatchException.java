package com.appointment.AppointmentService.Exception;

public class AppointmentHoursMismatchException extends RuntimeException {
    public AppointmentHoursMismatchException(String message) {
        super(message);
    }
}
