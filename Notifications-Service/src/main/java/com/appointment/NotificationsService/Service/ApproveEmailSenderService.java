package com.appointment.NotificationsService.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class ApproveEmailSenderService {

    private final JavaMailSender mailSender;

    @Autowired
    private Configuration config;

    public ApproveEmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendApproveConfirmationEmail(String patientEmail, Map<String, Object> model) throws MessagingException, TemplateException, IOException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Template t = config.getTemplate("approveEmail-template.ftl");
        String approveHtml = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

        helper.setTo(patientEmail);
        helper.setText(approveHtml, true);
        helper.setSubject("Appointment Notification");
        helper.setFrom("benjiealcontin23@gmail.com");
        mailSender.send(message);
        System.out.println("Mail Sent for Patient is successfully");

    }
}
