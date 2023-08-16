package com.appointment.AppointmentService.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentData {
    private String transactionId;
//    private String PatientId;
    private String doctorName;
    private String doctorEmail;
//    private String patientEmail;
    private String appointmentReason;
    private String appointmentType;
    private String appointmentStatus;
    private LocalDate dateField;
    private String timeField;
}
