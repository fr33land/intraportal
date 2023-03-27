package org.intraportal.webtool.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.intraportal.api.service.users.SecurityContextUserService;
import org.intraportal.api.service.audit.AuditLogService;
import org.intraportal.api.service.users.UsersService;
import org.intraportal.api.service.validation.UsersValidationService;
import org.intraportal.persistence.domain.ValidationResult;
import org.intraportal.persistence.domain.user.PasswordDto;
import org.intraportal.persistence.domain.user.RolesDto;
import org.intraportal.persistence.domain.user.UserDto;
import org.intraportal.security.mock.WithMockIntraPortalUserDetails;
import org.intraportal.webtool.stubs.ActionLogCreator;
import org.intraportal.webtool.stubs.UserCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.datatables.mapping.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AnnotationConfigContextLoader.class})
@WebMvcTest(UsersController.class)
public class UsersControllerTest {

    private static final String EXISTING_USER_NAME = "elsi";
    private static final String USER_NOT_FOUND_MESSAGE = "User with provided id not found";
    private static final String TAB = "tab";
    private static final String USER_ATTRIBUTE = "user";
    public static final String PASSWORD_ATTRIBUTE = "password";
    private static final String ROLES_ATTRIBUTE = "roles";
    private static final String MESSAGE_ATTRIBUTE = "message";
    private static final String ERROR_ATTRIBUTE = "error";
    private static final String EDIT_ACTION = "edit";
    private static final String CREATE_ACTION = "create";
    private static final String ERROR_FORM = "templates/error.html";
    private static final String EDIT_FORM = "templates/admin/users/edit.html";
    private static final String CREATE_FORM = "templates/admin/users/create.html";

    private MockMvc mockMvc;

    @Mock
    private UsersService usersService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private UsersValidationService usersValidationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContextUserService contextUserService;

    @InjectMocks
    private UsersController usersController;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(usersController).build();

