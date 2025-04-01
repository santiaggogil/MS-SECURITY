package com.sgm.ms_security.Controllers;

import com.sgm.ms_security.Models.OtpCode;
import com.sgm.ms_security.Services.OtpService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/otp")
@CrossOrigin
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/validate")
    public Map<String, String> validateOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        boolean isValid = otpService.validateOtp(email, code);

        if (isValid) {
            return Map.of("message", "OTP válido. Acceso concedido.");
        } else {
            return Map.of("message", "Código OTP incorrecto o expirado.");
        }
    }


    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody Map<String, String> request) {
        System.out.println("🔹 Recibida solicitud para generar OTP");  // 🔍 Verificar si el request llega aquí

        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("El email es obligatorio.");
        }

        String otp = otpService.generateOtp(email);
        System.out.println("✅ Código OTP generado para: " + email);

        return ResponseEntity.ok("Código OTP enviado a " + email);
    }


    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        System.out.println("Verificando OTP para: " + email + " con código: " + code);

        boolean isValid = otpService.validateOtp(email, code);
        if (isValid) {
            System.out.println("OTP Válido. Autenticación exitosa.");
            return ResponseEntity.ok("Autenticación exitosa");
        } else {
            System.out.println("OTP Inválido o expirado.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código OTP incorrecto o expirado");
        }
    }
}
