package com.ynov.crud.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class LogClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogClient.class);

    private final RestTemplate restTemplate;
    private final String logsApiUrl;

    public LogClient(RestTemplate restTemplate, @Value("${logs.api.url}") String logsApiUrl) {
        this.restTemplate = restTemplate;
        this.logsApiUrl = logsApiUrl;
    }

    public void send(LogRequest log) {
        try {
            restTemplate.postForEntity(logsApiUrl + "/api/v1/logs", log, Void.class);
        } catch (RestClientException e) {
            // L'API CRUD doit continuer à fonctionner même si l'API de logs est indisponible
            LOGGER.warn("Impossible d'envoyer le log à l'API Logs : {}", e.getMessage());
        }
    }
}
