package org.intraportal.persistence.domain.date;

import java.util.Objects;

public class FormattedTimeRange {

    private String label;
    private String startDate;
    private String endDate;

    public FormattedTimeRange(String label, String startDate, String endDate) {
        this.label = label;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getLabel() {
        return label;
    }

    public FormattedTimeRange setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getStartDate() {
        return startDate;
    }

    public FormattedTimeRange setStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }

    public String getEndDate() {
        return endDate;
    }

    public FormattedTimeRange setEndDate(String endDate) {
        this.endDate = endDate;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormattedTimeRange that = (FormattedTimeRange) o;
        return Objects.equals(label, that.label) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, startDate, endDate);
    }

    @Override
    public String toString() {
        return "FormattedTimeRange{" +
                "label='" + label + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }

}