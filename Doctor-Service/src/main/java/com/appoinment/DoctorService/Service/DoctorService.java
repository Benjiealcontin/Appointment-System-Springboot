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

    //Delete Doctor
    public void deleteDoctor(Long doctorId) {
        // Check if the doctor exists in the database
        if (!doctorsRepository.existsById(doctorId)) {
            throw new DoctorsNotFoundException("Doctor with ID " + doctorId + " not found.");
        }
        // Delete the doctor from the database
        doctorsRepository.deleteById(doctorId);
    }

    //Update doctors
    public MessageResponse updateDoctor(Long doctorId, DoctorsRequest doctorsRequest) {
        // Check if the doctor with the given ID exists in the database
        Doctors existingDoctor = doctorsRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorsNotFoundException("Doctor with ID " + doctorId + " not found."));

        // Update the doctor's fields with the new data from the request
        existingDoctor.setDoctorName(doctorsRequest.getDoctorName());
        existingDoctor.setQualifications(new ArrayList<>(doctorsRequest.getQualifications()));
        existingDoctor.setSpecializations(new ArrayList<>(doctorsRequest.getSpecializations()));
        existingDoctor.setWorkingHours(doctorsRequest.getWorkingHours());
        existingDoctor.setContactInformation(new ArrayList<>(doctorsRequest.getContactInformation()));
        existingDoctor.setProfessionalExperience(new ArrayList<>(doctorsRequest.getProfessionalExperience()));

        // Save the updated doctor to the database
        doctorsRepository.save(existingDoctor);

        return new MessageResponse("Doctor updated successfully.");
    }
}
