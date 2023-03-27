package org.intraportal.api.exception;

import java.time.LocalDateTime;

public class ErrorResponse {

    private LocalDateTime timeStamp;
    private String status;
    private String error;
    private String path;
    private String message;

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ErrorResponse timestamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public ErrorResponse status(String status) {
        this.status = status;
        return this;
    }

    public ErrorResponse error(String error) {
        this.error = error;
        return this;
    }

    public ErrorResponse path(String path) {
        this.path = path;
        return this;
    }

    public ErrorResponse message(String message) {
        this.message = message;
        return this;
    }
}
