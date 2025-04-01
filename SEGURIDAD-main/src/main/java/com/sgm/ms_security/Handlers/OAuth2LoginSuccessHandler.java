package com.sgm.ms_security.Handlers;

import com.sgm.ms_security.Repositories.UserRepository;
import com.sgm.ms_security.Services.EmailService;
import com.sgm.ms_security.Services.OtpService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OtpService otpService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(OtpService otpService, EmailService emailService, UserRepository userRepository) {
        this.otpService = otpService;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        //Si viene desde el navegador (Google/Microsoft), autenticación directa SIN OTP
        if (!request.getHeader("User-Agent").contains("Postman")) {
            response.sendRedirect("/bienvenido");
            return;
        }

        //Si viene de Postman, genera y envía OTP
        String otpCode = otpService.generateOtp(email);
        try {
            emailService.sendEmail(email, "Tu código OTP", "Tu código de verificación es: " + otpCode);
            System.out.println("OTP enviado a: " + email);
        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando el correo: " + e.getMessage());
        }

        response.sendRedirect("/otp/pending?email=" + email);
    }
}
