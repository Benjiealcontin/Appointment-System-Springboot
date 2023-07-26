package com.appointment.NotificationsService.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class KafkaMessageListener {


    private final EmailSenderService emailSenderService;

    public KafkaMessageListener(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    Logger log = LoggerFactory.getLogger(KafkaMessageListener.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "appointment", groupId = "appointment-group")
    public void consume1(ConsumerRecord<String, String> record) {
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

            // Now you can use the email as needed
            emailSenderService.sendSimpleEmail(email, key,transactionId,appointmentReason,appointmentType
            ,dateField,timeField,doctorName);
        } catch (IOException e) {
            // Handle any JSON parsing errors here
            log.error("Error parsing JSON from Kafka message: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "appointment", groupId = "appointment-group")
    public void consume2(ConsumerRecord<String, String> record) {
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

            // Now you can use the email as needed
            emailSenderService.sendSimpleEmail(email, key,transactionId,appointmentReason,appointmentType
                    ,dateField,timeField,doctorName);
        } catch (IOException e) {
            // Handle any JSON parsing errors here
            log.error("Error parsing JSON from Kafka message: {}", e.getMessage());
        }
    }


    @KafkaListener(topics = "appointment", groupId = "appointment-group")
    public void consume3(ConsumerRecord<String, String> record) {
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

            // Now you can use the email as needed
            emailSenderService.sendSimpleEmail(email, key,transactionId,appointmentReason,appointmentType
                    ,dateField,timeField,doctorName);
        } catch (IOException e) {
            // Handle any JSON parsing errors here
            log.error("Error parsing JSON from Kafka message: {}", e.getMessage());
        }
    }
}
