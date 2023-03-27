package org.intraportal.api.service.validation;

import org.intraportal.api.service.users.SecurityContextUserService;
import org.intraportal.persistence.domain.ValidationResult;
import org.intraportal.persistence.domain.user.PasswordDto;
import org.intraportal.persistence.domain.user.UserDto;
import org.intraportal.persistence.model.user.User;
import org.intraportal.persistence.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("UsersValidationService")
public class UsersValidationService {

    private final UserRepository userRepository;

    private final SecurityContextUserService contextUserService;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_USERNAME_REGEX = Pattern.compile("^[a-zA-Z0-9]*$", Pattern.CASE_INSENSITIVE);

    @Autowired
    public UsersValidationService(UserRepository userRepository, SecurityContextUserService contextUserService) {
        this.userRepository = userRepository;
        this.contextUserService = contextUserService;
    }

    public ValidationResult validateCreateUser(UserDto user) {

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()){
            return ValidationResult.inValid("Username is missing");
        }

        if(user.getUsername().trim().contains(" ")) {
            return ValidationResult.inValid("Username contains whitespaces");
        }

        if(user.getUsername().length() < 3) {
            return ValidationResult.inValid("Username must be at least 3 characters long");
        }

        Matcher matcher = VALID_USERNAME_REGEX.matcher(user.getUsername());
        if(!matcher.find()) {
            return ValidationResult.inValid("Username must contain only alphanumeric characters");
        }

        if(userRepository.findByUsername(user.getUsername().toLowerCase()) != null) {
            return ValidationResult.inValid("Username already exists");
        }

        matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(user.getEmail());
        if(!matcher.find()) {
            return ValidationResult.inValid("Incorrect email format");
        }

        if(userRepository.findByEmail(user.getEmail().toLowerCase()) != null) {
            return ValidationResult.inValid("Email already used");
        }

        if(user.getPasswordFirst() == null || user.getPasswordFirst().equals("")) {
            return ValidationResult.inValid("Password is empty");
        }

        if(!user.getPasswordFirst().equals(user.getPasswordSecond())) {
            return ValidationResult.inValid("Passwords does not match");
        }

        return  ValidationResult.valid();
    }

    public ValidationResult validateEditUser(UserDto user) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(user.getEmail());
        if(!matcher.find()) {
            return ValidationResult.inValid("Incorrect email format");
        }

        User u = userRepository.findByEmail(user.getEmail().toLowerCase());
        if(u != null && !u.getUsername().equals(user.getUsername())) {
            return ValidationResult.inValid("Email already used");
        }

        return validateUserCanEdit(user.getId());
    }

    public ValidationResult validateUpdateUserPassword(PasswordDto password) {
        if(password.getPasswordFirst() == null || password.getPasswordFirst().equals("")) {
            return ValidationResult.inValid("Password is empty");
        }

        if(!password.getPasswordFirst().equals(password.getPasswordSecond())) {
            return ValidationResult.inValid("Passwords does not match");
        }

        return  ValidationResult.valid();
    }

    public ValidationResult validateUserCanEdit(Integer userId) {
        Optional<User> user = userRepository.findById(userId);

        if(!user.isPresent()) {
            return ValidationResult.inValid("User not found");
        }

        String username = user.map(User::getUsername).orElse("");

        if(contextUserService.getSessionUsername().equals(username)) {
            return ValidationResult.inValid("Editing self information is prohibited. Change user data via <a href=\"/web/user/settings\">settings</a> page");
        }

        if(username.equals("admin") && !contextUserService.getSessionUsername().equals("admin")) {
            return ValidationResult.inValid("User cannot be updated");
        }

        return  ValidationResult.valid();
    }
}
