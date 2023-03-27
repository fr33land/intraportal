package org.intraportal.webtool.configuration;

import org.intraportal.webtool.security.filters.MdcFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("MdcConfiguration")
public class MdcConfiguration {

    public static final String DEFAULT_RESPONSE_TOKEN_HEADER = "responseToken";
    public static final String DEFAULT_MDC_UUID_TOKEN_KEY = "traceId";

    private String responseHeader = DEFAULT_RESPONSE_TOKEN_HEADER;
    private String mdcTokenKey = DEFAULT_MDC_UUID_TOKEN_KEY;
    private String requestHeader = null;

    @Bean
    public FilterRegistrationBean servletRegistrationBean() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        final MdcFilter mdcFilterFilter = new MdcFilter();
        registrationBean.setFilter(mdcFilterFilter);
        registrationBean.setOrder(2);
        return registrationBean;
    }
}