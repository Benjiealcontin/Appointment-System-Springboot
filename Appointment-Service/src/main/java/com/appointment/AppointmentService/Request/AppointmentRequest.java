package com.appointment.AppointmentService.Request;


import com.appointment.AppointmentService.Entity.Appointment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    private String transactionId = UUID.randomUUID().toString();
    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
//    private String doctorName;
//    private String location;
    @NotBlank(message = "Appointment reason is required")
    private String appointmentReason;

    @NotBlank(message = "Appointment type is required")
    private String appointmentType;
//    private String appointmentStatus;
    @NotNull(message = "Date field is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateField;

    @NotBlank(message = "Time field is required")
    private String timeField;
}
