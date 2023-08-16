package com.appointment.CancelService.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCancelDetails {
    private String appointmentReason;
    private String appointmentType;
    private LocalDate dateField;
    private String timeField;
}
