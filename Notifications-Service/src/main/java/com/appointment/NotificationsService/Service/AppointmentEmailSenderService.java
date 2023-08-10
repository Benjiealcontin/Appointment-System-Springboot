package com.appointment.NotificationsService.Service;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Slf4j
public class AppointmentEmailSenderService {
    private final JavaMailSender mailSender;

    @Autowired
    private Configuration config;

    public AppointmentEmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAppointmentConfirmationEmail(String patientEmail, String doctorEmail, Map<String, Object> model) throws MessagingException, TemplateException, IOException {

        MimeMessage message1 = mailSender.createMimeMessage();
        MimeMessageHelper helper1 = new MimeMessageHelper(message1, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Template t1 = config.getTemplate("doctorEmail-template.ftl");
        String doctorHtml = FreeMarkerTemplateUtils.processTemplateIntoString(t1, model);

        helper1.setTo(doctorEmail);
        helper1.setText(doctorHtml, true);
        helper1.setSubject("Appointment Notification");
        helper1.setFrom("benjiealcontin23@gmail.com");
        mailSender.send(message1);
        System.out.println("Mail Sent for Doctor is successfully");

        MimeMessage message2 = mailSender.createMimeMessage();
        MimeMessageHelper helper2 = new MimeMessageHelper(message2, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Template t2 = config.getTemplate("patientEmail-template.ftl");
        String patientHtml = FreeMarkerTemplateUtils.processTemplateIntoString(t2, model);

        helper2.setTo(patientEmail);
        helper2.setText(patientHtml, true);
        helper2.setSubject("Appointment Notification");
        helper2.setFrom("benjiealcontin23@gmail.com");
        mailSender.send(message2);
        System.out.println("Mail Sent for Patient is successfully");
    }
}