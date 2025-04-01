package com.sgm.ms_security.Services;

import com.sgm.ms_security.Models.OtpCode;
import com.sgm.ms_security.Repositories.OtpCodeRepository;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {
    private final OtpCodeRepository otpCodeRepository;
    private final EmailService emailService;

    public OtpService(OtpCodeRepository otpCodeRepository, EmailService emailService) {
        this.otpCodeRepository = otpCodeRepository;
        this.emailService = emailService;
    }

    public String generateOtp(String email) {
        Optional<OtpCode> existingOtp = otpCodeRepository.findTopByEmailOrderByExpirationTimeDesc(email);

        // ⚠️ Evitar regeneración innecesaria
        if (existingOtp.isPresent() && existingOtp.get().getExpirationTime().isAfter(LocalDateTime.now())) {
            return existingOtp.get().getCode(); // ✅ Usar el código existente si no ha expirado
        }

        // Generar nuevo OTP
        String code = String.format("%06d", new Random().nextInt(999999));

        OtpCode otp = new OtpCode();
        otp.setEmail(email);
        otp.setCode(code);
        otp.setExpirationTime(LocalDateTime.now().plusMinutes(5));

        otpCodeRepository.save(otp);
        enviarCorreo(email, code);

        return code;
    }

    public boolean validateOtp(String email, String code) {
        Optional<OtpCode> otpOptional = otpCodeRepository.findTopByEmailOrderByExpirationTimeDesc(email);

        if (otpOptional.isPresent()) {
            OtpCode otp = otpOptional.get();

            // ⚠️ Evitar problemas de expiración
            if (otp.getExpirationTime().isAfter(LocalDateTime.now())) {
                otpCodeRepository.delete(otp); // ✅ Eliminar OTP después de usarlo
                return true;
            }
        }
        return false;
    }

    private void enviarCorreo(String destinatario, String codigoOtp) {
        try {
            emailService.sendOtpEmail(destinatario, codigoOtp);
        } catch (MessagingException e) {
            System.out.println("❌ Error al enviar el correo: " + e.getMessage());
        }
    }
}
