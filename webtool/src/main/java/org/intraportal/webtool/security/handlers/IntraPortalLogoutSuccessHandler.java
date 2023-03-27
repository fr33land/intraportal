package org.intraportal.webtool.security.handlers;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IntraPortalLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    public IntraPortalLogoutSuccessHandler(String logoutTargetUrl, RedirectStrategy redirectStrategy) {
        super.setDefaultTargetUrl(logoutTargetUrl);
        super.setAlwaysUseDefaultTargetUrl(true);
        super.setRedirectStrategy(redirectStrategy);
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (request.getParameter("reset") != null) {
            super.setDefaultTargetUrl("/web/login?reset=true");
        } else {
            super.setDefaultTargetUrl("/web/login?logout=true");
        }
        super.handle(request, response, authentication);
    }

}
