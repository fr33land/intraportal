package org.intraportal.stubs;

import org.intraportal.persistence.model.user.Role;
import org.intraportal.persistence.model.user.User;
import org.intraportal.persistence.domain.user.RoleDto;
import org.intraportal.persistence.domain.user.UserDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserCreator {

    public static List<User> mockUsersList() {
        List<User> ul = new ArrayList<>();

        Role r = new Role();
        r.setDescription("test role");
        r.setId(1);
        r.setName("TEST_ROLE");

        User u1 = new User();
        u1.setId(1);
        u1.setEmail("u1@email.com");
        u1.setFirstName("User1");
        u1.setLastName("Last1");
        u1.setUsername("user1");
        u1.setEnabled(Boolean.FALSE);
        u1.setPassword("xxx");
        u1.setRoles(Collections.singletonList(r));
        ul.add(u1);

        User u2 = new User();
        u2.setId(2);
        u2.setEmail("u2@email.com");
        u2.setFirstName("User2");
        u2.setLastName("Last2");
        u2.setUsername("user2");
        u2.setEnabled(Boolean.FALSE);
        u2.setPassword("xxx");
        u2.setRoles(Collections.singletonList(r));
        ul.add(u2);

        User u3 = new User();
        u3.setId(3);
        u3.setEmail("u3@email.com");
        u3.setFirstName("User3");
        u3.setLastName("Last3");
        u3.setUsername("user3");
        u3.setEnabled(Boolean.FALSE);
        u3.setPassword("xxx");
        u3.setRoles(Collections.singletonList(r));
        ul.add(u3);

        return ul;
    }

    public static UserDto mockUserDto() {
        UserDto u1 = new UserDto();
        u1.setId(1);
        u1.setEmail("u1@email.com");
        u1.setFirstName("User1");
        u1.setLastName("Last1");
        u1.setUsername("user1");
        u1.setEnabled(Boolean.FALSE);
        u1.setPasswordFirst("XXX");
        u1.setPasswordSecond("XXX");
        u1.setRolesDto(Collections.singletonList(new RoleDto(1, "TECH")));

        return u1;
    }

    public static User mockUser() {
        Role r = new Role();
        r.setDescription("test role");
        r.setId(1);
        r.setName("TEST_ROLE");

        User u1 = new User();
        u1.setId(1);
        u1.setEmail("u1@email.com");
        u1.setFirstName("User1");
        u1.setLastName("Last1");
        u1.setUsername("user1");
        u1.setEnabled(Boolean.FALSE);
        u1.setRoles(Collections.singletonList(r));

        return u1;
    }

    public static List<UserDto> mockUserDtoList() {
        List<UserDto> ul = new ArrayList<>();

        RoleDto r = new RoleDto();
        r.setDescription("test role");
        r.setId(1);
        r.setName("TEST_ROLE");

        UserDto u1 = new UserDto();
        u1.setId(1);
        u1.setEmail("u1@email.com");
        u1.setFirstName("User1");
        u1.setLastName("Last1");
        u1.setUsername("user1");
        u1.setEnabled(Boolean.FALSE);
        u1.setRolesDto(Collections.singletonList(r));
        ul.add(u1);

        UserDto u2 = new UserDto();
        u2.setId(2);
        u2.setEmail("u2@email.com");
        u2.setFirstName("User2");
        u2.setLastName("Last2");
        u2.setUsername("user2");
        u2.setEnabled(Boolean.FALSE);
        u2.setRolesDto(Collections.singletonList(r));
        ul.add(u2);

        return ul;
    }

    public static UserDto mockBadUserDto(int id, String email, String username) {
        RoleDto r = new RoleDto();
        r.setDescription("test role");
        r.setId(1);
        r.setName("TEST_ROLE");

        UserDto u = new UserDto();
        u.setId(id);
        u.setEmail(email);
        u.setFirstName("User1");
        u.setLastName("Last1");
        u.setUsername(username);
        u.setEnabled(Boolean.FALSE);
        u.setRolesDto(Collections.singletonList(r));

        return u;
    }
}
