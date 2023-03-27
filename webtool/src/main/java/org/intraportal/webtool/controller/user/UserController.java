package org.intraportal.webtool.controller.user;

import org.intraportal.api.model.mappers.UserMapper;
import org.intraportal.api.service.audit.AuditLogService;
import org.intraportal.api.service.users.UsersService;
import org.intraportal.api.service.validation.UsersValidationService;
import org.intraportal.persistence.domain.ValidationResult;
import org.intraportal.persistence.domain.audit.ActionLogDto;
import org.intraportal.persistence.domain.audit.SessionLogDto;
import org.intraportal.persistence.domain.user.PasswordDto;
import org.intraportal.persistence.domain.user.UserDto;
import org.intraportal.persistence.model.user.User;
import org.intraportal.webtool.security.core.IntraPortalUserDetails;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller("UserController")
@SessionAttributes("back")
@PreAuthorize("hasAnyAuthority('ADMIN', 'VIEWER')")
@RequestMapping("/web/user")
public class UserController {

    private static final String BACK_REF = "backReferer";
    public static final String REDIRECT_SETTINGS_PAGE = "redirect:/web/user/settings";
    private final UsersService usersService;
    private final UsersValidationService usersValidationService;

    private final AuditLogService auditLogService;
    private final PasswordEncoder passwordEncoder;

    private static final String USER_ATTRIBUTE = "user";
    private static final String PASSWORD_ATTRIBUTE = "password";

    private static final String TAB = "tab";
    private static final String BACK = "back";
    private static final String MESSAGE_ATTRIBUTE = "userMessage";
    public static final String USER_SETTINGS_FORM = "templates/user/settings.html";
    public static final String VALIDATION_SELF_EDITING_MESSAGE = "Editing self information is prohibited.";


