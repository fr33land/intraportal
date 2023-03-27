package org.intraportal.persistence.domain;

public class ValidationResult {

    private final boolean valid;

    private final String errorMessage;

    public static ValidationResult valid() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult inValid(String errorMessage) {
        return new ValidationResult(false, errorMessage);
    }

    private ValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    public boolean isNotValid() {
        return !this.valid;
    }

    public String getValidationMessage() {
        return this.errorMessage;
    }
}
