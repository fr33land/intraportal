package org.intraportal.webtool.security.filters;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class MdcFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "traceId";
    private static final String REQUEST_ID = "X-Request-ID";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws java.io.IOException, ServletException {
        try {
            final String token;
            if (!StringUtils.isEmpty(REQUEST_ID) && !StringUtils.isEmpty(request.getHeader(REQUEST_ID))) {
                token = request.getHeader(REQUEST_ID);
            } else {
                token = UUID.randomUUID().toString();
            }
            MDC.put(TRACE_ID, token);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID);
        }
    }
}