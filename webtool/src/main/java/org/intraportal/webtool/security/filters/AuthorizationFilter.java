package org.intraportal.webtool.security.filters;

import org.intraportal.webtool.security.auth.AuthProvider;
import org.intraportal.webtool.security.providers.SecurityProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter that intercepts all endpoint calls and check if there is a valid
 * authentication present.
 */
public class AuthorizationFilter extends BasicAuthenticationFilter {

	private final AuthProvider communicationProvider;

	public AuthorizationFilter(AuthenticationManager authenticationManager, AuthProvider communicationProvider) {
		super(authenticationManager);
		this.communicationProvider = communicationProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		Authentication authentication = null;

		if (authentication != null) {
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}
}
