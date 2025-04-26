package com.vaibhav.booking.system.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(MfaRequiredException.class)
    public ResponseEntity<Object> handleMfaRequiredException(MfaRequiredException ex, WebRequest request) {
        return createErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        return createErrorResponse("Invalid credentials", HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return createErrorResponse("Access denied", HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        return createErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred", ex);
        return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<Object> createErrorResponse(String message, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(MovieAlreadyExistException.class)
    public ResponseEntity<Object> movieExistException(MovieAlreadyExistException ex,WebRequest request) {
        log.error("Cannot Insert Movie Data, {}", ex.getMessage());
        return createErrorResponse(ex.getMessage(), HttpStatus.CONFLICT,request);
    }

    @ExceptionHandler(TheaterAlreadyExistsException.class)
    public ResponseEntity<Object> theaterExistException(TheaterAlreadyExistsException ex,WebRequest request) {
        log.error("Cannot Insert Theater Data, {}", ex.getMessage());
        return createErrorResponse(ex.getMessage(), HttpStatus.CONFLICT,request);
    }

    @ExceptionHandler(LocationCountExceedsException.class)
    public ResponseEntity<Object> theaterLocationCountException(LocationCountExceedsException ex, WebRequest request) {
        log.error("Cannot Insert more Theater, {}", ex.getMessage());
        return createErrorResponse(ex.getMessage(), HttpStatus.CONFLICT,request);
    }
    @ExceptionHandler(BookingException.class)
    public ResponseEntity<Object> handleBookingException(BookingException ex,WebRequest request) {
        return createErrorResponse(ex.getMessage(), ex.getStatus(),request);
    }


}