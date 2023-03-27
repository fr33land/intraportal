package org.intraportal.persistence.model.audit;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

import static org.intraportal.persistence.mappers.TimeZoneMapper.getOffsetDateTimeUTC;

@Entity(name = "ActionLog")
@Table(name = "action_log")
public class ActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @LastModifiedBy
    private String actor;

    @Enumerated(EnumType.STRING)
    private AuditDomain domain;

    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @Column(nullable = false)
    private String sessionId;

    @CreatedDate
    private OffsetDateTime createdDate;

    @PrePersist
    protected void prePersist() {
        this.createdDate = getOffsetDateTimeUTC();
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

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionLog actionLog = (ActionLog) o;
        return Objects.equals(id, actionLog.id) && Objects.equals(actor, actionLog.actor) && domain == actionLog.domain && action == actionLog.action && Objects.equals(sessionId, actionLog.sessionId) && Objects.equals(createdDate, actionLog.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, actor, domain, action, sessionId, createdDate);
    }

    @Override
    public String toString() {
        return "ActionLog{" +
                "sessionId=" + sessionId +
                ", actor='" + actor + '\'' +
                ", domain=" + domain +
                ", action=" + action +
                ", createdDate=" + createdDate +
                '}';
    }

}
