package com.ynov.crud.logging;

public record LogRequest(String message, String source, String timestamp, String level) {
}
