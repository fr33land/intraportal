package org.intraportal.webtool.controller.admin;

import org.intraportal.api.service.validation.UsersValidationService;
import org.intraportal.api.model.mappers.UserMapper;
import org.intraportal.api.service.audit.AuditLogService;
import org.intraportal.api.service.users.UsersService;
import org.intraportal.persistence.domain.ValidationResult;
import org.intraportal.persistence.domain.audit.ActionLogDto;
import org.intraportal.persistence.domain.audit.SessionLogDto;
import org.intraportal.persistence.domain.user.PasswordDto;
import org.intraportal.persistence.domain.user.RoleDto;
import org.intraportal.persistence.domain.user.RolesDto;
import org.intraportal.persistence.domain.user.UserDto;
import org.intraportal.persistence.model.user.Role;
import org.intraportal.persistence.model.user.User;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller("UsersController")
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/web/admin/users")
public class UsersController {

    public static final String REDIRECT_URL = "redirect:/web/admin/users";

    private final UsersService usersService;
    private final UsersValidationService usersValidationService;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    private static final String USER_ATTRIBUTE = "user";
    private static final String PASSWORD_ATTRIBUTE = "password";
    private static final String ROLES_ATTRIBUTE = "roles";
    private static final String MESSAGE_ATTRIBUTE = "message";
    private static final String ERROR_ATTRIBUTE = "error";
    private static final String EDIT_ACTION = "edit";
    private static final String CREATE_ACTION = "create";
    private static final String TAB = "tab";
    private static final String ERROR_PAGE = "templates/error.html";
    private static final String EDIT_FORM = "templates/admin/users/edit.html";
    private static final String CREATE_FORM = "templates/admin/users/create.html";
    private static final String REDIRECT_PAGE = "redirect:/web/admin/users/edit/";
    private static final String REDIRECT_PAGE_NEW = "redirect:/web/admin/users/new/";


    public UsersController(UsersService usersService, UsersValidationService usersValidationService, PasswordEncoder passwordEncoder, AuditLogService auditLogService) {
        this.usersService = usersService;
        this.usersValidationService = usersValidationService;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ModelAndView loadUsersPage() {
        return new ModelAndView("templates/admin/users/overview.html");
    }

    @ResponseBody
    @PostMapping(value = "/list")
    public DataTablesOutput<UserDto> getUsersList(@RequestBody DataTablesInput input) {
        return usersService.findAll(input);
    }

    @GetMapping("/edit/{userId}")
    public ModelAndView editUserPage(@PathVariable("userId") Integer userId, ModelMap mm) {
        try {
            if (!mm.containsAttribute(USER_ATTRIBUTE)) {
                this.initUserForm(mm, userId);
            }

            ValidationResult validationResults = usersValidationService.validateUserCanEdit(userId);
            if(validationResults.isNotValid()) {
                mm.addAttribute(ERROR_ATTRIBUTE, validationResults.getValidationMessage());
                return new ModelAndView(ERROR_PAGE);
            }

            if (!mm.containsAttribute(PASSWORD_ATTRIBUTE)) {
                this.initPasswordForm(mm, userId);
            }

            if (!mm.containsAttribute(ROLES_ATTRIBUTE)) {
                this.initRolesForm(mm, userId);
            }
        } catch (UsernameNotFoundException ex) {
            mm.addAttribute(ERROR_ATTRIBUTE, ex.getMessage());
            return new ModelAndView(ERROR_PAGE);
        }

        if (mm.getAttribute(TAB) == null) {
            mm.addAttribute(TAB, "user-info");
        }

        mm.addAttribute(EDIT_ACTION, Boolean.TRUE);
        return new ModelAndView(EDIT_FORM);
    }

    @PostMapping(value = "/save", params = "save")
    public ModelAndView saveUser(@Valid @ModelAttribute("user") UserDto user, final BindingResult bindingResult, RedirectAttributes ra, ModelMap mm) {
        User u = UserMapper.userDtoToUser(user, Boolean.TRUE);

        this.initPasswordForm(mm, user.getId());
        this.initRolesForm(mm, user.getId());
        mm.addAttribute(EDIT_ACTION, Boolean.TRUE);
        mm.addAttribute(TAB, "user-info");

        ValidationResult validationResult = usersValidationService.validateUserCanEdit(user.getId());

        if (validationResult.isNotValid()) {
            bindingResult.reject(null, validationResult.getValidationMessage());
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE, bindingResult);
            ra.addFlashAttribute(USER_ATTRIBUTE, user);
            return new ModelAndView(REDIRECT_PAGE + user.getId());
        }

        if (bindingResult.hasErrors()) {
            bindingResult.reject(null, "Form has errors");
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE, bindingResult);
            ra.addFlashAttribute(USER_ATTRIBUTE, user);
            return new ModelAndView(REDIRECT_PAGE + user.getId());
        }

        validationResult = usersValidationService.validateEditUser(user);

