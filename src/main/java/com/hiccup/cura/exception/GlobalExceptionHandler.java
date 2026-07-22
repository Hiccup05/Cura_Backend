package com.hiccup.cura.exception;

import com.hiccup.cura.exception.custom.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(KhaltiGatewayFailException.class)
    public ResponseEntity<ErrorResponse> handleKhaltiGatewayFailException(UnauthorizedUserAccessException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(502, "KhaltiGatewayFailException", ex.getMessage(), request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidBookingTimeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBookingTimeException(InvalidBookingTimeException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, "InvalidBookingTimeException", ex.getMessage(), request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(CancellationNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleCancellationNotAllowedException(CancellationNotAllowedException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, "Cancellation Not Allowed", ex.getMessage(), request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(UnauthorizedUserAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedUserAccessException(UnauthorizedUserAccessException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(403, "Unauthorized User Access", ex.getMessage(), request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, "Resource Not Found",
                        ex.getMessage(), request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEntry(DuplicateEntryException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, "Duplicate Entry", ex.getMessage(),
                        request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(PatientAccountDeactivatedException.class)
    public ResponseEntity<ErrorResponse> handlePatientAccountDeactivate(PatientAccountDeactivatedException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(403, "ACCOUNT_DEACTIVATED", ex.getMessage(),
                request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(StaffAccountDeactivatedException.class)
    public ResponseEntity<ErrorResponse> handleStaffDisable(StaffAccountDeactivatedException ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(403, "ACCOUNT_DISABLED", ex.getMessage(),
                request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidReactivationTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, "INVALID_TOKEN",
                        "Invalid reactivation link", request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(ReactivationTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpired(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.GONE)
                .body(new ErrorResponse(410, "TOKEN_EXPIRED",
                        "Your reactivation link has expired. Please request a new one.", request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(PaymentProviderNotSupported.class)
    public ResponseEntity<ErrorResponse> handlePaymentProviderNotSupported(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.GONE)
                .body(new ErrorResponse(404, "Payment_Provider_NOT_SUPPORTED",
                        ex.getMessage(), request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, "Validation Failed",
                        message, request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "Internal Server Error",
                        ex.getMessage(), request.getRequestURI(), LocalDateTime.now()));
    }

}
