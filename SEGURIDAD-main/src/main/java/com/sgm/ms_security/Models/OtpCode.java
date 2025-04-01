package com.sgm.ms_security.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor  // 🔹 Constructor vacío
@AllArgsConstructor // 🔹 Constructor con todos los campos
@Document(collection = "otp_codes")
public class OtpCode {
    @Id
    private String id;
    private String email;
    private String code;
    private LocalDateTime expirationTime;

    // 🔹 Métodos Getters y Setters manuales si es necesario
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }
}
