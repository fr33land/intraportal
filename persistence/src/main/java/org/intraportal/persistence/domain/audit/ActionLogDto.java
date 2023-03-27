package org.intraportal.persistence.domain.audit;

import org.intraportal.persistence.model.audit.AuditAction;
import org.intraportal.persistence.model.audit.AuditDomain;

import java.time.LocalDateTime;
import java.util.Objects;

public class ActionLogDto {

    private Integer id;

    private String actor;

    private AuditDomain domain;

    private AuditAction action;

    private String sessionId;

    private LocalDateTime createdDate;

    public ActionLogDto() {
    }

    public ActionLogDto(Integer id, String actor, AuditDomain domain, AuditAction action, String sessionId, LocalDateTime createdDate) {
        this.id = id;
        this.actor = actor;
        this.domain = domain;
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

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public AuditDomain getDomain() {
        return domain;
    }

    public void setDomain(AuditDomain domain) {
        this.domain = domain;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
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
        ActionLogDto that = (ActionLogDto) o;
        return Objects.equals(id, that.id) && Objects.equals(actor, that.actor) && domain == that.domain && action == that.action && Objects.equals(sessionId, that.sessionId) && Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, actor, domain, action, sessionId, createdDate);
    }

    @Override
    public String toString() {
        return "ActionLogDto{" +
                "id=" + id +
                ", actor='" + actor + '\'' +
                ", domain=" + domain +
                ", action=" + action +
                ", sessionId='" + sessionId + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }

}
