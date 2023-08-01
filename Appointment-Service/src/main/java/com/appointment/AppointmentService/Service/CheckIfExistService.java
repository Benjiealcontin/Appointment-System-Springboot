package com.appointment.AppointmentService.Service;

import com.appointment.AppointmentService.Repository.AppointmentRepository;
import com.appointment.AppointmentService.Request.AppointmentRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CheckIfExistService {

    private final AppointmentRepository appointmentRepository;

    public CheckIfExistService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public boolean doesDataExist(AppointmentRequest appointmentRequest) {
        return appointmentRepository.existsByDateFieldAndTimeField(appointmentRequest.getDateField(), appointmentRequest.getTimeField());
    }
}
