package org.intraportal.persistence.domain.server;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class SystemTime implements Serializable {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    private Boolean utc;

    public SystemTime() {
    }

    public SystemTime(LocalDateTime dateTime, Boolean utc) {
        this.dateTime = dateTime;
        this.utc = utc;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getUtc() {
        return utc;
    }

    public void setUtc(Boolean utc) {
        this.utc = utc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemTime that = (SystemTime) o;
        return Objects.equals(dateTime, that.dateTime) && Objects.equals(utc, that.utc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, utc);
    }

    @Override
    public String toString() {
        return "SystemTime{" +
                "dateTime=" + dateTime +
                ", utc=" + utc +
                '}';
    }

}
