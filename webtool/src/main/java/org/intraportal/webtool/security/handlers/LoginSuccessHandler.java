package org.intraportal.webtool.security.handlers;

import org.intraportal.api.service.audit.AuditLogService;
import org.intraportal.api.service.users.UsersService;
import org.intraportal.webtool.security.core.IntraPortalUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.intraportal.persistence.model.audit.AuditSessionAction.STARTED;

public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UsersService usersService;
    private final AuditLogService auditLogService;

    public LoginSuccessHandler(UsersService usersService, String defaultUri, AuditLogService auditLogService) {
        super(defaultUri);
        this.usersService = usersService;
        this.auditLogService = auditLogService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        IntraPortalUserDetails userDetails = (IntraPortalUserDetails) authentication.getPrincipal();
        usersService.updateUserLastLogin(userDetails.getUsername());
        auditLogService.logUserSessionAction(request.getSession().getId(), authentication, STARTED);

        super.onAuthenticationSuccess(request, response, authentication);
    }

}