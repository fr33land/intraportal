package org.intraportal.persistence.mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class DateTimeMapper {

    public static LocalDateTime toLocalDateTime(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }

        var currentOffset = OffsetDateTime.now().getOffset();
        var localOffsetDateTime = offsetDateTime.withOffsetSameInstant(currentOffset);

        return localOffsetDateTime.toLocalDateTime();
    }

    public static OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }

        var currentOffset = OffsetDateTime.now().getOffset();
        var localOffsetDateTime = OffsetDateTime.of(localDateTime, currentOffset);
        var offsetDateTimeAtUTC = localOffsetDateTime.withOffsetSameInstant(ZoneOffset.UTC);
        return offsetDateTimeAtUTC;
    }

}
