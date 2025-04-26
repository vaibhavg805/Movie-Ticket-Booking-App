package com.vaibhav.booking.system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class MfaRequiredException extends RuntimeException {
    public MfaRequiredException(String message) {
        super(message);
    }
}