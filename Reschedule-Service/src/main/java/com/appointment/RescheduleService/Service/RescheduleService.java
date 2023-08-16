package com.appointment.RescheduleService.Service;

import com.appointment.Approve_Service.Entity.Approve;
import com.appointment.RescheduleService.Entity.Reschedule;
import com.appointment.RescheduleService.Repository.RescheduleRepository;
import com.appointment.RescheduleService.Request.RescheduleRequest;
import com.appointment.RescheduleService.Response.MessageResponse;
import com.google.common.net.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RescheduleService {

    private final RescheduleRepository rescheduleRepository;
    private final WebClient.Builder webClientBuilder;

    public RescheduleService(RescheduleRepository rescheduleRepository, WebClient.Builder webClientBuilder) {
        this.rescheduleRepository = rescheduleRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public MessageResponse reschedule(String transactionId, RescheduleRequest rescheduleRequest, String bearerToken){

        //Get the data from Appointment
        Approve Approve = webClientBuilder.build()
                .get()
                .uri("http://Approve-Service/api/approve/findByTransactionId/{transactionId}", transactionId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken) // Add Authorization header
                .retrieve()
                .bodyToMono(Approve.class)
                .block();

        if (Approve != null) {
            Reschedule reschedule = Reschedule.builder()
                    .appointmentReason(Approve.getAppointmentReason())
                    .appointmentType(Approve.getAppointmentType())
                    .patientId(Approve.getPatientId())
                    .doctorId(Approve.getDoctorId())
                    .transactionId(Approve.getTransactionId())
                    .rescheduleReason(rescheduleRequest.getRescheduleReason())
                    .dateField(rescheduleRequest.getDateField())
                    .timeField(rescheduleRequest.getTimeField())
                    .appointmentStatus("Reschedule")
                    .build();

            Approve approve = new Approve();
            approve.setAppointmentStatus("Reschedule");

            rescheduleRepository.save(reschedule);
        }else{
            // Throw an exception if the appointment object is null
            throw new IllegalArgumentException("The appointment object is null.");
        }

        return new MessageResponse("Reschedule Appointment Succesfuly");
    }
}
