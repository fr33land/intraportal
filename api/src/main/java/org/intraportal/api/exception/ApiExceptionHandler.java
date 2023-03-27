package org.intraportal.api.exception;


import java.util.function.Function;

public class ApiExceptionHandler {

    public static Function<Exception, Exception> handleProcessException() {
        return r -> new ApiCommandException(r.getMessage());
    }

    public static Function<Exception, Exception> handleIOException() {
        return r -> new ApiServerIOException(r.getMessage());
    }

}
