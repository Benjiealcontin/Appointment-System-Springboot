package com.appoinment.DoctorService.Service;


import com.appoinment.DoctorService.Entity.Doctors;
import com.appoinment.DoctorService.Exception.AddDoctorException;
import com.appoinment.DoctorService.Exception.DoctorsNotFoundException;
import com.appoinment.DoctorService.Repository.DoctorsRepository;
import com.appoinment.DoctorService.Request.DoctorsRequest;
import com.appoinment.DoctorService.Response.MessageResponse;

import com.appointment.AvailableService.Request.AvailableRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorsRepository doctorsRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    //Add doctor
    public MessageResponse addDoctor(DoctorsRequest doctorsRequest) {
        Doctors doctors = new Doctors();
        doctors.setDoctorName(doctorsRequest.getDoctorName());
        doctors.setQualifications(new ArrayList<>(doctorsRequest.getQualifications()));
        doctors.setSpecializations(new ArrayList<>(doctorsRequest.getSpecializations()));
        doctors.setContactInformation(new ArrayList<>(doctorsRequest.getContactInformation()));
        doctors.setProfessionalExperience(new ArrayList<>(doctorsRequest.getProfessionalExperience()));

        try {
            // Save the doctor in the local database
            doctorsRepository.save(doctors);

            // Prepare the data to send to the endpoint
            String doctorName = doctorsRequest.getDoctorName();
            String workingHours = doctorsRequest.getWorkingHours();

            // Create the request body to be sent
            AvailableRequest availableRequest = new AvailableRequest();
            availableRequest.setDoctorName(doctorName);
            availableRequest.setWorkingHours(workingHours);

            // Send the data to the endpoint
            WebClient webClient = webClientBuilder.baseUrl("http://available-service").build();

            // Make the POST request to the endpoint and receive the response
            webClient.post()
                    .uri("/api/available/add")
                    .body(Mono.just(availableRequest), AvailableRequest.class)
                    .exchange()
                    .doOnError(WebClientResponseException.class, ex -> {
                        // Handle WebClient response error
                        throw new AddDoctorException("Error occurred while adding the doctor: " + ex.getRawStatusCode() + " " + ex.getStatusText(), ex);
                    })
                    .block(); // Blocking call to wait for the response

            return new MessageResponse("Doctor Added Successfully");
        } catch (Exception e) {
            throw new AddDoctorException("Error occurred while adding the doctor: " + e.getMessage(), e);
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

    //FindById
    public Doctors getDoctorById(Long doctorId) {
        Optional<Doctors> doctorOptional = doctorsRepository.findById(doctorId);
        return doctorOptional.orElseThrow(() -> new DoctorsNotFoundException("Doctor with ID " + doctorId + " not found."));
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
        existingDoctor.setContactInformation(new ArrayList<>(doctorsRequest.getContactInformation()));
        existingDoctor.setProfessionalExperience(new ArrayList<>(doctorsRequest.getProfessionalExperience()));

        // Save the updated doctor to the database
        doctorsRepository.save(existingDoctor);

        return new MessageResponse("Doctor updated successfully.");
    }

    //Check if the doctor is Exist
    public boolean isDoctorExists(DoctorsRequest doctorsRequest) {
        return doctorsRepository.existsByDoctorName(doctorsRequest.getDoctorName());
    }
}
