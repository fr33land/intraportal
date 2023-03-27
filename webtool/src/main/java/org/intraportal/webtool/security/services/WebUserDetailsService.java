package org.intraportal.webtool.security.services;

import org.intraportal.persistence.repository.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("WebUserDetailsService")
public class WebUserDetailsService extends IntraPortalUserDetailsService {

    private static final List<String> API_ALLOWED_ROLES = new ArrayList<>() {{
        add("ADMIN");
        add("VIEWER");
    }};

    public WebUserDetailsService(UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    protected List<String> getAllowedUserRoles() {
        return API_ALLOWED_ROLES;
    }

}
