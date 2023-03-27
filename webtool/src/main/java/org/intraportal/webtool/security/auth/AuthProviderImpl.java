package org.intraportal.webtool.security.auth;

import org.springframework.security.authentication.AuthenticationManager;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.function.Predicate;

public class AuthProviderImpl implements AuthProvider {

	private final List<AuthHandler> authHandlers;
	private final AuthHandler defaultHandler;

	public AuthProviderImpl(AuthenticationManager authenticationManager) {
		this.defaultHandler = new EmptyAuthHandler();
		this.authHandlers = List.of( //
				new HeaderAuthHandler(authenticationManager), //
				new CookieAuthHandler(authenticationManager) //
		);
	}

	@Override
	public AuthenticationHandler getAuthenticationHandler(HttpServletRequest request) {
		return get(p -> p.canHandleAuthentication(request));
	}

	@Override
	public AuthorizationHandler getAuthorizationHandler(HttpServletRequest request) {
		return get(p -> p.canHandleAuthorization(request));
	}

	private AuthHandler get(Predicate<AuthHandler> condition) {
		return authHandlers.stream().filter(condition).findFirst().orElse(defaultHandler);
	}
}
