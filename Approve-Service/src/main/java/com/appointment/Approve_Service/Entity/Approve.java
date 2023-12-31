package com.appointment.Approve_Service.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@Table(name = "Approve")
@NoArgsConstructor
public class Approve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String doctorId;
    private String transactionId;
    private String patientId;
    private String appointmentReason;
    private String appointmentType;
    private String appointmentStatus;
    private LocalDate dateField;
    private String timeField;
    @CreationTimestamp
    @Column(name = "approve_at", nullable = false, updatable = false)
    private LocalDateTime approveAt;
}
