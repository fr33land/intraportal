package org.intraportal.persistence.domain.server;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class ServerTime implements Serializable {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime time;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime timeUTC;

    private TimeZoneDto timezone;

    public ServerTime() {
    }

    public ServerTime(LocalDate date, LocalTime time, LocalTime timeUTC, TimeZoneDto timezone) {
        this.date = date;
        this.time = time;
        this.timeUTC = timeUTC;
        this.timezone = timezone;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public LocalTime getTimeUTC() {
        return timeUTC;
    }

    public TimeZoneDto getTimezone() {
        return timezone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerTime that = (ServerTime) o;
        return Objects.equals(date, that.date) && Objects.equals(time, that.time) && Objects.equals(timeUTC, that.timeUTC) && Objects.equals(timezone, that.timezone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time, timeUTC, timezone);
    }

    @Override
    public String toString() {
        return "ServerTime{" +
                "date=" + date +
                ", time=" + time +
                ", timeUTC=" + timeUTC +
                ", timezone=" + timezone +
                '}';
    }

}
