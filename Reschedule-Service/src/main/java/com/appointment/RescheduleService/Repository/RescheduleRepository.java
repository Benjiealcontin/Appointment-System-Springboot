package com.appointment.RescheduleService.Repository;

import com.appointment.RescheduleService.Entity.Reschedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RescheduleRepository extends JpaRepository<Reschedule,Long> {
}
