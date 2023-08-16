package com.appointment.Approve_Service.Repository;

import com.appointment.Approve_Service.Entity.Approve;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApproveRepository extends JpaRepository<Approve,Long> {

    List<Approve> findByDoctorIdAndAppointmentStatus(String doctorId, String appointmentStatus);

    Optional<Approve> findByTransactionId(String transactionId);
}
