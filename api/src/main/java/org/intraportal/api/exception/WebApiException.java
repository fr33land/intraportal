package org.intraportal.api.exception;

public abstract class WebApiException extends Exception {

    public WebApiException(String message) {
        super(message);
    }

}
