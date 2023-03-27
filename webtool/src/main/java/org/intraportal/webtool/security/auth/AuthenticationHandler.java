package org.intraportal.webtool.security.auth;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface that was made in order to allow different authentication mechanism
 * to exists concurrently.
 */
public interface AuthenticationHandler {

	/**
	 * Create an {@link Authentication} entity from the request sent from the
	 * client.
	 * 
	 * @param request the request
	 * @return an appropriate entity or null
	 */
	Authentication createAuthentication(HttpServletRequest request);

	/**
	 * Add the token to the response.
	 * 
	 * @param response the response
	 * @param token    the token
	 */
	void setToken(HttpServletResponse response, String token);

	/**
	 * Verify if the entity can handle the authentication process for the given
	 * request.
	 * 
	 * @param request the request
	 * @return true/false
	 */
	boolean canHandleAuthentication(HttpServletRequest request);
}
