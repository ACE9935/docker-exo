package com.ynov.crud.logging;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;

/**
 * Transforme les exceptions en réponses HTTP et mémorise le message d'erreur
 * et l'instant où il a été levé, pour que LoggingInterceptor l'envoie à l'API Logs.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    public static final String ERROR_MESSAGE_ATTRIBUTE = "crud.errorMessage";
    public static final String ERROR_TIMESTAMP_ATTRIBUTE = "crud.errorTimestamp";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handle(Exception ex, HttpServletRequest request) {
        HttpStatusCode status;
        if (ex instanceof ErrorResponse errorResponse) {
            status = errorResponse.getStatusCode();
        } else if (ex instanceof HttpMessageNotReadableException || ex instanceof MethodArgumentTypeMismatchException) {
            status = HttpStatus.BAD_REQUEST;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String message = ex instanceof ResponseStatusException rse && rse.getReason() != null
                ? rse.getReason()
                : String.valueOf(ex.getMessage());

        request.setAttribute(ERROR_MESSAGE_ATTRIBUTE, message);
        request.setAttribute(ERROR_TIMESTAMP_ATTRIBUTE, Instant.now().toString());

        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}