    public UserController(UsersService usersService, UsersValidationService usersValidationService, AuditLogService auditLogService, PasswordEncoder passwordEncoder) {
        this.usersService = usersService;
        this.usersValidationService = usersValidationService;
        this.auditLogService = auditLogService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(value = "/settings")
    public ModelAndView loadUserSettingsPage(@RequestParam(name = "ref", required = false) String ref, ModelMap mm) {
        IntraPortalUserDetails principle = (IntraPortalUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDto u;

        if (!mm.containsAttribute(USER_ATTRIBUTE)) {
            User user = usersService.findByUserName(principle.getUsername());
            u = UserMapper.userToUserDto(user, Boolean.TRUE);
            mm.addAttribute(USER_ATTRIBUTE, u);
        } else {
            u = (UserDto) mm.getAttribute(USER_ATTRIBUTE);
        }

        PasswordDto p = new PasswordDto();
        p.setUserId(u.getId());

        mm.addAttribute(PASSWORD_ATTRIBUTE, p);
        mm.addAttribute(BACK, "false");

        if (principle.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            if (ref != null && ref.equals("ul")) {
                mm.addAttribute(BACK, "true");
            }
        }

        if (mm.getAttribute(TAB) == null) {
            mm.addAttribute(TAB, "user-info");
        }

        return new ModelAndView(USER_SETTINGS_FORM);
    }

    @PostMapping(value = "/settings/save", params = "save")
    public ModelAndView saveUserSettings(@Valid @ModelAttribute("user") UserDto user, final BindingResult bindingResult, @ModelAttribute("back") String back, ModelMap mm, RedirectAttributes ra) {
        IntraPortalUserDetails principle = (IntraPortalUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setId(principle.getUserId());
        user.setUsername(principle.getUsername());

        User u = UserMapper.userDtoToUser(user, Boolean.TRUE);

        PasswordDto p = new PasswordDto();
        p.setUserId(u.getId());

        mm.addAttribute(PASSWORD_ATTRIBUTE, p);
        mm.addAttribute(TAB, "user-info");

        addBackSupport(back, mm, ra, principle);

        if (bindingResult.hasErrors()) {
            bindingResult.reject(null, "Form has errors");
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE, bindingResult);
            ra.addFlashAttribute(USER_ATTRIBUTE, user);
            return new ModelAndView(REDIRECT_SETTINGS_PAGE + mm.getAttribute(BACK_REF));
        }

        ValidationResult validationResult = usersValidationService.validateEditUser(user);

        if (validationResult.isNotValid() && !validationResult.getValidationMessage().startsWith(VALIDATION_SELF_EDITING_MESSAGE)) {
            bindingResult.reject(null, validationResult.getValidationMessage());
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE, bindingResult);
            ra.addFlashAttribute(USER_ATTRIBUTE, user);
            return new ModelAndView(REDIRECT_SETTINGS_PAGE + mm.getAttribute(BACK_REF));
        }

        try {
            usersService.updateUser(u);
            ra.addFlashAttribute(MESSAGE_ATTRIBUTE, "Settings successfully updated");
            return new ModelAndView(REDIRECT_SETTINGS_PAGE + mm.getAttribute(BACK_REF));
        } catch (UsernameNotFoundException ex) {
            mm.addAttribute(USER_ATTRIBUTE, user);
            bindingResult.reject(null, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            mm.addAttribute(USER_ATTRIBUTE, user);
            bindingResult.reject(null, ex.getMessage());
        }

        return new ModelAndView(USER_SETTINGS_FORM, mm);
    }

    @PostMapping(value = "/settings/passwd", params = "change")
    public ModelAndView saveUserPassword(@Valid @ModelAttribute("password") PasswordDto password, final BindingResult bindingResult, @ModelAttribute("back") String back, ModelMap mm, RedirectAttributes ra) {
        IntraPortalUserDetails principle = (IntraPortalUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        password.setUserId(principle.getUserId());

        User user = usersService.findByUserName(principle.getUsername());
        UserDto u = UserMapper.userToUserDto(user, Boolean.TRUE);
        mm.addAttribute(USER_ATTRIBUTE, u);
        mm.addAttribute(TAB, "user-password");

        addBackSupport(back, mm, ra, principle);

        if (bindingResult.hasErrors()) {
            bindingResult.reject(null, "Form has errors");
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE, bindingResult);
            ra.addFlashAttribute(USER_ATTRIBUTE, user);
            return new ModelAndView(REDIRECT_SETTINGS_PAGE + mm.getAttribute(BACK_REF));
        }

        ValidationResult validationResult = usersValidationService.validateUpdateUserPassword(password);

        if (validationResult.isNotValid()) {
            bindingResult.reject(null, validationResult.getValidationMessage());
            ra.addFlashAttribute(BindingResult.class.getCanonicalName() + "." + USER_ATTRIBUTE, bindingResult);
            return new ModelAndView(REDIRECT_SETTINGS_PAGE + mm.getAttribute(BACK_REF));
        }

        try {
            usersService.updateUserPassword(password.getUserId(), passwordEncoder.encode(password.getPasswordFirst()));
            ra.addFlashAttribute("passwordMessage", "Password updated. API tokens invalidated.");
            ra.addFlashAttribute(TAB, "user-password");
            return new ModelAndView(REDIRECT_SETTINGS_PAGE + mm.getAttribute(BACK_REF));
        } catch (UsernameNotFoundException ex) {
            bindingResult.reject(null, ex.getMessage());
        }

        return new ModelAndView(USER_SETTINGS_FORM);
    }

    @ResponseBody
    @PostMapping(value = "/audit/action")
    public DataTablesOutput<ActionLogDto> auditUserAction(@RequestBody DataTablesInput input) {
        try {
            IntraPortalUserDetails principle = (IntraPortalUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User u = usersService.findByUserId(principle.getUserId());
            return auditLogService.getUserActionLogData(u.getUsername(), input);
        } catch (UsernameNotFoundException ex) {
            return new DataTablesOutput<>();
        }
    }

    @ResponseBody
    @PostMapping(value = "/audit/session")
    public DataTablesOutput<SessionLogDto> auditUserSession(@RequestBody DataTablesInput input) {
        try {
            IntraPortalUserDetails principle = (IntraPortalUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User u = usersService.findByUserId(principle.getUserId());
            return auditLogService.getUserSessionLogData(u.getUsername(), input);
        } catch (UsernameNotFoundException ex) {
            return new DataTablesOutput<>();
        }
    }

    private void addBackSupport(@ModelAttribute("back") String back, ModelMap mm, RedirectAttributes ra, IntraPortalUserDetails principle) {
        mm.addAttribute(BACK, "false");
        mm.addAttribute(BACK_REF, "");
        if (principle.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            if (back.equals("true")) {
                mm.addAttribute(BACK, back);
                mm.addAttribute(BACK_REF, "?ref=ul");
            }
        }
    }
}
