package com.hiccup.cura.exception;

import com.hiccup.cura.exception.custom.CancellationNotAllowedException;
import com.hiccup.cura.exception.custom.DuplicateEntryException;
import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.exception.custom.UnauthorizedUserAccessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CancellationNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleCancellationNotAllowedException(UnauthorizedUserAccessException ex, HttpServletRequest request) {
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "Internal Server Error",
                        ex.getMessage(), request.getRequestURI(), LocalDateTime.now()));
    }

}
