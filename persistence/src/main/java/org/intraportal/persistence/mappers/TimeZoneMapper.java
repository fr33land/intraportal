package org.intraportal.persistence.mappers;

import org.intraportal.persistence.domain.server.TimeZoneDto;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class TimeZoneMapper {

    private static DateTimeFormatter offsetFormatter = DateTimeFormatter.ofPattern("xxx");

    public static TimeZoneDto toTimeZoneDto(TimeZone timeZone) {
        int offsetSeconds = timeZone.getRawOffset() / 1000;
        var zoneOffset = ZoneOffset.ofTotalSeconds(offsetSeconds);
        String formattedOffset = offsetFormatter.format(zoneOffset);
        var timeZoneId = timeZone.getID();

        return new TimeZoneDto(timeZoneId, timeZone.getDisplayName(), formattedOffset);
    }

    public static OffsetDateTime getOffsetDateTimeUTC() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

}
