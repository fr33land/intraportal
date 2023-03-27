package org.intraportal.persistence.domain.server;

import java.util.Objects;

public class TimeZoneDto {

    private String id;
    private String name;
    private String offset;

    public TimeZoneDto() {
    }

    public TimeZoneDto(String id, String name, String offset) {
        this.id = id;
        this.name = name;
        this.offset = offset;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOffset() {
        return offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeZoneDto that = (TimeZoneDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(offset, that.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, offset);
    }

    @Override
    public String toString() {
        return "TimeZoneDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", offset='" + offset + '\'' +
                '}';
    }

}
