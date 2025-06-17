package com.microservice.authservice.controller;

import com.microservice.authservice.entity.User;
import com.microservice.authservice.repository.UserRepository;
import com.microservice.authservice.service.MyUserDetailsService;
import com.microservice.authservice.service.UserService;
import com.microservice.commonservice.dto.ResponseDTO;
import com.microservice.commonservice.dto.SignInRequestDTO;
import com.microservice.commonservice.dto.SignUpDTO;
import com.microservice.commonservice.dto.UserDetailDTO;
import com.microservice.commonservice.util.Constant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    private final MyUserDetailsService userDetailsService;

    public UserController(final UserService userService,final UserRepository userRepository,final MyUserDetailsService userDetailsService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/auth/signIn")
    public ResponseDTO signIn(@RequestBody final SignInRequestDTO signInDTO) {
        return new ResponseDTO(HttpStatus.OK.value(), Constant.SIGN_IN, this.userService.signIn(signInDTO));
    }

    @PostMapping("/auth/customer")
    public ResponseDTO customerCreate(@RequestBody final SignUpDTO signUpDTO) {
        return new ResponseDTO(HttpStatus.OK.value(), Constant.CREATE, this.userService.customerCreate(signUpDTO));
    }

    @GetMapping("/auth/user")
    public ResponseEntity<UserDetailDTO> getUserByEmail(@RequestParam final String email) {
        final User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        final UserDetailDTO dto = new UserDetailDTO(user.getName(), user.getEmail(), user.getRole());
        return ResponseEntity.ok(dto);
    }
}
