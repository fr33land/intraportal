package org.intraportal.security.mock;

import org.intraportal.persistence.model.user.Role;
import org.intraportal.webtool.security.core.IntraPortalUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;
import java.util.List;

public class WithMockIntraPortalUserDetailsSecurityContextFactory implements WithSecurityContextFactory<WithMockIntraPortalUserDetails> {

    @Override
    public SecurityContext createSecurityContext(WithMockIntraPortalUserDetails withUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        List<Role> roles = new ArrayList<>();

        for (String authority : withUser.authorities()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }

        IntraPortalUserDetails principal = new IntraPortalUserDetails(withUser.userId(), withUser.username(), withUser.password(), withUser.firstname(), withUser.lastname(), withUser.email(), true, true, true, true, grantedAuthorities);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());

        context.setAuthentication(auth);
        return context;
    }
}
