package org.intraportal.persistence.domain.audit;


import org.intraportal.persistence.model.audit.AuditSessionAction;

import java.time.LocalDateTime;
import java.util.Objects;

public class SessionLogDto {

    private Integer id;

    private String username;

    private AuditSessionAction action;

    private String sessionId;

    private LocalDateTime createdDate;

    public SessionLogDto() {
    }

    public SessionLogDto(Integer id, String username, AuditSessionAction action, String sessionId, LocalDateTime createdDate) {
        this.id = id;
        this.username = username;
        this.action = action;
        this.sessionId = sessionId;
        this.createdDate = createdDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AuditSessionAction getAction() {
        return action;
    }

    public void setAction(AuditSessionAction action) {
        this.action = action;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionLogDto that = (SessionLogDto) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && action == that.action && Objects.equals(sessionId, that.sessionId) && Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, action, sessionId, createdDate);
    }

    @Override
    public String toString() {
        return "SessionLogDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", action=" + action +
                ", sessionId='" + sessionId + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }

}
