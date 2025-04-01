package com.sgm.ms_security.Repositories;

import com.sgm.ms_security.Models.OtpCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface OtpCodeRepository extends MongoRepository<OtpCode, String> {
    Optional<OtpCode> findTopByEmailOrderByExpirationTimeDesc(String email);
    void deleteByEmail(String email); //método para eliminar OTP previos
}
