package org.intraportal.webtool.security.services;

import org.intraportal.persistence.model.user.User;
import org.intraportal.persistence.repository.user.UserRepository;
import org.intraportal.webtool.security.core.IntraPortalUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class IntraPortalUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntraPortalUserDetailsService.class);

    private final UserRepository userRepository;

    public IntraPortalUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    protected abstract List<String> getAllowedUserRoles();

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username.toLowerCase());

        if (user == null) {
            LOGGER.error("Username {} entered wrong credentials", username);
            throw new UsernameNotFoundException("Wrong credentials!");
        }

        List<String> roleNameList = new ArrayList<>();
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        for (var role : user.getRoles()) {
            roleNameList.add(role.getName());
            authorityList.add(new SimpleGrantedAuthority(role.getName()));
        }

        boolean notContainsRole = Collections.disjoint(roleNameList, getAllowedUserRoles());
        if (notContainsRole) {
            LOGGER.error("User account \"{}\" is not authorized for application - missing role", username);
            throw new UsernameNotFoundException("Not Authorized. Role valid for application is missing!");
        }

        return new IntraPortalUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getEnabled(),
                true,
                true,
                true,
                authorityList
        );
    }

}
