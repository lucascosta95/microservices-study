package br.com.lucascosta.authserviceapi.controllers.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import models.exceptions.RefreshTokenExpired;
import models.exceptions.StandardError;
import models.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({BadCredentialsException.class, RefreshTokenExpired.class})
    ResponseEntity<StandardError> handleBadCredentialsException(final RuntimeException ex, final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(StandardError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ValidationException> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex, final HttpServletRequest request) {
        var error = ValidationException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Exception")
                .message("Exception in validation attributes")
                .path(request.getRequestURI())
                .errors(new ArrayList<>())
                .build();

        ex.getBindingResult().getFieldErrors().forEach(e -> error.addError(e.getField(), e.getDefaultMessage()));

        return ResponseEntity.badRequest().body(error);
    }
}
