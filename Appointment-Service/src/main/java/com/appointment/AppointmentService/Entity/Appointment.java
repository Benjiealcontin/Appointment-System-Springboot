package com.appointment.AppointmentService.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;


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
    private String patientId;
    private String appointmentReason;
    private String appointmentType;
    private String appointmentStatus;
    @Temporal(TemporalType.DATE)
    @Column(name = "date_field")
    private Date dateField;
    @Temporal(TemporalType.TIME)
    @Column(name = "time_field")
    private Date timeField;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;
}
