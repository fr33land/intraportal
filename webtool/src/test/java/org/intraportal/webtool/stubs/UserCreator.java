package org.intraportal.webtool.stubs;

import org.intraportal.persistence.domain.user.PasswordDto;
import org.intraportal.persistence.domain.user.RoleDto;
import org.intraportal.persistence.domain.user.RolesDto;
import org.intraportal.persistence.domain.user.UserDto;
import org.intraportal.persistence.model.user.Role;
import org.intraportal.persistence.model.user.User;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserCreator {

    public static final String TEST_NAME = "TestName";
    public static final String TEST_LAST_NAME = "TestLastName";
    public static final String TEST_EMAIL = "test@email.com";
    public static final String TEST_USERNAME = "test";
    final static LocalDateTime CREATED_DATE = LocalDateTime.of(2022, 10, 1, 7, 0, 0);
    final static LocalDateTime LAST_LOGIN = LocalDateTime.of(2022, 10, 24, 14, 0, 0);


    public static User getUser(Integer id) {
        return getUser(id, TEST_USERNAME);
    }

    public static User getUser(Integer id, String username) {
        User u = new User();
        u.setId(id);
        u.setFirstName(TEST_NAME);
        u.setLastName(TEST_LAST_NAME);
        u.setEmail(TEST_EMAIL);
        u.setUsername(username);
        u.setEnabled(Boolean.TRUE);
        u.setCreatedDate(OffsetDateTime.of(CREATED_DATE, ZoneOffset.UTC));
        u.setLastLogin(OffsetDateTime.of(LAST_LOGIN, ZoneOffset.UTC));

        ArrayList<Role> roles = new ArrayList<>();

        Role r1 = new Role();
        r1.setId(1);
        r1.setName("ADMIN");
        r1.setDescription("Administrator");
        roles.add(r1);

        Role r2 = new Role();
        r2.setId(2);
        r2.setName("VIEWER");
        r2.setDescription("Viewer");
        roles.add(r2);

        Role r3 = new Role();
        r3.setId(3);
        r3.setName("ADMIN");
        r3.setDescription("Administrator");
        roles.add(r3);

        u.setRoles(roles);
        return u;
    }

    public static UserDto getUserDto(Integer id) {
        UserDto u = new UserDto();
        u.setId(id);
        u.setFirstName(TEST_NAME);
        u.setLastName(TEST_LAST_NAME);
        u.setEmail(TEST_EMAIL);
        u.setUsername(TEST_USERNAME);
        u.setEnabled(Boolean.TRUE);
        u.setCreated(CREATED_DATE);
        u.setLastLogin(LAST_LOGIN);
        return u;
    }

    public static UserDto getUserDto(Integer id, String username, String email) {
        UserDto u = new UserDto();
        u.setId(id);
        u.setFirstName(TEST_NAME);
        u.setLastName(TEST_LAST_NAME);
        u.setEmail(email);
        u.setUsername(username);
        u.setEnabled(Boolean.TRUE);
        return u;
    }

    public static List<UserDto> getUsersListDto() {
        List<UserDto> uldto =  new ArrayList<>();
        uldto.add(getUserDto(1, "user1", "user1@email.com"));
        uldto.add(getUserDto(1, "user2", "user2@email.com"));
        uldto.add(getUserDto(1, "user3", "user3@email.com"));
        return uldto;
    }

    public static DataTablesOutput<UserDto> createUserDtos() {
        var u1 = new UserDto();
        u1.setId(1);
        u1.setFirstName(TEST_NAME);
        u1.setLastName(TEST_LAST_NAME);
        u1.setEmail("user1@email.com");
        u1.setUsername("user1");
        u1.setEnabled(Boolean.TRUE);

        var u2 = new UserDto();
        u2.setId(2);
        u2.setFirstName(TEST_NAME);
        u2.setLastName(TEST_LAST_NAME);
        u2.setEmail("user2@email.com");
        u2.setUsername("user2");
        u2.setEnabled(Boolean.TRUE);

        var u3 = new UserDto();
        u3.setId(2);
        u3.setFirstName(TEST_NAME);
        u3.setLastName(TEST_LAST_NAME);
        u3.setEmail("user3@email.com");
        u3.setUsername("user3");
        u3.setEnabled(Boolean.TRUE);

        DataTablesOutput output = new DataTablesOutput();
        output.setData(List.of(u1, u2, u3));
        output.setRecordsTotal(3);
        output.setRecordsFiltered(3);
        output.setDraw(1);

        return output;
    }

    public static DataTablesOutput<User> getUserList() {
        List<User> userList = new ArrayList<>();

        User u1 = new User();
        u1.setId(1);
        u1.setFirstName(TEST_NAME);
        u1.setLastName(TEST_LAST_NAME);
        u1.setEmail("user1@email.com");
        u1.setUsername("user1");
        u1.setEnabled(Boolean.TRUE);
        userList.add(u1);

        User u2 = new User();
        u2.setId(2);
        u2.setFirstName(TEST_NAME);
        u2.setLastName(TEST_LAST_NAME);
        u2.setEmail("user2@email.com");
        u2.setUsername("user2");
        u2.setEnabled(Boolean.TRUE);
        userList.add(u2);

        User u3 = new User();
        u3.setId(2);
        u3.setFirstName(TEST_NAME);
        u3.setLastName(TEST_LAST_NAME);
        u3.setEmail("user3@email.com");
        u3.setUsername("user3");
        u3.setEnabled(Boolean.TRUE);
        userList.add(u3);

        DataTablesOutput output = new DataTablesOutput();
        output.setData(userList);
        output.setRecordsTotal(3);
        output.setRecordsFiltered(3);
        output.setDraw(1);

        return output;
    }

    public static PasswordDto getPasswordDto(Integer id) {
        PasswordDto p = new PasswordDto();
        p.setUserId(id);
        p.setPasswordFirst("passwd");
        p.setPasswordSecond("passwd");
        return p;
    }

    public static RolesDto getRolesDto(Integer id) {
        RolesDto roles = new RolesDto();
        roles.setUserId(id);

        RoleDto r1 = new RoleDto();
        r1.setId(1);
        r1.setName("ADMIN");
        r1.setDescription("Administrator");

        RoleDto r2 = new RoleDto();
        r2.setId(2);
        r2.setName("VIEWER");
        r2.setDescription("Viewer");

        RoleDto r3 = new RoleDto();
        r3.setId(3);
        r3.setName("ADMIN");
        r3.setDescription("Administrator");

        roles.setList(Arrays.asList(r1, r2, r3));

        return roles;
    }

    public static RolesDto getBadRolesDto() {
        RolesDto roles = new RolesDto();

        RoleDto r1 = new RoleDto();
        r1.setDescription("Administrator");

        RoleDto r2 = new RoleDto();
        r2.setDescription("Viewer");

        RoleDto r3 = new RoleDto();
        r3.setId(3);
        r3.setName("ADMIN");
        r3.setDescription("Administrator");

        roles.setList(Arrays.asList(r1, r2, r3));

        return roles;
    }

}
