package org.intraportal.api.service.jobs;

import java.time.LocalDateTime;

public class BaseJobManagerResponse {

    private final String jobId;
    private String message;
    private int progress;
    private String url;
    private Boolean failed;
    private LocalDateTime timeSubmitted;
    private LocalDateTime timeFinished;

    public BaseJobManagerResponse(String jobId, String message, int progress, String url, LocalDateTime timeSubmitted) {
        this.jobId = jobId;
        this.message = message;
        this.progress = progress;
        this.url = url;
        this.failed = Boolean.FALSE;
        this.timeSubmitted = timeSubmitted;
    }

    public BaseJobManagerResponse(String jobId, String message, int progress, String url, Boolean failed, LocalDateTime timeFinished) {
        this.jobId = jobId;
        this.message = message;
        this.progress = progress;
        this.url = url;
        this.failed = failed;
        this.timeFinished = timeFinished;
    }

    public String getJobId() {
        return jobId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean isFailed() {
        return failed;
    }

    public void setFailed(Boolean failed) {
        this.failed = failed;
    }

    public LocalDateTime getTimeSubmitted() {
        return timeSubmitted;
    }

    public void setTimeSubmitted(LocalDateTime timeSubmitted) {
        this.timeSubmitted = timeSubmitted;
    }

    public LocalDateTime getTimeFinished() {
        return timeFinished;
    }

    public void setTimeFinished(LocalDateTime timeFinished) {
        this.timeFinished = timeFinished;
    }

    @Override
    public String toString() {
        return "jobId='" + jobId + '\'' +
                ", message='" + message + '\'' +
                ", progress=" + progress +
                ", url='" + url + '\'' +
                ", timeSubmitted='" + timeSubmitted + '\'' +
                ", timeFinished='" + timeFinished + '\'' +
                ", failed=" + failed;
    }
}
