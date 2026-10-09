package com.ynov.crud.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;

/**
 * Envoie un log à l'API Logs après chaque appel à l'API CRUD :
 * INFO pour une réponse 2xx/3xx, WARN pour une 4xx, ERR pour une 5xx.
 */
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private final LogClient logClient;

    public LoggingInterceptor(LogClient logClient) {
        this.logClient = logClient;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        int status = response.getStatus();

        String level;
        if (status >= 500) {
            level = "ERR";
        } else if (status >= 400) {
            level = "WARN";
        } else {
            level = "INFO";
        }

        Object errorMessage = request.getAttribute(ApiExceptionHandler.ERROR_MESSAGE_ATTRIBUTE);
        Object errorTimestamp = request.getAttribute(ApiExceptionHandler.ERROR_TIMESTAMP_ATTRIBUTE);

        String message = errorMessage != null ? errorMessage.toString() : "HTTP " + status;
        String timestamp = errorTimestamp != null ? errorTimestamp.toString() : Instant.now().toString();
        String source = "[CrudAPI] " + request.getMethod() + " " + request.getRequestURI();

        logClient.send(new LogRequest(message, source, timestamp, level));
    }
}
