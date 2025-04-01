package com.sgm.ms_security.Configurations;

import com.sgm.ms_security.Models.User;
import com.sgm.ms_security.Repositories.UserRepository;
import com.sgm.ms_security.Services.JwtService;
import com.sgm.ms_security.Services.OtpService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final OtpService otpService;

    public CustomOAuth2UserService(JwtService jwtService, UserRepository userRepository, OtpService otpService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.otpService = otpService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = (String) oAuth2User.getAttributes().get("email");

        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            user = new User((String) oAuth2User.getAttributes().get("name"), email, null);
            userRepository.save(user);
        }

        // Enviar OTP
        otpService.generateOtp(email);

        Map<String, Object> updatedAttributes = new HashMap<>(oAuth2User.getAttributes());
        updatedAttributes.put("requires_otp", true);

        return new CustomOAuth2User(oAuth2User, updatedAttributes);
    }
}
