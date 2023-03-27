package org.intraportal.webtool.security.providers;

import org.intraportal.persistence.model.user.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface SecurityProvider {

    /**
     * Generate the token.
     *
     * @param user  the user
     * @param roles the user roles
     * @return the token or null
     */
    String generateToken(User user, List<String> roles);

    /**
     * Retrieve an {@link Authentication} entity from a token.
     *
     * @param token the token
     * @return an appropriate entity or null
     */
    Authentication getAuthentication(String token);

    /**
     * Invalidate the token
     *
     * @param token
     */
    void invalidateToken(String token);
}