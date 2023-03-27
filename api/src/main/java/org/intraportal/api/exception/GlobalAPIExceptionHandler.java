package org.intraportal.api.exception;

import org.intraportal.api.exception.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice("GlobalAPIExceptionHandler")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalAPIExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalAPIExceptionHandler.class);

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<String> badCredentialsException(HttpServletRequest request, BadCredentialsException e) {
        LOGGER.error("Bad credentials: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong credentials. Please contact administrator.");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = UserAccessForbiddenException.class)
    public ResponseEntity<ErrorResponse> userAccessForbiddenException(HttpServletRequest request, UserAccessForbiddenException e) {
        LOGGER.error("User access forbidden: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse(HttpStatus.FORBIDDEN, request.getRequestURI(), e.getMessage()));
    }


    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> constraintViolationException(HttpServletRequest request, ConstraintViolationException e) {
        LOGGER.error("Invalid constraint: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(HttpStatus.BAD_REQUEST, request.getRequestURI(), "Invalid constraint"));
    }

    @ExceptionHandler(value = DateValidationException.class)
    public ResponseEntity<ErrorResponse> dateValidationException(HttpServletRequest request, DateValidationException e) {
        LOGGER.error("Invalid date: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(HttpStatus.BAD_REQUEST, request.getRequestURI(), e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        LOGGER.error("Validation errors found: {}", ex.getMessage());

        var singleErrorMessageBuilder = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            singleErrorMessageBuilder
                    .append(String.format("field \"%s\" is not valid: ", fieldName))
                    .append(error.getDefaultMessage())
                    .append("; ");
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(HttpStatus.BAD_REQUEST, null, singleErrorMessageBuilder.toString()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ValidationResultException.class})
    public ResponseEntity<ErrorResponse> validationException(HttpServletRequest request, ValidationResultException validationException) {
        LOGGER.error("API validation error. [message={}]", validationException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(HttpStatus.BAD_REQUEST, request.getRequestURI(), validationException.getMessage()));
    }

    @ExceptionHandler(value = SQLException.class)
    public ResponseEntity<ErrorResponse> databaseException(HttpServletRequest request, SQLException ex) {
        LOGGER.error("Database Exception: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse(HttpStatus.BAD_REQUEST, request.getRequestURI(), ex.getMessage()));
    }

    private ErrorResponse createErrorResponse(HttpStatus status, String path, String message) {
        return new ErrorResponse()
                .timestamp(LocalDateTime.now())
                .status(Integer.toString(status.value()))
                .error(status.getReasonPhrase())
                .path(path)
                .message(message);
    }

    private StackTraceElement getStackTraceElement(Exception ex) {
        return Arrays.stream(ex.getStackTrace())
                .filter(e -> e.getClassName().startsWith("org.intraportal"))
                .findFirst()
                .orElseGet(() -> ex.getStackTrace()[0]);
    }
}
