package org.intraportal.api.service.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service("UsersSessionService")
public class UsersSessionService {

    private final SessionRegistry sessionRegistry;

    @Autowired
    public UsersSessionService(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public void expireUserSessions(String currentSessionId, boolean expireCurrent) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                UserDetails userDetails = (UserDetails) principal;
                for (SessionInformation information : sessionRegistry.getAllSessions(userDetails, true)) {
                    if (expireCurrent) {
                        information.expireNow();
                    } else {
                        if (!information.getSessionId().equals(currentSessionId)) {
                            information.expireNow();
                        }
                    }
                }
            }
        }
    }
}
