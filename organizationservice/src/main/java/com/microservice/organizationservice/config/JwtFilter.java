package com.microservice.organizationservice.config;

import cn.hutool.core.util.ObjectUtil;
import com.microservice.commonservice.dto.UserDetailDTO;
import com.microservice.commonservice.util.Constant;
import com.microservice.organizationservice.service.JWTService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final WebClient webClient;
    private final HandlerExceptionResolver resolver;

    public JwtFilter(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
            JWTService jwtService,
            WebClient webClient
    ) {
        this.resolver = resolver;
        this.jwtService = jwtService;
        this.webClient = webClient;
    }

    private UserDetails fetchUserDetailsFromAuth(String email) {
        UserDetailDTO dto = this.webClient.get()
                .uri("api/v1/auth/user?email=" + email)
                .retrieve()
                .bodyToMono(UserDetailDTO.class)
                .block();

        if (dto == null) return null;

        return new User(
                dto.getEmail(),
                "", // password is not needed here
                Collections.singletonList(new SimpleGrantedAuthority(dto.getRole()))
        );
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                email = this.jwtService.extractUserName(token);
            }

            if (!ObjectUtil.isEmpty(email) && SecurityContextHolder.getContext().getAuthentication() == null) {
               final UserDetails userDetails = fetchUserDetailsFromAuth(email);

                if (userDetails != null && this.jwtService.validateToken(token, userDetails)) {
                    final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (ExpiredJwtException e) {
            this.resolver.resolveException(request, response, null, new SecurityException(Constant.TOKEN_EXPIRED));
            return;
        } catch (Exception e) {
            this.resolver.resolveException(request, response, null, new SignatureException(Constant.INVALID_SIGNATURE));
            return;
        }

        filterChain.doFilter(request, response);
    }
}


