package org.intraportal.webtool.security.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class IntraPortalRedirectInvalidSessionStrategy implements InvalidSessionStrategy {

    private static final Logger logger = LoggerFactory.getLogger(IntraPortalRedirectInvalidSessionStrategy.class);

    private final RedirectStrategy redirectStrategy;

    private boolean createNewSession;

    private final String logoutTargetUrl;

    private final String expiredSessionUrl;

    public IntraPortalRedirectInvalidSessionStrategy(String logoutTargetUrl, String expiredSessionUrl,
                                                    RedirectStrategy redirectStrategy) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(logoutTargetUrl), "url must start with '/' or with 'http(s)'");
        Assert.isTrue(UrlUtils.isValidRedirectUrl(expiredSessionUrl), "url must start with '/' or with 'http(s)'");

        this.redirectStrategy = redirectStrategy;
        this.createNewSession = true;
        this.logoutTargetUrl = logoutTargetUrl;
        this.expiredSessionUrl = expiredSessionUrl;
    }

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String destinationUrl = ServletUriComponentsBuilder
                .fromRequest(request)
                .host(null).scheme(null).port(null)
                .toUriString();

        if (this.createNewSession) {
            request.getSession();
        }

        if (destinationUrl.equals(logoutTargetUrl)) {
            this.logger.debug("createNewSession={}. Redirecting to actual Destination since already being redirected to Logout URL = '{}'", createNewSession, destinationUrl);
            this.redirectStrategy.sendRedirect(request, response, destinationUrl);
            return;
        }

        this.logger.debug("createNewSession={}. Redirecting to Expired Session URL = '{}'", createNewSession, expiredSessionUrl);
        this.redirectStrategy.sendRedirect(request, response, expiredSessionUrl);
    }

    public void setCreateNewSession(boolean createNewSession) {
        this.createNewSession = createNewSession;
    }

}
