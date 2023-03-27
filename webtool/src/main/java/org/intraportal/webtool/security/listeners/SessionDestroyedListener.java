package org.intraportal.webtool.security.listeners;

import org.intraportal.api.service.audit.AuditLogService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;

import javax.servlet.http.HttpSession;

import java.util.List;

import static org.intraportal.persistence.model.audit.AuditSessionAction.LOGOUT;

public class SessionDestroyedListener implements ApplicationListener<HttpSessionDestroyedEvent> {

    private final AuditLogService auditLogService;

    public SessionDestroyedListener(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    public void onApplicationEvent(HttpSessionDestroyedEvent event) {
        HttpSession s = event.getSession();
        List<SecurityContext> lstSecurityContext = event.getSecurityContexts();
        for (SecurityContext securityContext : lstSecurityContext){
            auditLogService.logUserSessionAction(s.getId(), securityContext.getAuthentication(), LOGOUT);
        }
    }
}
