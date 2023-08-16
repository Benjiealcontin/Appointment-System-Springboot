package com.appointment.NotificationsService.Listener;



import com.appointment.NotificationsService.Service.AppointmentEmailSenderService;
import com.appointment.NotificationsService.Service.ApproveEmailSenderService;
import com.appointment.NotificationsService.Service.CancelEmailSenderService;
import com.appointment.NotificationsService.Service.DisapproveEmailSenderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaMessageListener {


    private final AppointmentEmailSenderService appointmentEmailSenderService;

    private final ApproveEmailSenderService approveEmailSenderService;

    private final CancelEmailSenderService cancelEmailSenderService;

    private final DisapproveEmailSenderService disapproveEmailSenderService;


    public KafkaMessageListener(AppointmentEmailSenderService emailSenderService, ApproveEmailSenderService approveEmailSenderService, CancelEmailSenderService cancelEmailSenderService, DisapproveEmailSenderService disapproveEmailSenderService) {
        this.appointmentEmailSenderService = emailSenderService;
        this.approveEmailSenderService = approveEmailSenderService;
        this.cancelEmailSenderService = cancelEmailSenderService;
        this.disapproveEmailSenderService = disapproveEmailSenderService;
    }

    Logger log = LoggerFactory.getLogger(KafkaMessageListener.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "appointment", groupId = "appointment-group")
    public void consumeAppointment1(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        int partition = record.partition();
        long offset = record.offset();
        try {
            // Parse the JSON value
            JsonNode jsonNode = objectMapper.readTree(value);

            // Extract transactionId from the JSON
            String transactionId = jsonNode.get("appointmentData").get("transactionId").asText();
            String appointmentReason  = jsonNode.get("appointmentData").get("appointmentReason").asText();
            String appointmentType   = jsonNode.get("appointmentData").get("appointmentType").asText();
            String timeField   = jsonNode.get("appointmentData").get("timeField").asText();
            String doctorContactInfo = jsonNode.get("appointmentData").get("doctorEmail").asText();
            String doctorEmail = jsonNode.get("appointmentData").get("doctorEmail").asText();
            String doctorName    = jsonNode.get("appointmentData").get("doctorName").asText();
            JsonNode dateFieldNode = jsonNode.get("appointmentData").get("dateField");
            LocalDate dateField = LocalDate.of(
                    dateFieldNode.get(0).asInt(),
                    dateFieldNode.get(1).asInt(),
                    dateFieldNode.get(2).asInt()
            );

            // Extract Patient details from the JSON
            String fullName = jsonNode.get("userTokenData").get("fullName").asText();
            int age = Integer.parseInt(jsonNode.get("userTokenData").get("age").asText());
            String gender = jsonNode.get("userTokenData").get("gender").asText();
            String patientEmail = jsonNode.get("userTokenData").get("email").asText();
            String phoneNumber = jsonNode.get("userTokenData").get("phoneNumber").asText();
            String address = jsonNode.get("userTokenData").get("address").asText();

            Map<String, Object> appointmentModel = new HashMap<>();
            appointmentModel.put("doctorName",doctorName);
            appointmentModel.put("transactionId",transactionId);
            appointmentModel.put("appointmentReason",appointmentReason);
            appointmentModel.put("appointmentType",appointmentType);
            appointmentModel.put("dateField",dateField);
            appointmentModel.put("timeField",timeField);
            appointmentModel.put("fullName",fullName);
            appointmentModel.put("age",age);
            appointmentModel.put("patientEmail",patientEmail);
            appointmentModel.put("doctorEmail",doctorEmail);
            appointmentModel.put("gender",gender);
            appointmentModel.put("phoneNumber",phoneNumber);
            appointmentModel.put("address",address);

            appointmentEmailSenderService.sendAppointmentConfirmationEmail(patientEmail, doctorEmail, appointmentModel);

            log.info("Appointment Consumer1 received the message with key=[{}]from partition=[{}] with offset=[{}]",
                    key, partition, offset);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "appointment", groupId = "appointment-group")
    public void consumeAppointment2(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        int partition = record.partition();
        long offset = record.offset();
        try {
            // Parse the JSON value
            JsonNode jsonNode = objectMapper.readTree(value);

            // Extract transactionId from the JSON
            String transactionId = jsonNode.get("appointmentData").get("transactionId").asText();
            String appointmentReason  = jsonNode.get("appointmentData").get("appointmentReason").asText();
            String appointmentType   = jsonNode.get("appointmentData").get("appointmentType").asText();
            String timeField   = jsonNode.get("appointmentData").get("timeField").asText();
            String doctorContactInfo = jsonNode.get("appointmentData").get("doctorEmail").asText();
            String doctorEmail = jsonNode.get("appointmentData").get("doctorEmail").asText();
            String doctorName    = jsonNode.get("appointmentData").get("doctorName").asText();
            JsonNode dateFieldNode = jsonNode.get("appointmentData").get("dateField");
            LocalDate dateField = LocalDate.of(
                    dateFieldNode.get(0).asInt(),
                    dateFieldNode.get(1).asInt(),
                    dateFieldNode.get(2).asInt()
            );

            // Extract Patient details from the JSON
            String fullName = jsonNode.get("userTokenData").get("fullName").asText();
            int age = Integer.parseInt(jsonNode.get("userTokenData").get("age").asText());
            String gender = jsonNode.get("userTokenData").get("gender").asText();
            String patientEmail = jsonNode.get("userTokenData").get("email").asText();
            String phoneNumber = jsonNode.get("userTokenData").get("phoneNumber").asText();
            String address = jsonNode.get("userTokenData").get("address").asText();

            Map<String, Object> appointmentModel = new HashMap<>();
            appointmentModel.put("doctorName",doctorName);
            appointmentModel.put("transactionId",transactionId);
            appointmentModel.put("appointmentReason",appointmentReason);
            appointmentModel.put("appointmentType",appointmentType);
            appointmentModel.put("dateField",dateField);
            appointmentModel.put("timeField",timeField);
            appointmentModel.put("fullName",fullName);
            appointmentModel.put("age",age);
            appointmentModel.put("patientEmail",patientEmail);
            appointmentModel.put("doctorEmail",doctorEmail);
            appointmentModel.put("gender",gender);
            appointmentModel.put("phoneNumber",phoneNumber);
            appointmentModel.put("address",address);

            appointmentEmailSenderService.sendAppointmentConfirmationEmail(patientEmail, doctorEmail, appointmentModel);

            log.info("Appointment Consumer2 received the message with key=[{}]from partition=[{}] with offset=[{}]",
                    key, partition, offset);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "appointment", groupId = "appointment-group")
    public void consumeAppointment3(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        int partition = record.partition();
        long offset = record.offset();
        try {
            // Parse the JSON value
            JsonNode jsonNode = objectMapper.readTree(value);

            // Extract transactionId from the JSON
            String transactionId = jsonNode.get("appointmentData").get("transactionId").asText();
            String appointmentReason  = jsonNode.get("appointmentData").get("appointmentReason").asText();
            String appointmentType   = jsonNode.get("appointmentData").get("appointmentType").asText();
            String timeField   = jsonNode.get("appointmentData").get("timeField").asText();
            String doctorContactInfo = jsonNode.get("appointmentData").get("doctorEmail").asText();
            String doctorEmail = jsonNode.get("appointmentData").get("doctorEmail").asText();
//            doctorContactInfo = doctorContactInfo.replaceAll("[\\[\\]]", ""); // Removes both "[" and "]"
//            String[] emailArray = doctorContactInfo.split(", ");
//            String doctorEmail = emailArray[0];
            String doctorName    = jsonNode.get("appointmentData").get("doctorName").asText();
            JsonNode dateFieldNode = jsonNode.get("appointmentData").get("dateField");
            LocalDate dateField = LocalDate.of(
                    dateFieldNode.get(0).asInt(),
                    dateFieldNode.get(1).asInt(),
                    dateFieldNode.get(2).asInt()
            );

            // Extract Patient details from the JSON
            String fullName = jsonNode.get("userTokenData").get("fullName").asText();
            int age = Integer.parseInt(jsonNode.get("userTokenData").get("age").asText());
            String gender = jsonNode.get("userTokenData").get("gender").asText();
            String patientEmail = jsonNode.get("userTokenData").get("email").asText();
            String phoneNumber = jsonNode.get("userTokenData").get("phoneNumber").asText();
            String address = jsonNode.get("userTokenData").get("address").asText();

            Map<String, Object> appointmentModel = new HashMap<>();
            appointmentModel.put("doctorName",doctorName);
            appointmentModel.put("transactionId",transactionId);
            appointmentModel.put("appointmentReason",appointmentReason);
            appointmentModel.put("appointmentType",appointmentType);
            appointmentModel.put("dateField",dateField);
            appointmentModel.put("timeField",timeField);
            appointmentModel.put("fullName",fullName);
            appointmentModel.put("age",age);
            appointmentModel.put("patientEmail",patientEmail);
            appointmentModel.put("doctorEmail",doctorEmail);
            appointmentModel.put("gender",gender);
            appointmentModel.put("phoneNumber",phoneNumber);
            appointmentModel.put("address",address);

            appointmentEmailSenderService.sendAppointmentConfirmationEmail(patientEmail, doctorEmail, appointmentModel);

            log.info("Appointment Consumer3 received the message with key=[{}]from partition=[{}] with offset=[{}]",
                    key, partition, offset);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "approve", groupId = "approve-group")
    public void consumeApprove1(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        int partition = record.partition();
        long offset = record.offset();
        try {
            // Parse the JSON value
            JsonNode jsonNode = objectMapper.readTree(value);

            // Extract transactionId from the JSON
            String transactionId = jsonNode.get("transactionId").asText();
            String appointmentReason  = jsonNode.get("appointmentReason").asText();
            String appointmentType   = jsonNode.get("appointmentType").asText();
            String timeField   = jsonNode.get("timeField").asText();
            String location = jsonNode.get("location").asText();
            String doctorName    = jsonNode.get("doctorName").asText();
            String patientEmail = jsonNode.get("patientEmail").asText();
            JsonNode dateFieldNode = jsonNode.get("dateField");
            LocalDate dateField = LocalDate.of(
                    dateFieldNode.get(0).asInt(),
                    dateFieldNode.get(1).asInt(),
                    dateFieldNode.get(2).asInt()
            );


            Map<String, Object> approveModel = new HashMap<>();
            approveModel.put("doctorName",doctorName);
            approveModel.put("transactionId",transactionId);
            approveModel.put("appointmentReason",appointmentReason);
            approveModel.put("appointmentType",appointmentType);
            approveModel.put("location",location);
            approveModel.put("dateField",dateField);
            approveModel.put("timeField",timeField);

            approveEmailSenderService.sendApproveConfirmationEmail(patientEmail, approveModel);

            log.info("Approve Consumer3 received the message with key=[{}]from partition=[{}] with offset=[{}]",
                    key, partition, offset);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "approve", groupId = "approve-group")
    public void consumeApprove2(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        int partition = record.partition();
        long offset = record.offset();
        try {
            // Parse the JSON value
            JsonNode jsonNode = objectMapper.readTree(value);

            // Extract transactionId from the JSON
            String transactionId = jsonNode.get("transactionId").asText();
            String appointmentReason  = jsonNode.get("appointmentReason").asText();
            String appointmentType   = jsonNode.get("appointmentType").asText();
            String timeField   = jsonNode.get("timeField").asText();
            String location = jsonNode.get("location").asText();
            String doctorName    = jsonNode.get("doctorName").asText();
            String patientEmail = jsonNode.get("patientEmail").asText();
            JsonNode dateFieldNode = jsonNode.get("dateField");
            LocalDate dateField = LocalDate.of(
                    dateFieldNode.get(0).asInt(),
                    dateFieldNode.get(1).asInt(),
                    dateFieldNode.get(2).asInt()
            );


            Map<String, Object> approveModel = new HashMap<>();
            approveModel.put("doctorName",doctorName);
            approveModel.put("transactionId",transactionId);
            approveModel.put("appointmentReason",appointmentReason);
            approveModel.put("appointmentType",appointmentType);
            approveModel.put("location",location);
            approveModel.put("dateField",dateField);
            approveModel.put("timeField",timeField);

            approveEmailSenderService.sendApproveConfirmationEmail(patientEmail, approveModel);

            log.info("Approve Consumer3 received the message with key=[{}]from partition=[{}] with offset=[{}]",
                    key, partition, offset);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "approve", groupId = "approve-group")
    public void consumeApprove3(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        int partition = record.partition();
        long offset = record.offset();
        try {
            // Parse the JSON value
            JsonNode jsonNode = objectMapper.readTree(value);

            // Extract transactionId from the JSON
            String transactionId = jsonNode.get("transactionId").asText();
            String appointmentReason  = jsonNode.get("appointmentReason").asText();
            String appointmentType   = jsonNode.get("appointmentType").asText();
            String timeField   = jsonNode.get("timeField").asText();
            String location = jsonNode.get("location").asText();
            String doctorName    = jsonNode.get("doctorName").asText();
            String patientEmail = jsonNode.get("patientEmail").asText();
            JsonNode dateFieldNode = jsonNode.get("dateField");
            LocalDate dateField = LocalDate.of(
                    dateFieldNode.get(0).asInt(),
                    dateFieldNode.get(1).asInt(),
                    dateFieldNode.get(2).asInt()
            );


            Map<String, Object> approveModel = new HashMap<>();
            approveModel.put("doctorName",doctorName);
            approveModel.put("transactionId",transactionId);
            approveModel.put("appointmentReason",appointmentReason);
            approveModel.put("appointmentType",appointmentType);
            approveModel.put("location",location);
            approveModel.put("dateField",dateField);
            approveModel.put("timeField",timeField);

            approveEmailSenderService.sendApproveConfirmationEmail(patientEmail, approveModel);

            log.info("Approve Consumer3 received the message with key=[{}]from partition=[{}] with offset=[{}]",
                    key, partition, offset);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "disapprove", groupId = "disapprove-group")
    public void consumeDisapprove1(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        int partition = record.partition();
        long offset = record.offset();
        try {
            // Parse the JSON value
            JsonNode jsonNode = objectMapper.readTree(value);

            // Extract transactionId from the JSON
            String transactionId = jsonNode.get("transactionId").asText();
            String disapproveReason  = jsonNode.get("disapproveReason").asText();
            String patientEmail = jsonNode.get("patientEmail").asText();

            Map<String, Object> disapproveModel = new HashMap<>();
            disapproveModel.put("disapproveReason",disapproveReason);
            disapproveModel.put("transactionId",transactionId);


            disapproveEmailSenderService.sendDisapproveConfirmationEmail(patientEmail, disapproveModel);

            log.info("Disapprove Consumer1 received the message with key=[{}]from partition=[{}] with offset=[{}]",
                    key, partition, offset);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "disapprove", groupId = "disapprove-group")
    public void consumeDisapprove2(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        int partition = record.partition();
        long offset = record.offset();
        try {
            // Parse the JSON value
            JsonNode jsonNode = objectMapper.readTree(value);

            // Extract transactionId from the JSON
            String transactionId = jsonNode.get("transactionId").asText();
            String disapproveReason  = jsonNode.get("disapproveReason").asText();
            String patientEmail = jsonNode.get("patientEmail").asText();

            Map<String, Object> disapproveModel = new HashMap<>();
            disapproveModel.put("disapproveReason",disapproveReason);
            disapproveModel.put("transactionId",transactionId);


            disapproveEmailSenderService.sendDisapproveConfirmationEmail(patientEmail, disapproveModel);

            log.info("Disapprove Consumer2 received the message with key=[{}]from partition=[{}] with offset=[{}]",
                    key, partition, offset);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "disapprove", groupId = "disapprove-group")
    public void consumeDisapprove3(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        int partition = record.partition();
        long offset = record.offset();
        try {
            // Parse the JSON value
            JsonNode jsonNode = objectMapper.readTree(value);

            // Extract transactionId from the JSON
            String transactionId = jsonNode.get("transactionId").asText();
            String disapproveReason  = jsonNode.get("disapproveReason").asText();
            String patientEmail = jsonNode.get("patientEmail").asText();

            Map<String, Object> disapproveModel = new HashMap<>();
            disapproveModel.put("disapproveReason",disapproveReason);
            disapproveModel.put("transactionId",transactionId);


            disapproveEmailSenderService.sendDisapproveConfirmationEmail(patientEmail, disapproveModel);

            log.info("Disapprove Consumer3 received the message with key=[{}]from partition=[{}] with offset=[{}]",
                    key, partition, offset);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "cancel", groupId = "cancel-group")
    public void consumeCancel1(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        int partition = record.partition();
        long offset = record.offset();
        try {
            // Parse the JSON value
            JsonNode jsonNode = objectMapper.readTree(value);

            // Extract transactionId from the JSON
            String transactionId = jsonNode.get("transactionId").asText();
            String cancelReason  = jsonNode.get("cancelReason").asText();
            String doctorEmail = jsonNode.get("doctorEmail").asText();
            String patientEmail = jsonNode.get("patientEmail").asText();

            Map<String, Object> cancelModel = new HashMap<>();
            cancelModel.put("cancelReason",cancelReason);
            cancelModel.put("transactionId",transactionId);


            cancelEmailSenderService.sendCancelConfirmationEmail(doctorEmail, patientEmail, cancelModel);

            log.info("Cancel Consumer1 received the message with key=[{}]from partition=[{}] with offset=[{}]",
                    key, partition, offset);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "cancel", groupId = "cancel-group")
    public void consumeCancel2(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        int partition = record.partition();
        long offset = record.offset();
        try {
            // Parse the JSON value
            JsonNode jsonNode = objectMapper.readTree(value);

            // Extract transactionId from the JSON
            String transactionId = jsonNode.get("transactionId").asText();
            String cancelReason  = jsonNode.get("cancelReason").asText();
            String doctorEmail = jsonNode.get("doctorEmail").asText();
            String patientEmail = jsonNode.get("patientEmail").asText();

            Map<String, Object> cancelModel = new HashMap<>();
            cancelModel.put("cancelReason",cancelReason);
            cancelModel.put("transactionId",transactionId);


            cancelEmailSenderService.sendCancelConfirmationEmail(doctorEmail, patientEmail, cancelModel);

            log.info("Cancel Consumer2 received the message with key=[{}]from partition=[{}] with offset=[{}]",
                    key, partition, offset);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "cancel", groupId = "cancel-group")
    public void consumeCancel3(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        int partition = record.partition();
        long offset = record.offset();
        try {
            // Parse the JSON value
            JsonNode jsonNode = objectMapper.readTree(value);

            // Extract transactionId from the JSON
            String transactionId = jsonNode.get("transactionId").asText();
            String cancelReason  = jsonNode.get("cancelReason").asText();
            String doctorEmail = jsonNode.get("doctorEmail").asText();
            String patientEmail = jsonNode.get("patientEmail").asText();

            Map<String, Object> cancelModel = new HashMap<>();
            cancelModel.put("cancelReason",cancelReason);
            cancelModel.put("transactionId",transactionId);


            cancelEmailSenderService.sendCancelConfirmationEmail(doctorEmail, patientEmail, cancelModel);

            log.info("Cancel Consumer3 received the message with key=[{}]from partition=[{}] with offset=[{}]",
                    key, partition, offset);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}