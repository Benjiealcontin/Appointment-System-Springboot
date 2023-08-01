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
public class EmailSenderService {
    private final JavaMailSender mailSender;

    @Autowired
    private Configuration config;

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /*
    TODO:
    - create function to approve
     */
    public void sendAppointmentConfirmationEmail(String toEmail, Map<String, Object> model) throws MessagingException, TemplateException, IOException {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Template t = config.getTemplate("email-template.ftl");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

        helper.setTo(toEmail);
        helper.setText(html, true);
        helper.setSubject("Appointment Notification");
//        helper.setFrom("alcontinebenezer07@gmail.com");
        mailSender.send(message);
        System.out.println("Mail Sent successfully");
    }
}