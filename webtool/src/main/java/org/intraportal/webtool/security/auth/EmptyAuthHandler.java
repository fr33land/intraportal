package org.intraportal.webtool.security.auth;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class EmptyAuthHandler implements AuthHandler {

	// ------------------------------------------------------------------------
	// Interface AuthenticationHandler
	// ------------------------------------------------------------------------

	@Override
	public Authentication createAuthentication(HttpServletRequest request) {
		return null;
	}

	@Override
	public void setToken(HttpServletResponse response, String token) {
	}

	@Override
	public boolean canHandleAuthentication(HttpServletRequest request) {
		return false;
	}

	// ------------------------------------------------------------------------
	// Interface AuthorizationHandler
	// ------------------------------------------------------------------------

	@Override
	public String getToken(HttpServletRequest request) {
		return null;
	}

	@Override
	public boolean canHandleAuthorization(HttpServletRequest request) {
		return false;
	}

	@Override
	public void removeToken(HttpServletResponse response) {
	}

}
