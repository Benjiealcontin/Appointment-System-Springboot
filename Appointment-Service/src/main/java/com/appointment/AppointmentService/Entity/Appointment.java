package com.appointment.AppointmentService.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
@Data
@AllArgsConstructor
@Table(name = "Appointment")
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long doctorId;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotBlank(message = "Doctor name is required")
    private String doctorName;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    @NotBlank(message = "Doctor email is required")
    @Email(message = "Invalid doctor email format")
    private String doctorEmail;

    @NotBlank(message = "Patient email is required")
    @Email(message = "Invalid patient email format")
    private String patientEmail;

    @NotBlank(message = "Appointment reason is required")
    private String appointmentReason;

    @NotBlank(message = "Appointment type is required")
    private String appointmentType;

    @NotBlank(message = "Appointment status is required")
    private String appointmentStatus;

    @NotNull(message = "Date field is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateField;

    @NotBlank(message = "Time field is required")
    private String timeField;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;
}
