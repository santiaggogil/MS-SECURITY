
package com.sgm.ms_security.Controllers;

import com.sgm.ms_security.Services.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/email")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    //Enviar cualquier email
    @PostMapping("/send")
    public Map<String, String> sendEmail(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String subject = request.get("subject");
        String text = request.get("text");

        Map<String, String> response = new HashMap<>();
        try {
            emailService.sendEmail(to, subject, text);
            response.put("message", "Correo enviado con éxito a " + to);
        } catch (MessagingException e) {
            response.put("error", "Error al enviar el correo: " + e.getMessage());
        }
        return response;
    }

    //Enviar OTP
    @PostMapping("/sendOtp")
    public Map<String, String> sendOtp(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String otp = request.get("otp");

        Map<String, String> response = new HashMap<>();
        try {
            emailService.sendOtpEmail(to, otp);
            response.put("message", "OTP enviado con éxito a " + to);
        } catch (MessagingException e) {
            response.put("error", "Error al enviar el OTP: " + e.getMessage());
        }
        return response;
    }
}
