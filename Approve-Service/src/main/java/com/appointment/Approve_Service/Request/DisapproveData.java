package com.appointment.Approve_Service.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisapproveData {
    private String transactionId;
    private String disapproveReason;
    private String patientEmail;
}
