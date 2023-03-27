package org.intraportal.webtool.security.handlers;

import org.intraportal.api.service.audit.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IntraPortalLogoutSuccessHandlerTest {

    private static final String LOGOUT_TARGET_URL = "/web/login?logout=true";

    @Mock
    DefaultRedirectStrategy redirectStrategy;

    @Mock
    AuditLogService auditLogService;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Authentication authentication;


    IntraPortalLogoutSuccessHandler logoutSuccessHandler;

    @BeforeEach
    void setUp() {
        logoutSuccessHandler = new IntraPortalLogoutSuccessHandler(LOGOUT_TARGET_URL, redirectStrategy);
    }

    @Test
    void onLogoutSuccess_redirectsCorrectly() throws ServletException, IOException {
        logoutSuccessHandler.onLogoutSuccess(request, response, authentication);
        verify(redirectStrategy).sendRedirect(request, response, LOGOUT_TARGET_URL);
    }

}