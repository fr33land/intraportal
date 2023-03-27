package org.intraportal.webtool.dto;

import java.util.Objects;

public class TimeSyncStatusDto {

    private Boolean ntpActive;

    private Boolean clockSynchronized;

    public TimeSyncStatusDto() {
    }

    public TimeSyncStatusDto(Boolean ntpActive, Boolean clockSynchronized) {
        this.ntpActive = ntpActive;
        this.clockSynchronized = clockSynchronized;
    }

    public Boolean getNtpActive() {
        return ntpActive;
    }

    public void setNtpActive(Boolean ntpActive) {
        this.ntpActive = ntpActive;
    }

    public Boolean getClockSynchronized() {
        return clockSynchronized;
    }

    public void setClockSynchronized(Boolean clockSynchronized) {
        this.clockSynchronized = clockSynchronized;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSyncStatusDto that = (TimeSyncStatusDto) o;
        return Objects.equals(ntpActive, that.ntpActive) && Objects.equals(clockSynchronized, that.clockSynchronized);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ntpActive, clockSynchronized);
    }

    @Override
    public String toString() {
        return "TimeSyncStatusDto{" +
                "ntpActive=" + ntpActive +
                ", clockSynchronized=" + clockSynchronized +
                '}';
    }

}
