package com.microservice.authservice.service;

import com.microservice.authservice.entity.User;
import com.microservice.authservice.repository.UserRepository;
import com.microservice.commonservice.dto.SignInRequestDTO;
import com.microservice.commonservice.dto.SignUpDTO;
import com.microservice.commonservice.exception.BadRequestServiceAlertException;
import com.microservice.commonservice.util.Constant;
import com.microservice.commonservice.util.UserCredentialValidation;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final JWTService jwtService;

    private final UserRepository userRepository;

    private final UserCredentialValidation userCredentialValidation;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserService(JWTService jwtService, UserRepository userRepository, UserCredentialValidation userCredentialValidation) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userCredentialValidation = userCredentialValidation;
    }


    @Transactional
    public Map<String, String> signIn(final SignInRequestDTO signInDTO) {
        if (!userCredentialValidation.isValidEmail(signInDTO.getEmail())) {
            throw new BadRequestServiceAlertException("Invalid Email format");
        }
        final User user = this.userRepository.findByEmail(signInDTO.getEmail()).orElseThrow(() -> new RuntimeException(Constant.INCORRECT_EMAIL));
        if (!encoder.matches(signInDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException(Constant.INCORRECT_PASSWORD);
        }
        final String jwt = jwtService.generateToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);
        final Map<String, String> jwtAuthResp = new HashMap<>();
        jwtAuthResp.put("token", jwt);
        jwtAuthResp.put("refreshToken", refreshToken);
        return jwtAuthResp;
    }

    public User customerCreate(final SignUpDTO signUpDTO) {
        if (!userCredentialValidation.isValidEmail(signUpDTO.getEmail())) {
            throw new BadRequestServiceAlertException("Invalid Email format");
        }
        if (userRepository.existsByEmail(signUpDTO.getEmail())) {
            throw new BadRequestServiceAlertException("EXIST_ACCOUNT");
        }
        final User user = new User();
        user.setName(signUpDTO.getName());
        user.setEmail(signUpDTO.getEmail());
        user.setPassword(encoder.encode(signUpDTO.getPassword()));
        user.setRole(signUpDTO.getRole());
        user.setCreatedBy(signUpDTO.getCreatedBy());

        return this.userRepository.save(user);
    }
}
