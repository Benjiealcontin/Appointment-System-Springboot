package com.appointment.AppointmentService.Repository;

import com.appointment.AppointmentService.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
}