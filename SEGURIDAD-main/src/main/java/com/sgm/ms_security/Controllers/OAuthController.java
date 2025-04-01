package com.sgm.ms_security.Controllers;

import com.sgm.ms_security.Services.EmailService;
import com.sgm.ms_security.Services.JwtService;
import com.sgm.ms_security.Services.OtpService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
public class OAuthController {
    private final OtpService otpService;
    private final JwtService jwtService;
    private final EmailService emailService;

    public OAuthController(JwtService jwtService, EmailService emailService, OtpService otpService) {
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.otpService = otpService;
    }

    @GetMapping("/bienvenido")
    @ResponseBody
    public String bienvenida() {
        return "<h1>Bienvenido a Gestión de Servicios de Transporte de Carga de Productos</h1>";
    }

    @GetMapping("/error")
    @ResponseBody
    public String error() {
        return "<h1>Error: Código OTP inválido o expirado.</h1>";
    }


    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String provider = (String) request.getSession().getAttribute("oauth_provider");
        request.getSession().invalidate(); // Ahora se invalida después de obtener el proveedor

        String redirectUrl = switch (provider) {
            case "google" -> "https://accounts.google.com/logout?continue=http://localhost:8080/login";
            case "microsoft" -> "https://login.microsoftonline.com/common/oauth2/v2.0/logout?post_logout_redirect_uri=http://localhost:8080/login";
            default -> "/login";
        };
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/token")
    public Map<String, Object> getToken(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> attributes = new HashMap<>(principal.getAttributes());

        String email = (String) attributes.get("email");
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo obtener el email del usuario.");
        }

        // Generar OTP y enviarlo
        String otp = otpService.generateOtp(email);
        try {
            emailService.sendEmail(email, "Código de Autenticación", "Tu código de autenticación es: " + otp);
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al enviar correo.");
        }
        return attributes;
    }

    @PostMapping("/test-otp")
    public ResponseEntity<String> testOtp(@RequestParam String email) {
        String otp = otpService.generateOtp(email);
        return ResponseEntity.ok("Código OTP enviado a " + email);
    }

}
