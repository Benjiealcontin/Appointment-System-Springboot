package com.appointment.AppointmentService.Request;


import com.appointment.AppointmentService.Entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    private String transactionId = UUID.randomUUID().toString();
    private long doctorId;
    private String doctorName;
    private String location;
    private String appointmentReason;
    private String email;
    private String appointmentType;
    private String appointmentStatus;
    private LocalDate dateField;
    private String timeField;
}
