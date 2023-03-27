package org.intraportal.webtool.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("UserService")
public class UserService {

    public String getUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role))) {
            return true;
        }
        return false;
    }

}
