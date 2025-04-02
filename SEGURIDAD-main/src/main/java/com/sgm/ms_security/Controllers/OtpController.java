package com.sgm.ms_security.Controllers;

import com.sgm.ms_security.Anserws.VerifyBody;
import com.sgm.ms_security.Anserws.VerifyResponse;
import com.sgm.ms_security.Models.OtpCode;
import com.sgm.ms_security.Models.User;
import com.sgm.ms_security.Repositories.UserRepository;
import com.sgm.ms_security.Services.JwtService;
import com.sgm.ms_security.Services.JwtTokenProvider;
import com.sgm.ms_security.Services.OtpService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sgm.ms_security.Anserws.VerifyResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/otp")
@CrossOrigin
public class OtpController {

    private final OtpService otpService;
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private JwtService theJwtService;
    @Autowired
    private UserRepository theUserRepository;

    public OtpController(OtpService otpService, JwtTokenProvider jwtTokenProvider, JwtService theJwtService) {
        this.otpService = otpService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.theJwtService = theJwtService;
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
    public ResponseEntity<VerifyResponse> verifyOtp(@RequestBody VerifyBody request) {
        String email = request.getEmail();
        String code = request.getOtp();

        // Validación de entrada
        if (email == null || code == null) {
            return ResponseEntity.badRequest().body(VerifyResponse.create(HttpStatus.BAD_REQUEST.value(), "Email o código OTP faltante", null));
        }

        System.out.println("Verificando OTP para: " + email + " con código: " + code);

        boolean isValid = otpService.validateOtp(email, code);

        if (isValid) {
            User theActualUser = this.theUserRepository.getUserByEmail(email);

            if (theActualUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(VerifyResponse.create(HttpStatus.NOT_FOUND.value(), "Usuario no encontrado", null));
            }

            // Generar JWT
            String token = theJwtService.generateToken(theActualUser);
            System.out.println("Token generado: " + token);

            // Aquí puedes guardar la sesión del usuario si es necesario
           // sessionService.createSession(theActualUser.getId(), token);

            System.out.println("OTP Válido. Autenticación exitosa.");
            return ResponseEntity.ok(VerifyResponse.create(HttpStatus.OK.value(), "VERIFICADO", token));
        } else {
            System.out.println("OTP Inválido o expirado.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(VerifyResponse.create(HttpStatus.UNAUTHORIZED.value(), "Código OTP incorrecto o expirado", null));
        }
    }

}
