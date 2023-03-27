package org.intraportal.api.service.users;

import org.intraportal.persistence.domain.user.RoleDto;
import org.intraportal.persistence.domain.user.UserDto;
import org.intraportal.persistence.model.user.Role;
import org.intraportal.persistence.model.user.User;
import org.intraportal.persistence.repository.user.RoleRepository;
import org.intraportal.persistence.repository.user.UserRepository;
import org.intraportal.persistence.repository.user.UsersRolesRepository;
import org.intraportal.stubs.UserCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.datatables.mapping.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UsersRolesRepository usersRolesRepository;

    @InjectMocks
    UsersService usersService;

    @Test
    void findAll() {
        DataTablesInput input = new DataTablesInput();
        input.setDraw(3);
        input.setLength(4);
        input.setColumns(Collections.singletonList(new Column("id", "id", true, true, new Search())));
        input.setOrder(Collections.singletonList(new Order(0, "asc")));

        List<UserDto> records = UserCreator.mockUserDtoList();

        DataTablesOutput output = new DataTablesOutput();
        output.setDraw(3);
        output.setData(records);
        output.setRecordsTotal(records.size());

        when(userRepository.findAll(any(DataTablesInput.class), any(Function.class))).thenReturn(output);

        DataTablesOutput<UserDto> result = usersService.findAll(input);
        assertArrayEquals(result.getData().toArray(), records.toArray());
    }

    @Test
    void findByUserId() {
        User user = UserCreator.mockUser();

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        User result = usersService.findByUserId(1);

        assertEquals(result.getEmail(), user.getEmail());
        assertEquals(result.getFirstName(), user.getFirstName());
        assertEquals(result.getLastName(), user.getLastName());
        assertEquals(result.getUsername(), user.getUsername());
        assertEquals(result.getId(), user.getId());
    }

    @Test
    void findByUserId_whenUserNotFound() {
        User user = UserCreator.mockUser();

        when(userRepository.findById(anyInt())).thenThrow(new UsernameNotFoundException("User with provided id not found"));
        UsernameNotFoundException result = assertThrows(UsernameNotFoundException.class, () -> usersService.findByUserId(user.getId()));
        assertEquals(result.getMessage(), "User with provided id not found");
    }

    @Test
    void updateUser() {
        User user = UserCreator.mockUser();

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        usersService.updateUser(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createUser() {
        User user = UserCreator.mockUser();
        Role r = new Role();
        r.setId(1);
        r.setName("TECH");

        when(roleRepository.findByName(any())).thenReturn(r);

        usersService.createUser(user);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void createDefaultUser() {
        usersService.createDefaultUser(passwordEncoder);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void deleteUser() {
        User user = UserCreator.mockUser();

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(any());

        usersService.deleteUser(user.getId());
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void updateUserLastLogin() {
        User user = UserCreator.mockUser();

        usersService.updateUserLastLogin(user.getUsername());
        verify(userRepository, times(1)).updateUserLastLogin(eq(user.getUsername()), any());
    }

    @Test
    void updateUserPassword() {
        User user = UserCreator.mockUser();

        usersService.updateUserPassword(user.getId(), user.getPassword());
        verify(userRepository, times(1)).updateUserPassword(user.getId(), user.getPassword());
    }

    @Test
    void updateUserRoles() {
        User user = UserCreator.mockUser();

        doNothing().when(usersRolesRepository).deleteAllByUserId(anyInt());

        usersService.updateUserRoles(user.getId(), Collections.singletonList(new RoleDto(1, "TECH")));
        verify(usersRolesRepository, times(1)).deleteAllByUserId(user.getId());
    }

    @Test
    void getAvailableRoles() {
        Role r = new Role();
        r.setId(1);
        r.setName("TECH");

        when(roleRepository.findAll()).thenReturn(Collections.singletonList(r));

        List<Role> result = usersService.getAvailableRoles();
        assertArrayEquals(result.toArray(), Collections.singletonList(r).toArray());
    }

    @Test
    void findByUserName() {
        User user = UserCreator.mockUser();

        when(userRepository.findByUsername(any())).thenReturn(user);

        User result = usersService.findByUserName(user.getUsername());
        assertEquals(result.getId(), user.getId());
        assertEquals(result.getUsername(), user.getUsername());
        assertEquals(result.getLastName(), user.getLastName());
        assertEquals(result.getFirstName(), user.getFirstName());
    }

    @Test
    void removeUsersData() {
        usersService.removeUsersData();
        verify(usersRolesRepository, times(1)).deleteAll();
        verify(userRepository, times(1)).deleteAll();
    }
}