package com.microservice.authservice.config;

import com.microservice.authservice.service.JWTService;
import com.microservice.authservice.service.MyUserDetailsService;
import com.microservice.commonservice.util.Constant;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component("authJwtFilter")
public class JwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final ApplicationContext applicationContext;
    private final HandlerExceptionResolver resolver;
    private final MyUserDetailsService userDetailsService;

    public JwtFilter(
            @Qualifier("authJwtService") JWTService jwtService,
            ApplicationContext applicationContext,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
            MyUserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.applicationContext = applicationContext;
        this.resolver = resolver;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        try {

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                email = jwtService.extractUserName(token);
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                final UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.validateToken(token, userDetails)) {
                    System.out.println(userDetails);
                    final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            resolver.resolveException(request, response, null, new SecurityException(Constant.TOKEN_EXPIRED));
        } catch (Exception e) {
            e.printStackTrace();
            resolver.resolveException(request, response, null, new SignatureException(Constant.INVALID_SIGNATURE));
        }
        filterChain.doFilter(request, response);
    }
}

