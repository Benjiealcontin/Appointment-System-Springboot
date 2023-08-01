package com.appointment.CancelService.Repository;

import com.appointment.CancelService.Entity.Cancel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancelRepository extends JpaRepository<Cancel,Long> {
}
