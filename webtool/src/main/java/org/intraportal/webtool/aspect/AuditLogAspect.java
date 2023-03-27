package org.intraportal.webtool.aspect;

import org.intraportal.persistence.model.audit.ActionLog;
import org.intraportal.persistence.model.audit.SessionLog;
import org.intraportal.persistence.repository.audit.ActionAudit;
import org.intraportal.persistence.repository.audit.ActionLogRepository;
import org.intraportal.persistence.repository.audit.SessionAudit;
import org.intraportal.persistence.repository.audit.SessionLogRepository;
import org.intraportal.webtool.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component("AuditLogAspect")
public class AuditLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogAspect.class);

    private final SessionLogRepository sessionLogRepository;
    private final ActionLogRepository actionLogRepository;
    private final UserService userService;

    public AuditLogAspect(SessionLogRepository sessionLogRepository, ActionLogRepository actionLogRepository, UserService userService) {
        this.sessionLogRepository = sessionLogRepository;
        this.actionLogRepository = actionLogRepository;
        this.userService = userService;
    }

    @After("@annotation(org.intraportal.persistence.repository.audit.SessionAudit)")
    public void logSessionAuditActivity(JoinPoint joinPoint) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String sessionId = attr.getSessionId();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        SessionAudit sessionAudit = AnnotationUtils.findAnnotation(signature.getMethod(), SessionAudit.class);

        SessionLog sessionLog = new SessionLog();
        sessionLog.setUsername(userService.getUsername());
        sessionLog.setSessionId(sessionId);
        sessionLog.setAction(sessionAudit.action());
        logger.info("[Audit] Session Log: [{}]", sessionLog);

        sessionLogRepository.save(sessionLog);
    }

    @After("@annotation(org.intraportal.persistence.repository.audit.ActionAudit)")
    public void logActionAuditActivity(JoinPoint joinPoint) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String sessionId = attr.getSessionId();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        ActionAudit actionAudit = AnnotationUtils.findAnnotation(signature.getMethod(), ActionAudit.class);

        ActionLog actionLog = new ActionLog();
        actionLog.setActor(userService.getUsername());
        actionLog.setDomain(actionAudit.domain());
        actionLog.setSessionId(sessionId);
        actionLog.setAction(actionAudit.action());
        logger.info("[Audit] Action Log: [{}]", actionLog);

        actionLogRepository.save(actionLog);
    }

}