        when(contextUserService.getSessionUsername()).thenReturn(EXISTING_USER_NAME);
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"ROLE_ADMIN"})
    public void loadUsersPage() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/web/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("templates/admin/users/overview.html"));
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"ROLE_ADMIN"})
    public void getUsersList() throws Exception {
        when(usersService.findAll(any())).thenReturn(UserCreator.createUserDtos());

        DataTablesInput input = new DataTablesInput();
        input.setDraw(3);
        input.setLength(4);
        input.setColumns(Collections.singletonList(new Column("id", "id", true, true, new Search())));
        input.setOrder(Collections.singletonList(new Order(0, "asc")));

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/list")
                                .content(objectMapper.writeValueAsString(input))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        DataTablesOutput output = objectMapper.readValue(result.getResponse().getContentAsString(), DataTablesOutput.class);

        assertThat(output.getDraw()).isEqualTo(1);
        assertThat(output.getData().size()).isEqualTo(3);
        assertThat(output.getRecordsTotal()).isEqualTo(3);
    }


    @Test
    @WithMockUser(username = "user1", authorities = {"ROLE_ADMIN"})
    public void editUserPage() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(userId)).thenReturn(UserCreator.getUser(userId));
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/web/admin/users/edit/" + userId))
                .andExpect(status().isOk())
                .andExpect(view().name(EDIT_FORM))
                .andExpect(model().attribute(EDIT_ACTION, equalTo(Boolean.TRUE)))
                .andExpect(model().attribute(TAB, equalTo("user-info")));
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"ROLE_ADMIN"})
    public void editUserPage_withInvalidUser() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(userId)).thenReturn(UserCreator.getUser(userId));
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.inValid("User not found"));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/web/admin/users/edit/" + userId))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_FORM))
                .andExpect(model().attribute(ERROR_ATTRIBUTE, equalTo("User not found")));
    }

    @Test
    @WithMockUser(username = EXISTING_USER_NAME, authorities = {"ROLE_ADMIN"})
    public void editUserPage_selfDisable_getsPrevented() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(userId)).thenReturn(UserCreator.getUser(userId, EXISTING_USER_NAME));
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/web/admin/users/edit/" + userId))
                .andExpect(status().isOk())
                .andExpect(view().name(EDIT_FORM))
                .andExpect(model().attribute(EDIT_ACTION, equalTo(Boolean.TRUE)))
                .andExpect(model().attribute(TAB, equalTo("user-info")));
    }


    @Test
    @WithMockUser(username = "user1", authorities = {"ROLE_ADMIN"})
    public void editUserPage_withUserNotFound() throws Exception {
        when(usersService.findByUserId(any())).thenThrow(new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/web/admin/users/edit/9999"))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_FORM));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 3, username = "user1", authorities = {"ROLE_ADMIN"})
    public void saveUser() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(1));
        when(usersValidationService.validateEditUser(any())).thenReturn(ValidationResult.valid());
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/save")
                                .param("save", "true")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(MESSAGE_ATTRIBUTE, equalTo("User successfully updated")))
                .andExpect(redirectedUrl("/web/admin/users"));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 3, username = "user1", authorities = {"ROLE_ADMIN"})
    public void saveUser_withBindingError() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(1));
        when(usersValidationService.validateEditUser(any())).thenReturn(ValidationResult.valid());
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());

        UserDto userDto = UserCreator.getUserDto(userId);
        userDto.setFirstName("User1");
        userDto.setLastName("Last1");
        userDto.setUsername(null);
        userDto.setEmail(null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/save")
                                .param("save", "true")
                                .flashAttr(USER_ATTRIBUTE, userDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/edit/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE))
                .andExpect(flash().attributeExists(USER_ATTRIBUTE));

    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "user1", authorities = {"ROLE_ADMIN"})
    public void saveUser_withSelfEditingError() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(1));
        when(usersValidationService.validateEditUser(any())).thenReturn(ValidationResult.valid());
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.inValid("BAD"));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/save")
                                .param("save", "true")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/edit/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE))
                .andExpect(flash().attributeExists(USER_ATTRIBUTE));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "user1", authorities = {"ROLE_ADMIN"})
    public void saveUser_withValidationError() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(1));
        when(usersValidationService.validateEditUser(any())).thenReturn(ValidationResult.inValid("BAD"));
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/save")
                                .param("save", "true")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/edit/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE))
                .andExpect(flash().attributeExists(USER_ATTRIBUTE));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "user1", authorities = {"ROLE_ADMIN"})
    public void saveUser_withUserNotFoundException() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        when(usersValidationService.validateEditUser(any())).thenReturn(ValidationResult.valid());
        doThrow(new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE)).when(usersService).updateUser(any());
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/save")
                                .param("save", "true")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/edit/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE))
                .andExpect(flash().attributeExists(USER_ATTRIBUTE));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "user1", authorities = {"ROLE_ADMIN"})
    public void saveUser_withIllegalArgumentException() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        when(usersValidationService.validateEditUser(any())).thenReturn(ValidationResult.valid());
        doThrow(new IllegalArgumentException(USER_NOT_FOUND_MESSAGE)).when(usersService).updateUser(any());
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/save")
                                .param("save", "true")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/edit/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE))
                .andExpect(flash().attributeExists(USER_ATTRIBUTE));
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"ROLE_ADMIN"})
    public void newUserPage() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/web/admin/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name(CREATE_FORM))
                .andExpect(model().attributeExists(USER_ATTRIBUTE))
                .andExpect(model().attribute(CREATE_ACTION, equalTo(Boolean.TRUE)));
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"ROLE_ADMIN"})
    public void createUser() throws Exception {
        when(passwordEncoder.encode(any())).thenReturn("HASHED");
        when(usersValidationService.validateCreateUser(any())).thenReturn(ValidationResult.valid());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/save")
                                .param("create", "true")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(null)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(MESSAGE_ATTRIBUTE, equalTo("User successfully created")))
                .andExpect(redirectedUrl("/web/admin/users"));
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"ROLE_ADMIN"})
    public void createUser_withBindingError() throws Exception {
        when(passwordEncoder.encode(any())).thenReturn("HASHED");
        when(usersValidationService.validateCreateUser(any())).thenReturn(ValidationResult.valid());

        UserDto userDto = UserCreator.getUserDto(null);
        userDto.setUsername(null);
        userDto.setEmail(null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/save")
                                .param("create", "true")
                                .flashAttr(USER_ATTRIBUTE, userDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/new/"))
                .andExpect(flash().attributeExists(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE))
                .andExpect(flash().attributeExists(USER_ATTRIBUTE));

    }

    @Test
    @WithMockUser(username = "user1", authorities = {"ROLE_ADMIN"})
    public void createUser_withValidationError() throws Exception {
        when(passwordEncoder.encode(any())).thenReturn("HASHED");
        when(usersValidationService.validateCreateUser(any())).thenReturn(ValidationResult.inValid("BAD"));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/save")
                                .param("create", "true")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(null)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/new/"))
                .andExpect(flash().attributeExists(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE))
                .andExpect(flash().attributeExists(USER_ATTRIBUTE));
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"ROLE_ADMIN"})
    public void createUser_withException() throws Exception {
        when(usersValidationService.validateCreateUser(any())).thenReturn(ValidationResult.valid());
        doThrow(new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE)).when(usersService).createUser(any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/save")
                                .param("create", "true")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(null)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/new/"))
                .andExpect(flash().attributeExists(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE))
                .andExpect(flash().attributeExists(USER_ATTRIBUTE));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "test1", authorities = {"ROLE_ADMIN"})
    public void deleteUser() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());
        doNothing().when(usersService).deleteUser(any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/save")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId))
                                .param("delete", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(MESSAGE_ATTRIBUTE, equalTo("User successfully removed")))
                .andExpect(redirectedUrl("/web/admin/users"));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
    public void deleteUser_withNotFound() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());
        doThrow(new UsernameNotFoundException("User with provided id not found")).when(usersService).deleteUser(any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/save")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId))
                                .param("delete", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(ERROR_ATTRIBUTE, equalTo("User not found")))
                .andExpect(redirectedUrl("/web/admin/users"));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
    public void deleteUser_withBadValidation() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.inValid("User not found"));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/save")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId))
                                .param("delete", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/edit/" + userId));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
    public void deleteUser_withNotUserFoundException() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenThrow(new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/web/admin/users/delete/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "user1", authorities = {"ROLE_ADMIN"})
    public void changeUserPassword() throws Exception {
        Integer userId = 1;
        when(usersValidationService.validateUpdateUserPassword(any())).thenReturn(ValidationResult.valid());
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        doNothing().when(usersService).updateUserPassword(any(), any());
        when(passwordEncoder.encode(any())).thenReturn("HASHED");
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/passwd/change")
                                .param("change", "true")
                                .flashAttr(PASSWORD_ATTRIBUTE, UserCreator.getPasswordDto(userId))
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/edit/1"));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 3, username = "user1", authorities = {"ROLE_ADMIN"})
    public void changeUserPassword_withBindingError() throws Exception {
        Integer userId = 1;
        when(usersValidationService.validateUpdateUserPassword(any())).thenReturn(ValidationResult.valid());
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());

        PasswordDto p = UserCreator.getPasswordDto(userId);
        p.setPasswordSecond(null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/passwd/change")
                                .param("change", "true")
                                .flashAttr(PASSWORD_ATTRIBUTE, p)
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/edit/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getCanonicalName() + "." + PASSWORD_ATTRIBUTE));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 3, username = "user1", authorities = {"ROLE_ADMIN"})
    public void changeUserPassword_withValidationError() throws Exception {
        Integer userId = 1;
        when(usersValidationService.validateUpdateUserPassword(any())).thenReturn(ValidationResult.inValid("BAD"));
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());

        PasswordDto p = UserCreator.getPasswordDto(userId);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/passwd/change")
                                .param("change", "true")
                                .flashAttr(PASSWORD_ATTRIBUTE, p)
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/edit/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getCanonicalName() + "." + PASSWORD_ATTRIBUTE))
                .andExpect(flash().attribute(TAB, equalTo("user-password")))
                .andExpect(flash().attribute(EDIT_ACTION, equalTo(Boolean.TRUE)));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 3, username = "user1", authorities = {"ROLE_ADMIN"})
    public void changeUserPassword_withSelfValidationError() throws Exception {
        Integer userId = 1;
        when(usersValidationService.validateUpdateUserPassword(any())).thenReturn(ValidationResult.valid());
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.inValid("BAD"));

        PasswordDto p = UserCreator.getPasswordDto(userId);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/passwd/change")
                                .param("change", "true")
                                .flashAttr(PASSWORD_ATTRIBUTE, p)
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/edit/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getCanonicalName() + "." + PASSWORD_ATTRIBUTE))
                .andExpect(flash().attribute(TAB, equalTo("user-password")))
                .andExpect(flash().attribute(EDIT_ACTION, equalTo(Boolean.TRUE)));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 3, username = "user1", authorities = {"ROLE_ADMIN"})
    public void changeUserPassword_withUsernameNotFoundException() throws Exception {
        Integer userId = 1;
        when(usersValidationService.validateUpdateUserPassword(any())).thenReturn(ValidationResult.valid());
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        when(passwordEncoder.encode(any())).thenReturn("HASHED");
        doThrow(new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE)).when(usersService).updateUserPassword(any(), any());
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());

        PasswordDto p = UserCreator.getPasswordDto(userId);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/passwd/change")
                                .param("change", "true")
                                .flashAttr(PASSWORD_ATTRIBUTE, p)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/edit/1"));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 3, username = "user1", authorities = {"ROLE_ADMIN"})
    public void updateUserRoles() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        doNothing().when(usersService).updateUserRoles(any(), any());
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/roles/update")
                                .param("update", "true")
                                .flashAttr(ROLES_ATTRIBUTE, UserCreator.getRolesDto(userId))
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/edit/1"));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 3, username = "user1", authorities = {"ROLE_ADMIN"})
    public void updateUserRoles_withBindingError() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        doNothing().when(usersService).updateUserRoles(any(), any());
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());

        RolesDto r = UserCreator.getBadRolesDto();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/roles/update")
                                .param("update", "true")
                                .flashAttr(ROLES_ATTRIBUTE, r)
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/edit/null"))
                .andExpect(flash().attributeExists(BindingResult.class.getCanonicalName() + "." + PASSWORD_ATTRIBUTE))
                .andExpect(flash().attributeExists(ROLES_ATTRIBUTE));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 3, username = "user1", authorities = {"ROLE_ADMIN"})
    public void updateUserRoles_withInvalidResult() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        doNothing().when(usersService).updateUserRoles(any(), any());
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.inValid("BAD"));

        RolesDto r = UserCreator.getRolesDto(userId);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/roles/update")
                                .param("update", "true")
                                .flashAttr(ROLES_ATTRIBUTE, r)
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/edit/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getCanonicalName() + "." + PASSWORD_ATTRIBUTE));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 3, username = "user1", authorities = {"ROLE_ADMIN"})
    public void updateUserRoles_withUsernameNotFoundException() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        when(usersValidationService.validateUserCanEdit(any())).thenReturn(ValidationResult.valid());
        doThrow(new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE)).when(usersService).updateUserRoles(any(), any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/roles/update")
                                .param("update", "true")
                                .flashAttr(ROLES_ATTRIBUTE, UserCreator.getRolesDto(userId))
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users/edit/1"));
    }

    @Test
    public void cancelSaveUser() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/save")
                                .param("cancel", "true")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/admin/users"));
    }

    @Test
    public void auditUserAction() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        when(auditLogService.getUserActionLogData(any(), any())).thenReturn(ActionLogCreator.createActionLogDtos());

        DataTablesInput input = new DataTablesInput();
        input.setDraw(3);
        input.setLength(4);
        input.setColumns(Collections.singletonList(new Column("id", "id", true, true, new Search())));
        input.setOrder(Collections.singletonList(new Order(0, "asc")));

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/audit/action/" + userId)
                                .content(objectMapper.writeValueAsString(input))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        DataTablesOutput output = objectMapper.readValue(result.getResponse().getContentAsString(), DataTablesOutput.class);

        assertThat(output.getDraw()).isEqualTo(1);
        assertThat(output.getData().size()).isEqualTo(3);
        assertThat(output.getRecordsTotal()).isEqualTo(3);
    }

    @Test
    public void auditUserAction_userNotFoundException() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenThrow(new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));

        DataTablesInput input = new DataTablesInput();
        input.setDraw(3);
        input.setLength(4);
        input.setColumns(Collections.singletonList(new Column("id", "id", true, true, new Search())));
        input.setOrder(Collections.singletonList(new Order(0, "asc")));

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/audit/action/" + userId)
                                .content(objectMapper.writeValueAsString(input))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        DataTablesOutput output = objectMapper.readValue(result.getResponse().getContentAsString(), DataTablesOutput.class);

        assertThat(output.getDraw()).isEqualTo(0);
        assertThat(output.getData().size()).isEqualTo(0);
        assertThat(output.getRecordsTotal()).isEqualTo(0);
    }

    @Test
    public void auditUserSession() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenReturn(UserCreator.getUser(userId));
        when(auditLogService.getUserSessionLogData(any(), any())).thenReturn(ActionLogCreator.getSessionLogDto());

        DataTablesInput input = new DataTablesInput();
        input.setDraw(3);
        input.setLength(4);
        input.setColumns(Collections.singletonList(new Column("id", "id", true, true, new Search())));
        input.setOrder(Collections.singletonList(new Order(0, "asc")));

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/audit/session/" + userId)
                                .content(objectMapper.writeValueAsString(input))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        DataTablesOutput output = objectMapper.readValue(result.getResponse().getContentAsString(), DataTablesOutput.class);

        assertThat(output.getDraw()).isEqualTo(1);
        assertThat(output.getData().size()).isEqualTo(3);
        assertThat(output.getRecordsTotal()).isEqualTo(3);
    }

    @Test
    public void auditUserSession_userNotFoundException() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserId(any())).thenThrow(new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));

        DataTablesInput input = new DataTablesInput();
        input.setDraw(3);
        input.setLength(4);
        input.setColumns(Collections.singletonList(new Column("id", "id", true, true, new Search())));
        input.setOrder(Collections.singletonList(new Order(0, "asc")));

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/admin/users/audit/session/" + userId)
                                .content(objectMapper.writeValueAsString(input))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        DataTablesOutput output = objectMapper.readValue(result.getResponse().getContentAsString(), DataTablesOutput.class);

        assertThat(output.getDraw()).isEqualTo(0);
        assertThat(output.getData().size()).isEqualTo(0);
        assertThat(output.getRecordsTotal()).isEqualTo(0);
    }

}