package com.appointment.NotificationsService.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmailSenderService {
    private final JavaMailSender mailSender;

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /*
    TODO:
    - create button for approve
    - create button for not approve
    - design the email
     */
    public void sendAppointmentConfirmationEmail(String toEmail,
                                String key,
                                String transactionId,
                                String appointmentReason,
                                String appointmentType,
                                LocalDate dateField,
                                String timeField,
                                String doctorName) {

        String messageText = "<b>Appointment Information:</b>\n" +
                "Transaction UUID: " + transactionId + "\n" +
                "Patient ID: " + key + "\n" +
                "Doctor Name: " + doctorName + "\n" +
                "Appointment Reason: " + appointmentReason + "\n" +
                "Appointment Type: " + appointmentType + "\n" +
                "Date of Appointment: " + dateField + "\n" +
                "Time of Appointment: " + timeField;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("alcontinebenezer07@gmail.com");
        message.setTo(toEmail);
        message.setText(messageText);
        message.setSubject("Appointment Notification");

        mailSender.send(message);
        System.out.println("Mail Sent successfully");
    }
}