package org.intraportal.api.exception;

public class DataObjectValidationException extends WebApiException {

    private String field;

    public DataObjectValidationException(String message) {
        super(message);
        field = null;
    }

    public DataObjectValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }

}
