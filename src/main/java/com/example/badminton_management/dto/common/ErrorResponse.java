package com.example.badminton_management.dto.common;

public class ErrorResponse {
    private int status;
    private String message;
    private Long timestamp;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
