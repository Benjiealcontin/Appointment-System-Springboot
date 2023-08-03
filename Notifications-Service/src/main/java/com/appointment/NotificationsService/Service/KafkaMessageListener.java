package com.appointment.NotificationsService.Service;



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


    private final EmailSenderService emailSenderService;


    public KafkaMessageListener(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
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
            String toEmail = jsonNode.get("userTokenData").get("email").asText();
            String phoneNumber = jsonNode.get("userTokenData").get("phoneNumber").asText();

            Map<String, Object> appointmentModel = new HashMap<>();
            appointmentModel.put("doctorName",doctorName);
            appointmentModel.put("transactionId",transactionId);
            appointmentModel.put("appointmentReason",appointmentReason);
            appointmentModel.put("appointmentType",appointmentType);
            appointmentModel.put("dateField",dateField);
            appointmentModel.put("timeField",timeField);
            appointmentModel.put("fullName",fullName);
            appointmentModel.put("age",age);
            appointmentModel.put("email2",toEmail);
            appointmentModel.put("gender",gender);
            appointmentModel.put("phoneNumber",phoneNumber);

            emailSenderService.sendAppointmentConfirmationEmail(toEmail, appointmentModel);

            log.info("Consumer3 received the message with key=[{}]from partition=[{}] with offset=[{}]",
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
            String toEmail = jsonNode.get("userTokenData").get("email").asText();
            String phoneNumber = jsonNode.get("userTokenData").get("phoneNumber").asText();

            Map<String, Object> appointmentModel = new HashMap<>();
            appointmentModel.put("doctorName",doctorName);
            appointmentModel.put("transactionId",transactionId);
            appointmentModel.put("appointmentReason",appointmentReason);
            appointmentModel.put("appointmentType",appointmentType);
            appointmentModel.put("dateField",dateField);
            appointmentModel.put("timeField",timeField);
            appointmentModel.put("fullName",fullName);
            appointmentModel.put("age",age);
            appointmentModel.put("email2",toEmail);
            appointmentModel.put("gender",gender);
            appointmentModel.put("phoneNumber",phoneNumber);

            emailSenderService.sendAppointmentConfirmationEmail(toEmail, appointmentModel);

            log.info("Consumer2 received the message with key=[{}]from partition=[{}] with offset=[{}]",
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
            String toEmail = jsonNode.get("userTokenData").get("email").asText();
            String phoneNumber = jsonNode.get("userTokenData").get("phoneNumber").asText();

            Map<String, Object> appointmentModel = new HashMap<>();
            appointmentModel.put("doctorName",doctorName);
            appointmentModel.put("transactionId",transactionId);
            appointmentModel.put("appointmentReason",appointmentReason);
            appointmentModel.put("appointmentType",appointmentType);
            appointmentModel.put("dateField",dateField);
            appointmentModel.put("timeField",timeField);
            appointmentModel.put("fullName",fullName);
            appointmentModel.put("age",age);
            appointmentModel.put("email2",toEmail);
            appointmentModel.put("gender",gender);
            appointmentModel.put("phoneNumber",phoneNumber);

            emailSenderService.sendAppointmentConfirmationEmail(toEmail, appointmentModel);

            log.info("Consumer3 received the message with key=[{}]from partition=[{}] with offset=[{}]",
                    key, partition, offset);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}