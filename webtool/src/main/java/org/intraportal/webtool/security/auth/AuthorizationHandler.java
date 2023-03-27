package org.intraportal.webtool.security.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface that was made in order to allow different authorization mechanism
 * to exists concurrently.
 */
public interface AuthorizationHandler {

	/**
	 * Get token from a request received from the client.
	 * 
	 * @param request the request
	 * @return the token or null
	 */
	String getToken(HttpServletRequest request);

	/**
	 * Verify if the entity can handle the authorization process for the given
	 * request.
	 * 
	 * @param request the request
	 * @return true/false
	 */
	boolean canHandleAuthorization(HttpServletRequest request);

	/**
	 * Removes the token from a response.
	 *  
	 * @param response
	 */
	void removeToken(HttpServletResponse response);
}
