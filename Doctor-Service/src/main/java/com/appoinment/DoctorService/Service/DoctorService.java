package com.appoinment.DoctorService.Service;


import com.appoinment.DoctorService.Entity.Doctors;
import com.appoinment.DoctorService.Exception.AddDoctorException;
import com.appoinment.DoctorService.Exception.DoctorsNotFoundException;
import com.appoinment.DoctorService.Repository.DoctorsRepository;
import com.appoinment.DoctorService.Request.DoctorsRequest;
import com.appoinment.DoctorService.Response.MessageResponse;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorsRepository doctorsRepository;

    public DoctorService(DoctorsRepository doctorsRepository) {
        this.doctorsRepository = doctorsRepository;
    }

    //Add doctor
    public MessageResponse addDoctor(DoctorsRequest doctorsRequest) {
        Doctors doctors = new Doctors();
        doctors.setDoctorName(doctorsRequest.getDoctorName());
        doctors.setLocation(doctorsRequest.getLocation());
        doctors.setQualifications(new ArrayList<>(doctorsRequest.getQualifications()));
        doctors.setWorkingHours(new ArrayList<>(doctorsRequest.getWorkingHours()));
        doctors.setSpecializations(new ArrayList<>(doctorsRequest.getSpecializations()));
        doctors.setContactInformation(new ArrayList<>(doctorsRequest.getContactInformation()));
        doctors.setProfessionalExperience(new ArrayList<>(doctorsRequest.getProfessionalExperience()));

        try {
            // Save the doctor in the local database
            doctorsRepository.save(doctors);
            return new MessageResponse("Doctor Added Successfully");
        } catch (Exception e) {
            throw new AddDoctorException("Error occurred while adding the doctor: " + e.getMessage(), e);
        }
    }

    //Check if the doctor is Exist
    public boolean checkIfDoctorExists(String doctorName) {
        return doctorsRepository.existsByDoctorName(doctorName);
    }

    //FindAll
    public List<Doctors> getAllDoctors() {
        List<Doctors> doctors = doctorsRepository.findAll();
        if (doctors.isEmpty()) {
            throw new DoctorsNotFoundException("No doctors found.");
        }
        return doctors;
    }

    //FindById
    public Doctors getDoctorById(Long doctorId) {
        Optional<Doctors> doctorOptional = doctorsRepository.findById(doctorId);
        return doctorOptional.orElseThrow(() -> new DoctorsNotFoundException("Doctor with ID " + doctorId + " not found."));
    }

    //FindByDoctorName
    public Doctors getDoctorByName(String doctorName) {
        Optional<Doctors> doctorOptional = doctorsRepository.findByDoctorName(doctorName);
        return doctorOptional.orElseThrow(() -> new DoctorsNotFoundException("Doctor with Doctor name " + doctorName + " not found."));
    }

    //FindBySpecialization
    public Doctors getDoctorsBySpecialization(String doctorSpecialization){
        Optional<Doctors> doctorOptional = doctorsRepository.findBySpecializations(doctorSpecialization);
        return doctorOptional.orElseThrow(() -> new DoctorsNotFoundException("Doctor with Specialization " + doctorSpecialization + " not found."));
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
    public void updateDoctor(Long doctorId, DoctorsRequest doctorsRequest) {
        // Check if the doctor with the given ID exists in the database
        Doctors existingDoctor = doctorsRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorsNotFoundException("Doctor with ID " + doctorId + " not found."));

        // Update the doctor's fields with the new data from the request
        existingDoctor.setDoctorName(doctorsRequest.getDoctorName());
        existingDoctor.setLocation(doctorsRequest.getLocation());
        existingDoctor.setQualifications(new ArrayList<>(doctorsRequest.getQualifications()));
        existingDoctor.setWorkingHours(new ArrayList<>(doctorsRequest.getWorkingHours()));
        existingDoctor.setSpecializations(new ArrayList<>(doctorsRequest.getSpecializations()));
        existingDoctor.setContactInformation(new ArrayList<>(doctorsRequest.getContactInformation()));
        existingDoctor.setProfessionalExperience(new ArrayList<>(doctorsRequest.getProfessionalExperience()));

        // Save the updated doctor to the database
        doctorsRepository.save(existingDoctor);
    }
}
