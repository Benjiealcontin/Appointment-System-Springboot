package com.appointment.AvailableService.Service;

import com.appointment.AvailableService.Entity.Availability;
import com.appointment.AvailableService.Repository.AvailabilityRepository;
import com.appointment.AvailableService.Request.AvailableRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
@Service
public class AvailableService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    //Add Availability of doctors
    public Availability addWorkingHours(AvailableRequest availableRequest) {
        Availability availability = new Availability();
        availability.setDoctorName(availableRequest.getDoctorName());
        availability.setWorkingHours(availableRequest.getWorkingHours());
        return availabilityRepository.save(availability);
    }
}
