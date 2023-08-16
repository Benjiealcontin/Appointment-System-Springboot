package com.appointment.Approve_Service.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveData {
    private String transactionId;
    private String doctorName;
    private LocalDate dateField;
    private String timeField;
    private String location;
    private String appointmentReason;
    private String appointmentType;
    private String patientEmail;
}
