package com.appointment.CancelService.Request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelDataForNotification {
    private String transactionId;
    private String doctorEmail;
    private String cancelReason;
    private String patientEmail;
}
