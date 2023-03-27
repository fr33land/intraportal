package org.intraportal.api.service.validation;

import org.intraportal.api.service.users.SecurityContextUserService;
import org.intraportal.persistence.domain.ValidationResult;
import org.intraportal.persistence.domain.user.PasswordDto;
import org.intraportal.persistence.domain.user.UserDto;
import org.intraportal.persistence.model.user.User;
import org.intraportal.persistence.repository.user.UserRepository;
import org.intraportal.stubs.UserCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsersValidationServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    SecurityContextUserService contextUserService;

    @InjectMocks
    UsersValidationService usersValidationService;

    @Test
    public void validateCreateUser_withEmptyUsername() {
        UserDto userDto = UserCreator.mockUserDto();
        userDto.setUsername("");

        ValidationResult result = usersValidationService.validateCreateUser(userDto);
        assertEquals(result.getValidationMessage(), "Username is missing");
    }

    @Test
    public void validateCreateUser_withNullUsername() {
        UserDto userDto = UserCreator.mockUserDto();
        userDto.setUsername(null);

        ValidationResult result = usersValidationService.validateCreateUser(userDto);
        assertEquals(result.getValidationMessage(), "Username is missing");
    }

    @Test
    public void validateCreateUser_withTooShortUsername() {
        UserDto userDto = UserCreator.mockUserDto();
        userDto.setUsername("aa");

        ValidationResult result = usersValidationService.validateCreateUser(userDto);
        assertEquals(result.getValidationMessage(), "Username must be at least 3 characters long");
    }

    @Test
    public void validateCreateUser_withWhitespaceUsername() {
        UserDto userDto = UserCreator.mockUserDto();
        userDto.setUsername("                    ");

        ValidationResult result = usersValidationService.validateCreateUser(userDto);
        assertEquals(result.getValidationMessage(), "Username is missing");
    }

    @Test
    public void validateCreateUser_withBadUsername() {
        UserDto userDto = UserCreator.mockUserDto();
        userDto.setUsername("BAD USER");

        ValidationResult result = usersValidationService.validateCreateUser(userDto);
        assertEquals(result.getValidationMessage(), "Username contains whitespaces");
    }

    @Test
    public void validateCreateUser_withInvalidUsername() {
        UserDto userDto = UserCreator.mockUserDto();
        userDto.setUsername("INVALID;'USER");

        ValidationResult result = usersValidationService.validateCreateUser(userDto);
        assertEquals(result.getValidationMessage(), "Username must contain only alphanumeric characters");
    }

    @Test
    public void validateCreateUser_withExistingUsername() {
        UserDto userDto = UserCreator.mockUserDto();
        User user = UserCreator.mockUser();

        when(userRepository.findByUsername(any())).thenReturn(user);

        ValidationResult result = usersValidationService.validateCreateUser(userDto);
        assertEquals(result.getValidationMessage(), "Username already exists");
    }

    @Test
    public void validateCreateUser_withInvalidEmail() {
        UserDto userDto = UserCreator.mockUserDto();
        userDto.setEmail("@.lt");

        when(userRepository.findByUsername(any())).thenReturn(null);

        ValidationResult result = usersValidationService.validateCreateUser(userDto);
        assertEquals(result.getValidationMessage(), "Incorrect email format");
    }

    @Test
    public void validateCreateUser_withExistingEmail() {
        UserDto userDto = UserCreator.mockUserDto();
        User user = UserCreator.mockUser();

        when(userRepository.findByUsername(any())).thenReturn(null);
        when(userRepository.findByEmail(any())).thenReturn(user);

        ValidationResult result = usersValidationService.validateCreateUser(userDto);
        assertEquals(result.getValidationMessage(), "Email already used");
    }

    @Test
    public void validateCreateUser_withEmptyPassword() {
        UserDto userDto = UserCreator.mockUserDto();
        userDto.setPasswordFirst(null);

        when(userRepository.findByUsername(any())).thenReturn(null);
        when(userRepository.findByEmail(any())).thenReturn(null);

        ValidationResult result = usersValidationService.validateCreateUser(userDto);
        assertEquals(result.getValidationMessage(), "Password is empty");
    }

    @Test
    public void validateCreateUser_withNotMatchingPassword() {
        UserDto userDto = UserCreator.mockUserDto();
        userDto.setPasswordFirst("aaa");
        userDto.setPasswordSecond("bbb");

        when(userRepository.findByUsername(any())).thenReturn(null);
        when(userRepository.findByEmail(any())).thenReturn(null);

        ValidationResult result = usersValidationService.validateCreateUser(userDto);
        assertEquals(result.getValidationMessage(), "Passwords does not match");
    }

    @Test
    public void validateCreateUser() {
        UserDto userDto = UserCreator.mockUserDto();

        when(userRepository.findByUsername(any())).thenReturn(null);
        when(userRepository.findByEmail(any())).thenReturn(null);

        ValidationResult result = usersValidationService.validateCreateUser(userDto);
        assertEquals(result.isNotValid(), Boolean.FALSE);
    }

    @Test
    public void validateEditUser_withInvalidEmail() {
        UserDto userDto = UserCreator.mockUserDto();
        userDto.setEmail("@.lt");

        ValidationResult result = usersValidationService.validateEditUser(userDto);
        assertEquals(result.getValidationMessage(), "Incorrect email format");
    }

    @Test
    public void validateEditUser_withExistingEmail() {
        UserDto userDto = UserCreator.mockUserDto();
        User user = UserCreator.mockUser();
        user.setUsername("user2");

        when(userRepository.findByEmail(any())).thenReturn(user);

        ValidationResult result = usersValidationService.validateEditUser(userDto);
        assertEquals(result.getValidationMessage(), "Email already used");
    }

    @Test
    public void validateEditUser() {
        String userName = "user22";
        UserDto userDto = UserCreator.mockUserDto();
        User user = UserCreator.mockUser();

        when(userRepository.findByEmail(any())).thenReturn(user);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(contextUserService.getSessionUsername()).thenReturn(userName);

        ValidationResult result = usersValidationService.validateEditUser(userDto);
        assertEquals(result.isNotValid(), Boolean.FALSE);
    }

    @Test
    public void validateSelfEditing_withSelfUser() {
        String userName = "user1";
        User user = UserCreator.mockUser();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(contextUserService.getSessionUsername()).thenReturn(userName);

        ValidationResult result = usersValidationService.validateUserCanEdit(1);
        assertEquals(result.getValidationMessage(), "Editing self information is prohibited. Change user data via <a href=\"/web/user/settings\">settings</a> page");
    }

    @Test
    public void validateSelfEditing() {
        String loggedUser = "user2";
        User user = UserCreator.mockUser();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(contextUserService.getSessionUsername()).thenReturn(loggedUser);

        ValidationResult result = usersValidationService.validateUserCanEdit(1);
        assertEquals(result.isNotValid(), Boolean.FALSE);
    }

    @Test
    public void validateSelfEditing_withUserNotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        ValidationResult result = usersValidationService.validateUserCanEdit(1);
        assertEquals(result.isNotValid(), Boolean.TRUE);
        assertEquals(result.getValidationMessage(), "User not found");
    }

    @Test
    public void validateSelfEditing_withAdminUser() {
        String loggedUser = "notadmin";
        User user = UserCreator.mockUser();
        user.setUsername("admin");

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(contextUserService.getSessionUsername()).thenReturn(loggedUser);

        ValidationResult result = usersValidationService.validateUserCanEdit(1);
        assertEquals(result.isNotValid(), Boolean.TRUE);
        assertEquals(result.getValidationMessage(), "User cannot be updated");
    }

    @Test
    public void validateUpdateUserPassword_withEmptyPassword() {
        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setUserId(1);
        passwordDto.setPasswordFirst("");

        ValidationResult result = usersValidationService.validateUpdateUserPassword(passwordDto);
        assertEquals(result.getValidationMessage(), "Password is empty");
    }

    @Test
    public void validateUpdateUserPassword_withNotMatchPassword() {
        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setUserId(1);
        passwordDto.setPasswordFirst("ABC");
        passwordDto.setPasswordSecond("DEF");

        ValidationResult result = usersValidationService.validateUpdateUserPassword(passwordDto);
        assertEquals(result.getValidationMessage(), "Passwords does not match");
    }

    @Test
    public void validateUpdateUserPassword() {
        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setUserId(1);
        passwordDto.setPasswordFirst("ABC");
        passwordDto.setPasswordSecond("ABC");

        ValidationResult result = usersValidationService.validateUpdateUserPassword(passwordDto);
        assertEquals(result.isNotValid(), Boolean.FALSE);
    }

}