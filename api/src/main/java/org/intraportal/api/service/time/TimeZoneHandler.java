package org.intraportal.api.service.time;

import org.intraportal.persistence.domain.server.TimeZoneDto;
import org.intraportal.persistence.mappers.TimeZoneMapper;
import org.intraportal.persistence.repository.audit.ActionAudit;
import org.intraportal.api.exception.ApiExceptionHandler;
import org.intraportal.api.service.shell.CommandProcessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static org.intraportal.persistence.mappers.TimeZoneMapper.toTimeZoneDto;
import static org.intraportal.persistence.model.audit.AuditAction.CHANGE_TIME_ZONE;
import static org.intraportal.persistence.model.audit.AuditDomain.SERVER;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Service("TimeZoneHandler")
public class TimeZoneHandler {

    private static final Logger logger = LoggerFactory.getLogger(TimeZoneHandler.class);

    public static final String SUDO_COMMAND = "sudo";
    public static final String TIME_DATE_COMMAND = "timedatectl";
    public static final String SET_TIMEZONE_OPERATION = "set-timezone";

    private final CommandProcessHandler commandProcessHandler;

    public TimeZoneHandler(CommandProcessHandler commandProcessHandler) {
        this.commandProcessHandler = commandProcessHandler;
    }

    public TimeZoneDto getServerTimeZone() {
        var timezone = Calendar.getInstance().getTimeZone();
        return toTimeZoneDto(timezone);
    }

    public void updateDefaultTimeZone(String timeZoneId) {
        var timeZone = TimeZone.getTimeZone(timeZoneId);
        logger.info("changing Default TimeZone to: {}", timeZone);
        TimeZone.setDefault(timeZone);
    }

    public boolean validateTimeZoneId(String timeZoneId) {
        var existingTimeZoneOptional = getAvailableTimeZoneDtos().stream()
                .filter(timeZone -> timeZone.getId().equals(timeZoneId))
                .findAny();
        return existingTimeZoneOptional.isPresent();
    }

    public TimeZone getSystemTimeZone() {
        return Calendar.getInstance().getTimeZone();
    }

    public List<TimeZoneDto> getAvailableTimeZoneDtos() {
        var availableTimezoneDtos = Arrays.stream(TimeZone.getAvailableIDs())
                .map(timeZoneId -> TimeZone.getTimeZone(timeZoneId))
                .sorted(comparing(TimeZone::getRawOffset))
                .map(timeZone -> TimeZoneMapper.toTimeZoneDto(timeZone))
                .collect(toList());
        return availableTimezoneDtos;
    }

    @ActionAudit(action = CHANGE_TIME_ZONE, domain = SERVER)
    public void setTimeZone(String timeZoneId) throws Exception {
        logger.info("timezone before operation: {}", Calendar.getInstance().getTimeZone().getID());
        resetTimeZone(timeZoneId);
        logger.info("timezone after operation: {}", Calendar.getInstance().getTimeZone().getID());
    }

    public void resetTimeZone(String timeZoneId) throws Exception {
        commandProcessHandler.executeSingleCommand(ApiExceptionHandler.handleProcessException(),
                SUDO_COMMAND, TIME_DATE_COMMAND, SET_TIMEZONE_OPERATION, timeZoneId);
        updateDefaultTimeZone(timeZoneId);
    }


}
