package com.appointment.AppointmentService.Exception;

import org.springframework.dao.DataAccessException;

public class AddAppointmentException extends RuntimeException {
    public AddAppointmentException(String message, DataAccessException e) {
        super(message);
    }
}