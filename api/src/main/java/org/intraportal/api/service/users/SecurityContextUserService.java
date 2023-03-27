package org.intraportal.api.service.users;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("SecurityContextUserService")
public class SecurityContextUserService {

    public String getSessionUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
