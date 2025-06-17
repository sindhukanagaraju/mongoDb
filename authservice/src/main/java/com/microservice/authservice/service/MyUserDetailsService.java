package com.microservice.authservice.service;

import com.microservice.authservice.entity.User;
import com.microservice.authservice.repository.UserRepository;
import com.microservice.commonservice.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(Constant.USER_NOT_FOUND);
        }
        return user;
    }
}
