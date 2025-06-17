package com.microservice.commonservice.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UserCredentialValidation {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public boolean isValidEmail(final String email) {
        return email != null && Pattern.matches(this.EMAIL_REGEX, email.trim());
    }
}

