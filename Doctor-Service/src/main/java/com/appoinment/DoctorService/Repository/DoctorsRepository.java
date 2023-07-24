package com.appoinment.DoctorService.Repository;

import com.appoinment.DoctorService.Entity.Doctors;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorsRepository extends JpaRepository<Doctors, Long> {

    boolean existsByDoctorName(String doctorName);

}
