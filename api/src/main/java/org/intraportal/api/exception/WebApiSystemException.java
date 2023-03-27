package org.intraportal.api.exception;

public abstract class WebApiSystemException extends Exception {

    public WebApiSystemException(String message) {
        super(message);
    }

}
