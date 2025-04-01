package com.sgm.ms_security.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String to, String otpCode) throws MessagingException {
        String subject = "Tu código OTP";
        String text = "<h3>Tu código OTP es: <b>" + otpCode + "</b></h3>"
                + "<p>Este código expira en 5 minutos. No lo compartas con nadie.</p>";

        sendEmail(to, subject, text);
    }

    public void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        System.out.println("📨 Enviando correo a: " + to);
        System.out.println("📬 Asunto: " + subject);
        System.out.println("📄 Cuerpo: " + text);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.setFrom("tu_correo@gmail.com");

        mailSender.send(message);
    }
}

