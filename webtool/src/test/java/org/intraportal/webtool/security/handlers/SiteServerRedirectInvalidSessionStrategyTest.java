package org.intraportal.webtool.security.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.DefaultRedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IntraPortalRedirectInvalidSessionStrategyTest {

    private static final String LOGOUT_TARGET_URL = "/web/login?logout=true";
    private static final String EXPIRED_SESSION_URL = "/web/login?expired=true";

    @Mock
    DefaultRedirectStrategy redirectStrategy;

    HttpServletRequest request;

    @Mock
    HttpServletResponse response;


    IntraPortalRedirectInvalidSessionStrategy intraPortalRedirectInvalidSessionStrategy;

    @BeforeEach
    void setUp() {
        intraPortalRedirectInvalidSessionStrategy = new IntraPortalRedirectInvalidSessionStrategy(
                LOGOUT_TARGET_URL,
                EXPIRED_SESSION_URL,
                redirectStrategy);
    }

    @Test
    void onInvalidSessionDetected_targetRandom_forwardsToExpiredSessionPage() throws IOException {
        request = spy(createGETRequest("/web/home", null));

        intraPortalRedirectInvalidSessionStrategy.onInvalidSessionDetected(request, response);

        verify(request).getSession();
        verify(redirectStrategy).sendRedirect(request, response, EXPIRED_SESSION_URL);
    }

    @Test
    void onInvalidSessionDetected_targetLogout_forwardsToLogoutPage() throws IOException {
        request = spy(createGETRequest("/web/login", "logout=true"));

        intraPortalRedirectInvalidSessionStrategy.onInvalidSessionDetected(request, response);

        verify(request).getSession();
        verify(redirectStrategy).sendRedirect(request, response, LOGOUT_TARGET_URL);
    }

    MockHttpServletRequest createGETRequest(String requestURI, String queryString) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("localhost");
        request.setRequestURI(requestURI);
        request.setQueryString(queryString);
        return request;
    }

}