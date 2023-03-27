package org.intraportal.webtool.configuration;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component("WebErrorViewResolver")
public class WebErrorViewResolver implements ErrorViewResolver {

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        var originalPath = request.getAttribute(RequestDispatcher.FORWARD_SERVLET_PATH).toString();

        if (!originalPath.startsWith("/web/")) {
            return null;
        }

        switch (status) {
            case NOT_FOUND:
                ModelAndView notFoundView = new ModelAndView();
                notFoundView.addObject("error", "Page does not exist");
                notFoundView.setViewName("templates/errors/not_found.html");
                return notFoundView;
            case METHOD_NOT_ALLOWED:
                ModelAndView methodNotAllowed = new ModelAndView();
                methodNotAllowed.addObject("error", "Method not allowed");
                methodNotAllowed.setViewName("templates/errors/method_not_allowed.html");
                return methodNotAllowed;
            case FORBIDDEN:
            case UNAUTHORIZED:
            default:
                return new ModelAndView("redirect:/web/login");
        }
    }

}
