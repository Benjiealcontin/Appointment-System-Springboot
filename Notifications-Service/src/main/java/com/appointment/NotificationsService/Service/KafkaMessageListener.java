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

            // Extract the email field from the JSON
            String email = jsonNode.get("email").asText();
            String transactionId = jsonNode.get("transactionId").asText();
            String appointmentReason = jsonNode.get("appointmentReason").asText();
            String appointmentType = jsonNode.get("appointmentType").asText();
            JsonNode dateFieldNode = jsonNode.get("dateField");
            LocalDate dateField = LocalDate.of(
                    dateFieldNode.get(0).asInt(),
                    dateFieldNode.get(1).asInt(),
                    dateFieldNode.get(2).asInt()
            );
            String timeField = jsonNode.get("timeField").asText();
            String doctorName = jsonNode.get("doctorName").asText();
            log.info("Consumer1 received the message with key=[{}] and email=[{}] from partition=[{}] with offset=[{}]",
                    key, email, partition, offset);

            Map<String, Object> appointmentModel = new HashMap<>();
            appointmentModel.put("doctorName",doctorName);
            appointmentModel.put("transactionId",transactionId);
            appointmentModel.put("appointmentReason",appointmentReason);
            appointmentModel.put("appointmentType",appointmentType);
            appointmentModel.put("dateField",dateField);
            appointmentModel.put("timeField",timeField);

            emailSenderService.sendAppointmentConfirmationEmail(email,appointmentModel);
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

            // Extract the email field from the JSON
            String email = jsonNode.get("email").asText();
            String transactionId = jsonNode.get("transactionId").asText();
            String appointmentReason = jsonNode.get("appointmentReason").asText();
            String appointmentType = jsonNode.get("appointmentType").asText();
            JsonNode dateFieldNode = jsonNode.get("dateField");
            LocalDate dateField = LocalDate.of(
                    dateFieldNode.get(0).asInt(),
                    dateFieldNode.get(1).asInt(),
                    dateFieldNode.get(2).asInt()
            );
            String timeField = jsonNode.get("timeField").asText();
            String doctorName = jsonNode.get("doctorName").asText();
            log.info("Consumer2 received the message with key=[{}] and email=[{}] from partition=[{}] with offset=[{}]",
                    key, email, partition, offset);

            Map<String, Object> appointmentModel = new HashMap<>();
            appointmentModel.put("doctorName",doctorName);
            appointmentModel.put("transactionId",transactionId);
            appointmentModel.put("appointmentReason",appointmentReason);
            appointmentModel.put("appointmentType",appointmentType);
            appointmentModel.put("dateField",dateField);
            appointmentModel.put("timeField",timeField);

            emailSenderService.sendAppointmentConfirmationEmail(email,appointmentModel);
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

            // Extract the email field from the JSON
            String email = jsonNode.get("email").asText();
            String transactionId = jsonNode.get("transactionId").asText();
            String appointmentReason = jsonNode.get("appointmentReason").asText();
            String appointmentType = jsonNode.get("appointmentType").asText();
            JsonNode dateFieldNode = jsonNode.get("dateField");
            LocalDate dateField = LocalDate.of(
                    dateFieldNode.get(0).asInt(),
                    dateFieldNode.get(1).asInt(),
                    dateFieldNode.get(2).asInt()
            );
            String timeField = jsonNode.get("timeField").asText();
            String doctorName = jsonNode.get("doctorName").asText();
            log.info("Consumer3 received the message with key=[{}] and email=[{}] from partition=[{}] with offset=[{}]",
                    key, email, partition, offset);

            Map<String, Object> appointmentModel = new HashMap<>();
            appointmentModel.put("doctorName",doctorName);
            appointmentModel.put("transactionId",transactionId);
            appointmentModel.put("appointmentReason",appointmentReason);
            appointmentModel.put("appointmentType",appointmentType);
            appointmentModel.put("dateField",dateField);
            appointmentModel.put("timeField",timeField);

            emailSenderService.sendAppointmentConfirmationEmail(email,appointmentModel);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}