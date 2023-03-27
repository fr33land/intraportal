package org.intraportal.persistence.model.audit;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

import static org.intraportal.persistence.mappers.TimeZoneMapper.getOffsetDateTimeUTC;

@Entity(name = "SessionLog")
@Table(name = "session_log")
public class SessionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @LastModifiedBy
    private String username;

    @Enumerated(EnumType.STRING)
    private AuditSessionAction action;

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
        SessionLog that = (SessionLog) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && action == that.action && Objects.equals(sessionId, that.sessionId) && Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, action, sessionId, createdDate);
    }

    @Override
    public String toString() {
        return "SessionLog{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", action=" + action +
                ", sessionId='" + sessionId + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }

}
