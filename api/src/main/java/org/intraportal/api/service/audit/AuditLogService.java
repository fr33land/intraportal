package org.intraportal.api.service.audit;

import org.intraportal.persistence.domain.audit.ActionLogDto;
import org.intraportal.persistence.domain.audit.SessionLogDto;
import org.intraportal.persistence.model.audit.ActionLog;
import org.intraportal.persistence.model.audit.AuditSessionAction;
import org.intraportal.persistence.model.audit.SessionLog;
import org.intraportal.persistence.repository.audit.ActionLogRepository;
import org.intraportal.persistence.repository.audit.SessionLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Expression;

import static org.intraportal.api.model.mappers.ActionLogMapper.toActionLogDto;
import static org.intraportal.api.model.mappers.SessionLogMapper.toSessionLogDto;


@Service("AuditLogService")
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);

    private final SessionLogRepository sessionLogRepository;
    private final ActionLogRepository actionLogRepository;

    public AuditLogService(SessionLogRepository sessionLogRepository, ActionLogRepository actionLogRepository) {
        this.sessionLogRepository = sessionLogRepository;
        this.actionLogRepository = actionLogRepository;
    }

    public DataTablesOutput<SessionLogDto> fetchSessionLogData(DataTablesInput dataTablesInput) {
        var dataTablesOutput = sessionLogRepository.findAll(dataTablesInput, (sessionLog -> toSessionLogDto(sessionLog)));
        return dataTablesOutput;
    }

    public DataTablesOutput<ActionLogDto> fetchActionLogData(DataTablesInput dataTablesInput) {
        var dataTablesOutput = actionLogRepository.findAll(dataTablesInput, (actionLog -> toActionLogDto(actionLog)));
        return dataTablesOutput;
    }

    @Transactional
    public void logUserSessionAction(String sessionId, Authentication authentication, AuditSessionAction action) {
        SessionLog sessionLog = new SessionLog();
        sessionLog.setSessionId(sessionId);
        sessionLog.setUsername(authentication.getName());
        sessionLog.setAction(action);

        logger.info("[Audit] (Service) Session Log: [{}]", sessionLog);

        sessionLogRepository.save(sessionLog);
    }

    public DataTablesOutput<ActionLogDto> getUserActionLogData(String username, DataTablesInput input) {
        Specification<ActionLog> spec = (root, query, criteriaBuilder) -> {
            Expression<String> actorExpression = root.get("actor");
            query.orderBy(criteriaBuilder.desc(root.get("createdDate")));
            return criteriaBuilder.equal(actorExpression, username);
        };

        return actionLogRepository.findAll(input, spec, null, (actionLog -> toActionLogDto(actionLog)));
    }

    public DataTablesOutput<SessionLogDto> getUserSessionLogData(String username, DataTablesInput input) {
        Specification<SessionLog> spec = (root, query, criteriaBuilder) -> {
            Expression<String> actorExpression = root.get("username");
            query.orderBy(criteriaBuilder.desc(root.get("createdDate")));
            return criteriaBuilder.equal(actorExpression, username);
        };

        return sessionLogRepository.findAll(input, spec, null, (sessionLog -> toSessionLogDto(sessionLog)));
    }

    public void removeAuditData() {
        sessionLogRepository.deleteAll();
        actionLogRepository.deleteAll();
    }
}