        if (validationResult.isNotValid()) {
            bindingResult.reject(null, validationResult.getValidationMessage());
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE, bindingResult);
            ra.addFlashAttribute(USER_ATTRIBUTE, user);
            return new ModelAndView(REDIRECT_PAGE + user.getId());
        }

        try {
            usersService.updateUser(u);
            ra.addFlashAttribute(MESSAGE_ATTRIBUTE, "User successfully updated");
            return new ModelAndView(REDIRECT_URL);
        } catch (UsernameNotFoundException ex) {
            bindingResult.reject(null, ex.getMessage());
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE, bindingResult);
            ra.addFlashAttribute(USER_ATTRIBUTE, user);
            return new ModelAndView(REDIRECT_PAGE + user.getId());
        } catch (IllegalArgumentException ex) {
            bindingResult.reject(null, ex.getMessage());
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE, bindingResult);
            ra.addFlashAttribute(USER_ATTRIBUTE, user);
            return new ModelAndView(REDIRECT_PAGE + user.getId());
        }
    }

    @GetMapping("/new")
    public ModelAndView newUserPage(ModelMap mm) {
        if (!mm.containsAttribute(USER_ATTRIBUTE)) {
            mm.addAttribute(USER_ATTRIBUTE, new UserDto());
        }
        mm.addAttribute(CREATE_ACTION, Boolean.TRUE);
        return new ModelAndView(CREATE_FORM);
    }

    @PostMapping(value = "/save", params = "create")
    public ModelAndView createUser(@Valid @ModelAttribute("user") UserDto user, final BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            bindingResult.reject(null, "Form has errors");
            ra.addFlashAttribute(CREATE_ACTION, Boolean.TRUE);
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE, bindingResult);
            ra.addFlashAttribute(USER_ATTRIBUTE, user);
            return new ModelAndView(REDIRECT_PAGE_NEW);
        }

        ValidationResult validationResult = usersValidationService.validateCreateUser(user);

        if (validationResult.isNotValid()) {
            bindingResult.reject(null, validationResult.getValidationMessage());
            ra.addFlashAttribute(CREATE_ACTION, Boolean.TRUE);
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE, bindingResult);
            ra.addFlashAttribute(USER_ATTRIBUTE, user);
            return new ModelAndView(REDIRECT_PAGE_NEW);
        }

        User u = UserMapper.userDtoToUser(user, Boolean.FALSE);
        u.setPassword(passwordEncoder.encode(user.getPasswordFirst()));

        try {
            usersService.createUser(u);
            ra.addFlashAttribute(MESSAGE_ATTRIBUTE, "User successfully created");
            return new ModelAndView(REDIRECT_URL);
        } catch (Exception ex) {
            bindingResult.reject(null, ex.getMessage());
            ra.addFlashAttribute(CREATE_ACTION, Boolean.TRUE);
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE, bindingResult);
            ra.addFlashAttribute(USER_ATTRIBUTE, user);
            return new ModelAndView(REDIRECT_PAGE_NEW);
        }
    }

    @PostMapping(value = "/save", params = "delete")
    public ModelAndView deleteUser(@Valid @ModelAttribute("user") UserDto user, final BindingResult bindingResult, RedirectAttributes ra) {
        try {
            User u = usersService.findByUserId(user.getId());
            ValidationResult validationResult = usersValidationService.validateUserCanEdit(user.getId());

            if (validationResult.isNotValid()) {
                bindingResult.reject(null, validationResult.getValidationMessage());
                ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE, bindingResult);
                return new ModelAndView(REDIRECT_PAGE + user.getId());
            }

            usersService.deleteUser(user.getId());
            ra.addFlashAttribute(MESSAGE_ATTRIBUTE, "User successfully removed");
            return new ModelAndView(REDIRECT_URL);
        } catch (UsernameNotFoundException ex) {
            ra.addFlashAttribute(ERROR_ATTRIBUTE, "User not found");
            return new ModelAndView(REDIRECT_URL);
        }
    }

    @PostMapping(value = "/passwd/change", params = "change")
    public ModelAndView changeUserPassword(@Valid @ModelAttribute("password") PasswordDto password, final BindingResult bindingResult, ModelMap mm, RedirectAttributes ra) {
        this.initUserForm(mm, password.getUserId());
        this.initRolesForm(mm, password.getUserId());
        ra.addFlashAttribute(EDIT_ACTION, Boolean.TRUE);
        ra.addFlashAttribute(TAB, "user-password");
        UserDto u = (UserDto) mm.getAttribute(USER_ATTRIBUTE);

        ValidationResult validationResult = usersValidationService.validateUserCanEdit(password.getUserId());

        if (validationResult.isNotValid()) {
            bindingResult.reject(null, validationResult.getValidationMessage());
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + PASSWORD_ATTRIBUTE, bindingResult);
            return new ModelAndView(REDIRECT_PAGE + password.getUserId());
        }

        if (bindingResult.hasErrors()) {
            bindingResult.reject(null, "Form has errors");
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + PASSWORD_ATTRIBUTE, bindingResult);
            return new ModelAndView(REDIRECT_PAGE + password.getUserId());
        }

        validationResult = usersValidationService.validateUpdateUserPassword(password);

        if (validationResult.isNotValid()) {
            bindingResult.reject(null, validationResult.getValidationMessage());
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + PASSWORD_ATTRIBUTE, bindingResult);
            return new ModelAndView(REDIRECT_PAGE + password.getUserId());
        }

        try {
            usersService.updateUserPassword(password.getUserId(), passwordEncoder.encode(password.getPasswordFirst()));
            ra.addFlashAttribute("passwordMessage", "User password updated.");
            ra.addFlashAttribute(TAB, "user-password");
        } catch (UsernameNotFoundException ex) {
            bindingResult.reject(null, ex.getMessage());
        }

        return new ModelAndView(REDIRECT_PAGE + password.getUserId());
    }

    @PostMapping(value = "/roles/update", params = "update")
    public ModelAndView changeUserRoles(@Valid @ModelAttribute("roles") RolesDto roles, final BindingResult bindingResult, ModelMap mm, RedirectAttributes ra) {
        this.initUserForm(mm, roles.getUserId());
        this.initPasswordForm(mm, roles.getUserId());
        ra.addFlashAttribute(EDIT_ACTION, Boolean.TRUE);
        ra.addFlashAttribute(TAB, "user-roles");
        UserDto u = (UserDto) mm.getAttribute(USER_ATTRIBUTE);

        ValidationResult validationResult = usersValidationService.validateUserCanEdit(roles.getUserId());

        if (validationResult.isNotValid()) {
            bindingResult.reject(null, validationResult.getValidationMessage());
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + PASSWORD_ATTRIBUTE, bindingResult);
            return new ModelAndView(REDIRECT_PAGE + roles.getUserId());
        }

        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute(ROLES_ATTRIBUTE, roles);
            bindingResult.reject(null, "Form has errors");
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + PASSWORD_ATTRIBUTE, bindingResult);
            return new ModelAndView(REDIRECT_PAGE + roles.getUserId());
        }

        try {
            usersService.updateUserRoles(roles.getUserId(), roles.getList());
            ra.addFlashAttribute("rolesMessage", "User roles updated.");
        } catch (UsernameNotFoundException ex) {
            mm.addAttribute(ROLES_ATTRIBUTE, roles);
            bindingResult.reject(null, ex.getMessage());
        }

        return new ModelAndView(REDIRECT_PAGE + roles.getUserId());
    }

    @PostMapping(value = "/save", params = "cancel")
    public ModelAndView cancelSaveUser(SessionStatus ss) {
        ss.setComplete();
        return new ModelAndView(REDIRECT_URL);
    }

    @ResponseBody
    @PostMapping(value = "/audit/action/{userId}")
    public DataTablesOutput<ActionLogDto> auditUserAction(@PathVariable("userId") Integer userId, @RequestBody DataTablesInput input) {
        try {
            User u = usersService.findByUserId(userId);
            return auditLogService.getUserActionLogData(u.getUsername(), input);
        } catch (UsernameNotFoundException ex) {
            return new DataTablesOutput<>();
        }
    }

    @ResponseBody
    @PostMapping(value = "/audit/session/{userId}")
    public DataTablesOutput<SessionLogDto> auditUserSession(@PathVariable("userId") Integer userId, @RequestBody DataTablesInput input) {
        try {
            User u = usersService.findByUserId(userId);
            return auditLogService.getUserSessionLogData(u.getUsername(), input);
        } catch (UsernameNotFoundException ex) {
            return new DataTablesOutput<>();
        }
    }

    protected void initUserForm(ModelMap mm, Integer userId) {
        User user = usersService.findByUserId(userId);
        UserDto u = UserMapper.userToUserDto(user, Boolean.TRUE);

        mm.addAttribute(USER_ATTRIBUTE, u);
    }

    protected void initPasswordForm(ModelMap mm, Integer userId) {
        PasswordDto p = new PasswordDto();
        p.setUserId(userId);
        mm.addAttribute(PASSWORD_ATTRIBUTE, p);
    }

    protected void initRolesForm(ModelMap mm, Integer userId) {
        User user = usersService.findByUserId(userId);
        Collection<Role> userRoles = user.getRoles();
        List<Role> availableRoles = usersService.getAvailableRoles();

        List<RoleDto> r = availableRoles.stream().map(ar -> {
            if (userRoles.contains(ar)) {
                return new RoleDto(ar.getId(), ar.getName(), ar.getDescription(), true);
            } else {
                return new RoleDto(ar.getId(), ar.getName(), ar.getDescription(), false);
            }
        }).collect(Collectors.toList());

        RolesDto rl = new RolesDto();
        rl.setUserId(userId);
        rl.setList(r);

        mm.addAttribute(ROLES_ATTRIBUTE, rl);
    }

}
