package com.appointment.CancelService.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@Table(name = "Cancel")
@NoArgsConstructor
public class Cancel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long doctorId;
    private String transactionId;
    private String patientId;
    private String appointmentReason;
    private String appointmentType;
    private String appointmentStatus;
    private LocalDate dateField;
    private String timeField;
    @CreationTimestamp
    @Column(name = "cancel_at", nullable = false, updatable = false)
    private LocalDateTime cancelAt;
}
