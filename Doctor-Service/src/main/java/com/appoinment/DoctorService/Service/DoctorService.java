package com.appoinment.DoctorService.Service;


import com.appoinment.DoctorService.Entity.Doctors;
import com.appoinment.DoctorService.Exception.AddDoctorException;
import com.appoinment.DoctorService.Exception.DoctorsNotFoundException;
import com.appoinment.DoctorService.Repository.DoctorsRepository;
import com.appoinment.DoctorService.Request.DoctorsRequest;
import com.appoinment.DoctorService.Response.MessageResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class DoctorService {

    @Autowired
    private DoctorsRepository doctorsRepository;

    //Add doctor
    public MessageResponse addDoctor(DoctorsRequest doctorsRequest) {
        Doctors doctors = new Doctors();
        doctors.setDoctorName(doctorsRequest.getDoctorName());
        doctors.setQualifications(new ArrayList<>(doctorsRequest.getQualifications()));
        doctors.setSpecializations(new ArrayList<>(doctorsRequest.getSpecializations()));
        doctors.setWorkingHours(doctorsRequest.getWorkingHours());
        doctors.setContactInformation(new ArrayList<>(doctorsRequest.getContactInformation()));
        doctors.setProfessionalExperience(new ArrayList<>(doctorsRequest.getProfessionalExperience()));

        try {
            doctorsRepository.save(doctors);
            return new MessageResponse("Doctor Added Successfully");
        } catch (Exception e) {
            throw new AddDoctorException("Error occurred while adding the doctor: ", e);
        }
    }

    //FindAll
    public Iterable<Doctors> getAllDoctors() {
        Iterable<Doctors> doctors = doctorsRepository.findAll();
        if (!doctors.iterator().hasNext()) {
            throw new DoctorsNotFoundException("No doctors found.");
        }
        return doctors;
    }
}
