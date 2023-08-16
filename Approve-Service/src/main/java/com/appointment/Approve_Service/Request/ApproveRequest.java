package com.appointment.Approve_Service.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApproveRequest {
    private String doctorId;
    private String patientId;
    private String appointmentReason;
    private String appointmentType;
    private String appointmentStatus;
    private LocalDate dateField;
    private String transactionId;
    private String timeField;
}
