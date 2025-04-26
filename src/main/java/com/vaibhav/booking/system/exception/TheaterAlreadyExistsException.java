package com.vaibhav.booking.system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TheaterAlreadyExistsException extends RuntimeException{
    public TheaterAlreadyExistsException(String message){
        super(message);
    }
}
