package com.appointment.RescheduleService.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RescheduleRequest {
    private String rescheduleReason;
    private LocalDate dateField;
    private String timeField;
}
