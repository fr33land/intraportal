package org.intraportal.webtool.security.core;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Optional;

public class IntraPortalUserDetails extends User {

    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;

    public IntraPortalUserDetails(Integer userId, String username, String password, String firstName, String lastName, String email, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public IntraPortalUserDetails(Integer userId, String username, String password, String firstName, String lastName, String email, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        String fullName = Optional.ofNullable(firstName).orElse("") + " " + Optional.ofNullable(lastName).orElse("");
        if(fullName.trim().length() == 0) {
            return getUsername();
        } else {
            return fullName;
        }
    }

}
