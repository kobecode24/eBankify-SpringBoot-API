package com.example.demo.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(InsufficientFundsException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Validation failed");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred"));
    }




        @ExceptionHandler(EntityNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }


        @ExceptionHandler(ConstraintViolationException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getMessage())
                .findFirst()
                .orElse("Validation failed");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

        @ExceptionHandler(IllegalArgumentException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

        @ExceptionHandler(IllegalStateException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }


        @ExceptionHandler(DataIntegrityViolationException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = "Data integrity violation";

        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("uk_users_email")) {
                message = "Email already registered";
            } else if (ex.getMessage().contains("unique constraint") ||
                    ex.getMessage().contains("Unique index")) {
                message = "Duplicate value not allowed";
            }
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid value for parameter '%s'", ex.getName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }



        @ExceptionHandler(InvalidEmailFormatException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorResponse> handleInvalidEmailFormatException(InvalidEmailFormatException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

        @ExceptionHandler(DuplicateEmailException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

        @ExceptionHandler(WeakPasswordException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorResponse> handleWeakPasswordException(WeakPasswordException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

        @ExceptionHandler(UnderageUserException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorResponse> handleUnderageUserException(UnderageUserException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        String message = String.format("Missing required parameter '%s'", ex.getParameterName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

        @ExceptionHandler(NullPointerException.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex) {
        log.error("Null pointer exception occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error processing loan eligibility. Please try again or contact support."));
    }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = "Malformed or invalid JSON request";

        Throwable cause = ex.getCause();
        if (cause instanceof com.fasterxml.jackson.databind.exc.MismatchedInputException) {
            message = "Invalid input format: " + cause.getMessage();
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

        @ExceptionHandler(AuthorizationDeniedException.class)
        @ResponseStatus(HttpStatus.FORBIDDEN)
        public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("Access Denied: " + ex.getMessage()));
    }

    @ExceptionHandler(io.jsonwebtoken.MalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleMalformedJwtException(io.jsonwebtoken.MalformedJwtException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Invalid JWT format: " + ex.getMessage()));
    }

    @ExceptionHandler(io.jsonwebtoken.ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(io.jsonwebtoken.ExpiredJwtException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("JWT has expired: " + ex.getMessage()));
    }

    @ExceptionHandler(io.jsonwebtoken.security.SignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleSignatureException(io.jsonwebtoken.security.SignatureException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Invalid JWT signature: " + ex.getMessage()));
    }


    @ExceptionHandler(io.jsonwebtoken.UnsupportedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleUnsupportedJwtException(io.jsonwebtoken.UnsupportedJwtException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Unsupported JWT: " + ex.getMessage()));
    }



    @ExceptionHandler(io.jsonwebtoken.JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleJwtException(io.jsonwebtoken.JwtException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("JWT processing error: " + ex.getMessage()));
    }




    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }
}
