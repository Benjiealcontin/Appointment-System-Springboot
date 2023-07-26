package com.appoinment.DoctorService.Repository;

import com.appoinment.DoctorService.Entity.Doctors;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorsRepository extends JpaRepository<Doctors, Long> {

    boolean existsByDoctorName(String doctorName);

    Optional<Doctors> findByDoctorName(String doctorName);

    Optional<Doctors> findBySpecializations(String specializations);

}
