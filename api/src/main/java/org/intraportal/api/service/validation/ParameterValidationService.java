package org.intraportal.api.service.validation;

import org.intraportal.persistence.domain.ValidationResult;
import org.intraportal.persistence.domain.server.MailSettings;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;


@Service("ParameterValidationService")
public class ParameterValidationService {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    public ValidationResult validateSmtpServerSettings(MailSettings mailSettings) {
        Set<ConstraintViolation<MailSettings>> violations = validator.validate(mailSettings);
        if (!violations.isEmpty()) {
            ConstraintViolation<MailSettings> violation = violations.stream().findFirst().get();
            return ValidationResult.inValid(violation.getMessage());
        }
        
        return ValidationResult.valid();
    }
}
