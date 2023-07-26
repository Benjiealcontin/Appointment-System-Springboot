package com.appointment.Approve_Service.Service;

import com.appointment.AppointmentService.Entity.Appointment;
import com.appointment.AppointmentService.Exception.AppointmentNotFoundException;
import com.appointment.Approve_Service.Entity.Approve;
import com.appointment.Approve_Service.Exception.ApprovalException;
import com.appointment.Approve_Service.Exception.ApproveNotFoundException;
import com.appointment.Approve_Service.Exception.WebClientException;
import com.appointment.Approve_Service.Repository.ApproveRepository;
import com.appointment.Approve_Service.Request.ApproveRequest;
import com.appointment.Approve_Service.Response.MessageResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;

@Service
public class ApproveService {

    private final ApproveRepository approveRepository;

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public ApproveService(ApproveRepository approveRepository, WebClient.Builder webClientBuilder) {
        this.approveRepository = approveRepository;
        this.webClientBuilder = webClientBuilder;
    }

    //Approve Appointment
    public MessageResponse approveAppointment(String transactionId,String token){
       try{
           //Get the data from Appointment
           Appointment appointment = webClientBuilder.build()
                   .get()
                   .uri("http://Appointment-Service/api/appointment/transactionId/{transactionId}", transactionId)
                   .header(HttpHeaders.AUTHORIZATION, token) // Add Authorization header
                   .retrieve()
                   .bodyToMono(Appointment.class)
                   .block();

           //Save the Approve data
           if (appointment != null) {
               Approve approve = new Approve();
               approve.setAppointmentReason(appointment.getAppointmentReason());
               approve.setAppointmentType(appointment.getAppointmentType());
               approve.setAppointmentType(transactionId);
               approve.setDoctorId(appointment.getDoctorId());
               approve.setAppointmentStatus("Approved");
               approve.setPatientId(appointment.getPatientId());
               approve.setDateField(appointment.getDateField());
               approve.setTimeField(appointment.getTimeField());

               approveRepository.save(approve);

               //Delete after save the data from Appointment
               webClientBuilder.build()
                       .delete()
                       .uri("http://Appointment-Service/api/appointment/delete/{appointmentId}", appointment.getId())
                       .header(HttpHeaders.AUTHORIZATION, token) // Add Authorization header
                       .retrieve()
                       .bodyToMono(Appointment.class)
                       .block();
           } else {
               // Throw an exception if the appointment object is null
               throw new IllegalArgumentException("The appointment object is null.");
           }

           return new MessageResponse("Appointment Approved Successfully");
       } catch (WebClientResponseException.NotFound ex) {
           // Parse the error response to get the message
           String responseBody = ex.getResponseBodyAsString();
           String errorMessage = parseErrorMessage(responseBody);

           // Rethrow the AppointmentNotFoundException from the ApprovalService
           throw new AppointmentNotFoundException(errorMessage);
       } catch (WebClientResponseException.ServiceUnavailable ex) {
           throw new WebClientException("Error occurred while calling the external service: ","Service Unavailable from Appointment Service");
       }catch (Exception e){
           throw new ApprovalException("Error occurred while approving the appointment.", e.getMessage());
       }
    }
    private String parseErrorMessage(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("message").asText();
        } catch (Exception e) {
            // If there's an error while parsing, return the original response body
            return responseBody;
        }
    }

    //FindAll Approve
    public Iterable<Approve> getAllApproveAppointment(){
        Iterable<Approve> approve = approveRepository.findAll();
        if (!approve.iterator().hasNext()) {
            throw new ApproveNotFoundException("No approve found.");
        }
        return approve;
    }

    //FindById
    public Approve getApproveById(Long approveId){
        Optional<Approve> approveOptional = approveRepository.findById(approveId);
        return approveOptional.orElseThrow(() -> new ApproveNotFoundException("Approve with ID " + approveId + " not found."));
    }

    //Delete approve
    public MessageResponse deleteApproveById(Long approveId){
        if (!approveRepository.existsById(approveId)) {
            throw new ApproveNotFoundException("Approve with ID " + approveId + " not found.");
        }
        approveRepository.deleteById(approveId);
        return new MessageResponse("Delete Successfully");
    }

    //Update Approve
    public MessageResponse updateApproveById(Long approveId, ApproveRequest approveRequest){
        Optional<Approve> optionalApprove = approveRepository.findById(approveId);
        if (optionalApprove.isPresent()) {
            Approve approve = optionalApprove.get();
            approve.setDoctorId(approveRequest.getDoctorId());
            approve.setAppointmentStatus(approveRequest.getAppointmentStatus());
            approve.setAppointmentType(approveRequest.getAppointmentType());
            approve.setAppointmentReason(approveRequest.getAppointmentReason());
            approve.setPatientId(approveRequest.getPatientId());
            approve.setDateField(approveRequest.getDateField());
            approve.setTimeField(approveRequest.getTimeField());

            approveRepository.save(approve); // Save the updated object back to the database

            return new MessageResponse("Approve Updated Successfully");
        } else {
            throw new ApproveNotFoundException("Approve with ID " + approveId + " not found.");
        }
    }

}
