package org.intraportal.api.model.mappers;

import org.intraportal.persistence.domain.audit.SessionLogDto;
import org.intraportal.persistence.model.audit.SessionLog;

import static org.intraportal.api.model.mappers.DateTimeMapper.toLocalDateTime;

public class SessionLogMapper {

    public static SessionLogDto toSessionLogDto(SessionLog sessionLog) {
        var sessionLogDto = new SessionLogDto();

        sessionLogDto.setId(sessionLog.getId());
        sessionLogDto.setSessionId(sessionLog.getSessionId());
        sessionLogDto.setUsername(sessionLog.getUsername());
        sessionLogDto.setAction(sessionLog.getAction());
        sessionLogDto.setCreatedDate(toLocalDateTime(sessionLog.getCreatedDate()));

        return sessionLogDto;
    }

}
