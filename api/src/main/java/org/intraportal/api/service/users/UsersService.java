package org.intraportal.api.service.users;

import org.intraportal.persistence.domain.user.RoleDto;
import org.intraportal.persistence.domain.user.UserDto;
import org.intraportal.persistence.model.user.Role;
import org.intraportal.persistence.model.user.User;
import org.intraportal.persistence.repository.audit.ActionAudit;
import org.intraportal.persistence.repository.user.RoleRepository;
import org.intraportal.persistence.repository.user.UserRepository;
import org.intraportal.persistence.repository.user.UsersRolesRepository;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.intraportal.persistence.mappers.TimeZoneMapper.getOffsetDateTimeUTC;
import static org.intraportal.persistence.model.audit.AuditAction.*;
import static org.intraportal.persistence.model.audit.AuditDomain.USER;
import static org.intraportal.api.model.mappers.UserMapper.userToUserDto;

@Service("UsersService")
public class UsersService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UsersRolesRepository usersRolesRepository;

    public UsersService(UserRepository userRepository,
                        RoleRepository roleRepository,
                        UsersRolesRepository usersRolesRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.usersRolesRepository = usersRolesRepository;
    }

    public DataTablesOutput<UserDto> findAll(DataTablesInput dti) {
        DataTablesOutput<UserDto> dto = userRepository.findAll(dti, (user -> userToUserDto(user, true)));
        return dto;
    }

    public User findByUserId(Integer userId) throws UsernameNotFoundException {
        return userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User with provided id not found")
        );
    }

    @ActionAudit(action = EDIT, domain = USER)
    public void updateUser(User u) throws UsernameNotFoundException {
        User user = findByUserId(u.getId());

        user.setEmail(u.getEmail().toLowerCase());
        user.setEnabled(u.getEnabled());
        user.setFirstName(u.getFirstName());
        user.setLastName(u.getLastName());

        userRepository.save(user);
    }

    @ActionAudit(action = CREATE, domain = USER)
    public void createUser(User u) {
        User user = new User();

        user.setUsername(u.getUsername().toLowerCase());
        user.setEmail(u.getEmail().toLowerCase());
        user.setEnabled(u.getEnabled());
        user.setFirstName(u.getFirstName());
        user.setLastName(u.getLastName());
        user.setPassword(u.getPassword());

        Role role = roleRepository.findByName("VIEWER");
        user.setRoles(Collections.singleton(role));

        userRepository.save(user);
    }

    public void createDefaultUser(PasswordEncoder passwordEncoder) {
        User user = new User();

        user.setUsername("admin");
        user.setEmail("admin@domain.com");
        user.setEnabled(Boolean.TRUE);
        user.setFirstName("Admin");
        user.setLastName("Admin");
        user.setPassword(passwordEncoder.encode("admin"));
        Role role = roleRepository.findByName("ADMIN");
        user.setRoles(Collections.singleton(role));

        userRepository.save(user);
    }

    @ActionAudit(action = DELETE, domain = USER)
    public void deleteUser(Integer userId) {
        User user = findByUserId(userId);
        userRepository.delete(user);
    }

    @Transactional
    public void updateUserLastLogin(String username) {
        userRepository.updateUserLastLogin(username, getOffsetDateTimeUTC());
    }

    @Transactional
    @ActionAudit(action = CHANGE_PASSWORD, domain = USER)
    public void updateUserPassword(Integer userId, String password) {
        userRepository.updateUserPassword(userId, password);
    }

    @Transactional(rollbackFor = Exception.class)
    @ActionAudit(action = EDIT_ROLES, domain = USER)
    public void updateUserRoles(Integer userId, List<RoleDto> list) {
        usersRolesRepository.deleteAllByUserId(userId);
        list.stream().filter(RoleDto::isChecked).forEach(r -> usersRolesRepository.updateUserRole(userId, r.getId()));
    }

    public List<Role> getAvailableRoles() {
        return roleRepository.findAll().stream().filter(r -> !r.getName().equals("MOBILE")).collect(Collectors.toList());
    }

    public User findByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    public void removeUsersData() {
        usersRolesRepository.deleteAll();
        userRepository.deleteAll();
    }
}
