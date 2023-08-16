package com.appointment.RescheduleService.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@Table(name = "Reschedule")
@NoArgsConstructor
@Builder
public class Reschedule {
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
    private String rescheduleReason;
    @CreationTimestamp
    @Column(name = "reschedule_at", nullable = false, updatable = false)
    private LocalDateTime rescheduleAt;
}
