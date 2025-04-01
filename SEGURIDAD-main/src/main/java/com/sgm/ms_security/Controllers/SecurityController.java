package com.sgm.ms_security.Controllers;


import com.sgm.ms_security.Models.User;
import com.sgm.ms_security.Repositories.UserRepository;
import com.sgm.ms_security.Services.EncryptionService;
import com.sgm.ms_security.Services.JwtService;
import com.sgm.ms_security.Services.OtpService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/public/security")
public class SecurityController {
    @Autowired
    private UserRepository theUserRepository;
    @Autowired
    private EncryptionService theEncryptionService;
    @Autowired
    private JwtService theJwtService;

    @Autowired
    private OtpService otpService;  // Agregamos el servicio OTP

    @PostMapping("/login")
    public HashMap<String,Object> login(@RequestBody User theNewUser,
                                        final HttpServletResponse response)throws IOException {
        HashMap<String,Object> theResponse=new HashMap<>();
        String token="";
        User theActualUser=this.theUserRepository.getUserByEmail(theNewUser.getEmail());
        if(theActualUser!=null &&
           theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))){
            token=theJwtService.generateToken(theActualUser);
            theActualUser.setPassword("");
            theResponse.put("token",token);
            theResponse.put("user",theActualUser);
            return theResponse;
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return  theResponse;
        }
    }

    @PostMapping("/validate-otp")
    public Map<String, String> validateOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = otpService.validateOtp(email, otp);

        if (!isValid) {
            return Map.of("status", "error", "message", "Código OTP incorrecto o expirado");
        }

        // 🔹 Si el OTP es válido, generamos y enviamos el token JWT
        User user = theUserRepository.getUserByEmail(email);
        if (user == null) {
            return Map.of("status", "error", "message", "Usuario no encontrado");
        }

        String token = theJwtService.generateToken(user);
        return Map.of("status", "success", "message", "Autenticación completada", "token", token);
    }

}
