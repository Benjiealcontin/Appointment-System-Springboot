package com.appointment.AppointmentService.Repository;

import com.appointment.AppointmentService.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

    Optional<Appointment> findByTransactionId(String transactionId);
}
