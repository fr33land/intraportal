package org.intraportal.webtool.security.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HeaderAuthHandler implements AuthHandler {

	private static final String HEADER_CREDENTIALS_KEY = "Authentication";
	private static final String HEADER_CREDENTIALS_VALUE_PREFIX = "Basic";

	private static final String HEADER_AUTHORIZATION_KEY = "Authorization";
	private static final String HEADER_TOKEN_VALUE_PREFIX = "Bearer";

	private final AuthenticationManager authenticationManager;
	private final Decoder decoder = Base64.getDecoder();
	private final Pattern checkCredentialsPattern = Pattern.compile("^([^:]{1,}):([^:]{1,})$");

	public HeaderAuthHandler(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	// ------------------------------------------------------------------------
	// Interface AuthenticationHandler
	// ------------------------------------------------------------------------

	@Override
	public Authentication createAuthentication(HttpServletRequest request) {
		Authentication auth = null;

		var authHeader = request.getHeader(HEADER_CREDENTIALS_KEY);
		if (authHeader != null) {
			String[] authHeaderValues = authHeader.split(" ");

			if (authHeaderValues.length == 2 && HEADER_CREDENTIALS_VALUE_PREFIX.equals(authHeaderValues[0])) {
				String credentials = new String(decoder.decode(authHeaderValues[1]));

				Matcher matcher = checkCredentialsPattern.matcher(credentials);

				if (matcher.find()) {

					String username = matcher.group(1);
					String password = matcher.group(2);

					auth = authenticationManager
							.authenticate(new UsernamePasswordAuthenticationToken(username, password));
				}
			}
		}

		return auth;
	}

	@Override
	public void setToken(HttpServletResponse response, String token) {
		response.setHeader(HEADER_AUTHORIZATION_KEY, HEADER_TOKEN_VALUE_PREFIX + " " + token);
	}

	@Override
	public boolean canHandleAuthentication(HttpServletRequest request) {
		return request.getHeader(HEADER_CREDENTIALS_KEY) != null || request.getHeader(HEADER_AUTHORIZATION_KEY) != null;
	}

	// ------------------------------------------------------------------------
	// Interface AuthorizationHandler
	// ------------------------------------------------------------------------

	@Override
	public String getToken(HttpServletRequest request) {
		String token = null;
		String authHeader = request.getHeader(HEADER_AUTHORIZATION_KEY);

		if (authHeader != null) {
			String[] authHeaderValues = authHeader.split(" ");

			if (authHeaderValues.length == 2 && HEADER_TOKEN_VALUE_PREFIX.equals(authHeaderValues[0])) {
				token = authHeaderValues[1];
			}
		}

		return token;
	}

	@Override
	public boolean canHandleAuthorization(HttpServletRequest request) {
		return request.getHeader(HEADER_AUTHORIZATION_KEY) != null || request.getHeader(HEADER_CREDENTIALS_KEY) != null;
	}

	@Override
	public void removeToken(HttpServletResponse response) {
		// NOT USED
	}
}
