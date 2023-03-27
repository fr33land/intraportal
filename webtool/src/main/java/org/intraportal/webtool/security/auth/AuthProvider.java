package org.intraportal.webtool.security.auth;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface that facilitates the auth communication between the client and the
 * server.
 */
public interface AuthProvider {

	public AuthenticationHandler getAuthenticationHandler(HttpServletRequest request);
	
	public AuthorizationHandler getAuthorizationHandler(HttpServletRequest request);
}
