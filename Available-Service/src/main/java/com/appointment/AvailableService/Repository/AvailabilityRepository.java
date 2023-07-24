package com.appointment.AvailableService.Repository;

import com.appointment.AvailableService.Entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
}
