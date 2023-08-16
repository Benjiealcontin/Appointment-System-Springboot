package com.appointment.CancelService.Repository;

import com.appointment.CancelService.Entity.Cancel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CancelRepository extends JpaRepository<Cancel,Long> {

    Optional<Cancel> findByTransactionId(String transactionId);

    List<Cancel> findByPatientId(String patientId);
}
