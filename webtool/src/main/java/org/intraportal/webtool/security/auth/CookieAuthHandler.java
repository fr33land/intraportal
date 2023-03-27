package org.intraportal.webtool.security.auth;

import org.intraportal.webtool.security.auth.AuthHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

class CookieAuthHandler implements AuthHandler {

	private static String USERNAME = "username";
	private static String PASSWORD = "password";
	private static String COOKIE_TOKEN_KEY = "JSESSIONID";

	private final AuthenticationManager authenticationManager;

	public CookieAuthHandler(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	// ------------------------------------------------------------------------
	// Interface AuthenticationHandler
	// ------------------------------------------------------------------------

	@Override
	public Authentication createAuthentication(HttpServletRequest request) {
		var username = request.getParameter(USERNAME);
		var password = request.getParameter(PASSWORD);

		return username != null && password != null //
				? authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)) //
				: null;
	}

	@Override
	public void setToken(HttpServletResponse response, String token) {
		response.addCookie(new Cookie(COOKIE_TOKEN_KEY, token));
	}

	@Override
	public boolean canHandleAuthentication(HttpServletRequest request) {
		return request.getParameter(USERNAME) != null && request.getParameter(PASSWORD) != null;
	}

	// ------------------------------------------------------------------------
	// Interface AuthorizationHandler
	// ------------------------------------------------------------------------

	@Override
	public String getToken(HttpServletRequest request) {
		return request.getCookies() != null //
				? Arrays.stream(request.getCookies()) //
						.filter(c -> COOKIE_TOKEN_KEY.equals(c.getName())) //
						.findFirst() //
						.map(Cookie::getValue) //
						.orElse(null) //
				: null;
	}

	@Override
	public boolean canHandleAuthorization(HttpServletRequest request) {
		return request.getCookies() != null && Arrays.stream(request.getCookies()) //
				.filter(c -> COOKIE_TOKEN_KEY.equals(c.getName())) //
				.findFirst() //
				.isPresent();
	}

	@Override
	public void removeToken(HttpServletResponse response) {
		Cookie cookie = new Cookie(COOKIE_TOKEN_KEY, null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);	
	}
}
