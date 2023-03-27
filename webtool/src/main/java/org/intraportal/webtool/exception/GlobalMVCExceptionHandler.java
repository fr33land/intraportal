package org.intraportal.webtool.exception;

import org.intraportal.api.exception.ErrorResponse;
import org.intraportal.api.exception.WebApiException;
import org.intraportal.api.exception.WebApiSystemException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE;

@ControllerAdvice
public class GlobalMVCExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalMVCExceptionHandler.class);

    public static final String DEFAULT_ERROR_VIEW = "templates/error.html";
    public static final String ACCESS_DENIED_ERROR_VIEW = "templates/errors/access_denied.html";
    public static final String ERROR = "error";
    private static final String TRACE_ID = "traceId";
    private static final String LOG_FORMAT = "class: [{}] method: [{}] traceId: [{}] exception: [{}] message: {}";

    private static final long MEGABYTE = 1024L * 1024L;

    @ExceptionHandler(value = {NumberFormatException.class, MethodArgumentTypeMismatchException.class})
    public ModelAndView numberFormatException(Exception ex, HandlerMethod handlerMethod) {
        LOGGER.error(LOG_FORMAT, handlerMethod.getMethod().getDeclaringClass().getName(), handlerMethod.getMethod().getName(), MDC.get(TRACE_ID), ex.getClass().getSimpleName(), ex.getMessage());

        ModelAndView mav = new ModelAndView();
        mav.addObject(ERROR, "Bad request");
        mav.addObject(TRACE_ID, MDC.get(TRACE_ID));
        mav.setViewName(DEFAULT_ERROR_VIEW);

        return mav;
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView accessDeniedHandler(Exception ex, HandlerMethod handlerMethod) {
        LOGGER.error(LOG_FORMAT, handlerMethod.getMethod().getDeclaringClass().getName(), handlerMethod.getMethod().getName(), MDC.get(TRACE_ID), ex.getClass().getSimpleName(), ex.getMessage());

        ModelAndView noAccessView = new ModelAndView();
        noAccessView.addObject(ERROR, "You don't have access to view this page");
        noAccessView.setViewName(ACCESS_DENIED_ERROR_VIEW);

        return noAccessView;
    }

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> fileSizeLimitExceededException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        SizeLimitExceededException rootEx = (SizeLimitExceededException) ex.getRootCause();
        long permittedMb = rootEx.getPermittedSize() / MEGABYTE;
        long actualMb = rootEx.getActualSize() / MEGABYTE;
        LOGGER.error(LOG_FORMAT, "fileUpload", "fileUpload", MDC.get(TRACE_ID), ex.getClass().getSimpleName(), "Maximum  (" + permittedMb + " MB) file upload size exceeded. Actual size " + actualMb + " MB");
        return ResponseEntity
                .status(PAYLOAD_TOO_LARGE)
                .body(createErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE, request.getRequestURI(), "Maximum  (" + permittedMb + " MB) file upload size exceeded."));
    }

    @ExceptionHandler(value = WebApiException.class)
    public ResponseEntity<ErrorResponse> webApiErrorHandler(WebApiException apiException, HttpServletRequest request, HandlerMethod handlerMethod) {
        LOGGER.error("WebApiException: {}", apiException.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse(HttpStatus.BAD_REQUEST, request.getRequestURI(), apiException.getMessage()));
    }

    @ExceptionHandler(value = WebApiSystemException.class)
    public ResponseEntity<ErrorResponse> webApiSystemErrorHandler(WebApiSystemException apiSystemException, HttpServletRequest request, HandlerMethod handlerMethod) {
        LOGGER.error("WebApiSystemException: {}", apiSystemException.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI(), apiSystemException.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(Exception ex, HandlerMethod handlerMethod) {
        LOGGER.error(LOG_FORMAT, handlerMethod.getMethod().getDeclaringClass().getName(), handlerMethod.getMethod().getName(), MDC.get(TRACE_ID), ex.getClass().getSimpleName(), ex.getMessage());

        ModelAndView mav = new ModelAndView();
        mav.addObject(ERROR, ex.getMessage());
        mav.addObject(TRACE_ID, MDC.get(TRACE_ID));
        mav.setViewName(DEFAULT_ERROR_VIEW);

        return mav;
    }

    private ErrorResponse createErrorResponse(HttpStatus status, String path, String message) {
        return new ErrorResponse()
                .timestamp(LocalDateTime.now())
                .status(Integer.toString(status.value()))
                .error(status.getReasonPhrase())
                .path(path)
                .message(message);
    }

}