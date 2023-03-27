package org.intraportal.webtool.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.intraportal.api.service.users.SecurityContextUserService;
import org.intraportal.api.service.audit.AuditLogService;
import org.intraportal.api.service.users.UsersService;
import org.intraportal.api.service.validation.UsersValidationService;
import org.intraportal.persistence.domain.ValidationResult;
import org.intraportal.persistence.domain.user.PasswordDto;
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
@WebMvcTest(UserController.class)
public class UserControllerTest {

    private static final String BACK_REF = "backReferer";
    private static final String TAB = "tab";
    private static final String USER_ATTRIBUTE = "user";
    private static final String EXISTING_USER_NAME = "elsi";
    public static final String PASSWORD_ATTRIBUTE = "password";
    private static final String MESSAGE_ATTRIBUTE = "userMessage";
    public static final String PASSWORD_MESSAGE = "passwordMessage";
    public static final String USER_SETTINGS_FORM = "templates/user/settings.html";
    private static final String USER_NOT_FOUND_MESSAGE = "User with provided id not found";
    public static final String WEB_USER_SETTINGS_REDIRECT = "/web/user/settings";

    private MockMvc mockMvc;

    @Mock
    private UsersService usersService;

    @Mock
    private UsersValidationService usersValidationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private SecurityContextUserService contextUserService;

    @InjectMocks
    private UserController userController;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        when(contextUserService.getSessionUsername()).thenReturn(EXISTING_USER_NAME);
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
    public void loadUserSettingsPage() throws Exception {
        when(usersService.findByUserName(any())).thenReturn(UserCreator.getUser(1));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(WEB_USER_SETTINGS_REDIRECT))
                .andExpect(status().isOk())
                .andExpect(view().name(USER_SETTINGS_FORM));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
    public void saveUserSettings() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserName(any())).thenReturn(UserCreator.getUser(1));
        when(usersValidationService.validateEditUser(any())).thenReturn(ValidationResult.valid());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/user/settings/save")
                                .param("save", "true")
                                .sessionAttr("back", "true")
                                .flashAttr(BACK_REF, "")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(MESSAGE_ATTRIBUTE, equalTo("Settings successfully updated")))
                .andExpect(redirectedUrl(WEB_USER_SETTINGS_REDIRECT));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
    public void saveUserSettings_withBindingError() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserName(any())).thenReturn(UserCreator.getUser(1));
        when(usersValidationService.validateEditUser(any())).thenReturn(ValidationResult.valid());

        UserDto userDto = UserCreator.getUserDto(userId);
        userDto.setUsername(null);
        userDto.setEmail(null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/user/settings/save")
                                .param("save", "true")
                                .sessionAttr("back", "true")
                                .flashAttr(USER_ATTRIBUTE, userDto))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
    public void saveUserSettings_withValidationError() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserName(any())).thenReturn(UserCreator.getUser(1));
        when(usersValidationService.validateEditUser(any())).thenReturn(ValidationResult.inValid("BAD"));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/user/settings/save")
                                .param("save", "true")
                                .sessionAttr("back", "true")
                                .flashAttr(BACK_REF, "")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(WEB_USER_SETTINGS_REDIRECT))
                .andExpect(flash().attributeExists(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE))
                .andExpect(flash().attributeExists(USER_ATTRIBUTE));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
    public void saveUserSettings_withUserNotFoundException() throws Exception {
        Integer userId = 1;
        when(usersService.findByUserName(any())).thenReturn(UserCreator.getUser(1));
        when(usersValidationService.validateEditUser(any())).thenReturn(ValidationResult.valid());
        doThrow(new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE)).when(usersService).updateUser(any());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/user/settings/save")
                                .param("save", "true")
                                .sessionAttr("back", "true")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId)))
                .andExpect(status().isOk())
                .andExpect(view().name(USER_SETTINGS_FORM))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists(USER_ATTRIBUTE))
                .andExpect(model().attribute(TAB, equalTo("user-info")));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
    public void saveUserPassword() throws Exception {
        Integer userId = 1;
        when(usersValidationService.validateUpdateUserPassword(any())).thenReturn(ValidationResult.valid());
        when(usersService.findByUserName(any())).thenReturn(UserCreator.getUser(userId));
        doNothing().when(usersService).updateUserPassword(any(), any());
        when(passwordEncoder.encode(any())).thenReturn("HASHED");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/user/settings/passwd")
                                .param("change", "true")
                                .sessionAttr("back", "true")
                                .flashAttr(BACK_REF, "")
                                .flashAttr(PASSWORD_ATTRIBUTE, UserCreator.getPasswordDto(userId))
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(WEB_USER_SETTINGS_REDIRECT))
                .andExpect(flash().attribute(PASSWORD_MESSAGE, equalTo("Password updated. API tokens invalidated.")));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
    public void saveUserPassword_withBindingError() throws Exception {
        Integer userId = 1;
        when(usersValidationService.validateUpdateUserPassword(any())).thenReturn(ValidationResult.valid());
        when(usersService.findByUserName(any())).thenReturn(UserCreator.getUser(userId));

        PasswordDto p = UserCreator.getPasswordDto(userId);
        p.setPasswordSecond(null);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/user/settings/passwd")
                                .param("change", "true")
                                .flashAttr(PASSWORD_ATTRIBUTE, p)
                                .sessionAttr("back", "false")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId))
                )
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
    public void saveUserPassword_withValidationError() throws Exception {
        Integer userId = 1;
        when(usersValidationService.validateUpdateUserPassword(any())).thenReturn(ValidationResult.inValid("BAD"));
        when(usersService.findByUserName(any())).thenReturn(UserCreator.getUser(userId));

        PasswordDto p = UserCreator.getPasswordDto(userId);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/user/settings/passwd")
                                .param("change", "true")
                                .sessionAttr("back", "true")
                                .flashAttr(PASSWORD_ATTRIBUTE, p)
                                .flashAttr(BACK_REF, "")
                                .flashAttr(USER_ATTRIBUTE, UserCreator.getUserDto(userId))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE))
                .andExpect(redirectedUrl(WEB_USER_SETTINGS_REDIRECT));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
    public void saveUserPassword_withUsernameNotFoundException() throws Exception {
        Integer userId = 1;
        when(usersValidationService.validateUpdateUserPassword(any())).thenReturn(ValidationResult.valid());
        when(usersService.findByUserName(any())).thenReturn(UserCreator.getUser(userId));
        when(passwordEncoder.encode(any())).thenReturn("HASHED");
        doThrow(new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE)).when(usersService).updateUserPassword(any(), any());

        PasswordDto p = UserCreator.getPasswordDto(userId);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/user/settings/passwd")
                                .param("change", "true")
                                .sessionAttr("back", "true")
                                .flashAttr(PASSWORD_ATTRIBUTE, p)
                )
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name(USER_SETTINGS_FORM))
                .andExpect(model().attribute(TAB, equalTo("user-password")));
    }

    @Test
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
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
                                .post("/web/user/audit/action/")
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
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
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
                                .post("/web/user/audit/action/")
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
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
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
                                .post("/web/user/audit/session/")
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
    @WithMockIntraPortalUserDetails(userId = 1, username = "test", authorities = {"ROLE_ADMIN"})
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
                                .post("/web/user/audit/session/")
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