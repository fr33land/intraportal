package org.intraportal.api.model.mappers;


import org.intraportal.persistence.domain.audit.ActionLogDto;
import org.intraportal.persistence.model.audit.ActionLog;

import static org.intraportal.api.model.mappers.DateTimeMapper.toLocalDateTime;


public class ActionLogMapper {

    public static ActionLogDto toActionLogDto(ActionLog actionLog) {
        var actionLogDto = new ActionLogDto();

        actionLogDto.setId(actionLog.getId());
        actionLogDto.setSessionId(actionLog.getSessionId());
        actionLogDto.setActor(actionLog.getActor());
        actionLogDto.setDomain(actionLog.getDomain());
        actionLogDto.setAction(actionLog.getAction());
        actionLogDto.setCreatedDate(toLocalDateTime(actionLog.getCreatedDate()));

        return actionLogDto;
    }

}
