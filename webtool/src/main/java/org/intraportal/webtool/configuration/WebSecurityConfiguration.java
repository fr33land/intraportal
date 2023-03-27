package org.intraportal.webtool.configuration;

import org.intraportal.webtool.security.auth.AuthProvider;
import org.intraportal.webtool.security.auth.AuthProviderImpl;
import org.intraportal.webtool.security.filters.XssFilter;
import org.intraportal.webtool.security.handlers.IntraPortalLogoutSuccessHandler;
import org.intraportal.webtool.security.handlers.IntraPortalRedirectInvalidSessionStrategy;
import org.intraportal.webtool.security.handlers.LoginSuccessHandler;
import org.intraportal.webtool.security.services.WebUserDetailsService;
import org.intraportal.api.service.audit.AuditLogService;
import org.intraportal.api.service.users.UsersService;
import org.intraportal.webtool.security.filters.AuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.firewall.HttpStatusRequestRejectedHandler;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity()
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        prePostEnabled = true,
        jsr250Enabled = true
)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String LOGOUT_URL = "/web/logout";
    private static final String LOGOUT_TARGET_URL = "/web/login?logout=true";
    private static final String EXPIRED_SESSION_URL = "/web/login?expired=true";

    private final UsersService usersService;
    private final AuditLogService auditLogService;
    private final WebUserDetailsService webUserDetailsService;

    @Autowired
    public WebSecurityConfiguration(WebUserDetailsService webUserDetailsService, UsersService usersService, AuditLogService auditLogService) {
        this.webUserDetailsService = webUserDetailsService;
        this.usersService = usersService;
        this.auditLogService = auditLogService;
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers("/webjars/**", "/resources/**", "/swagger-ui.html", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(webUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .addFilterAfter(new XssFilter(), WebAsyncManagerIntegrationFilter.class)
                .addFilter(new AuthorizationFilter(authenticationManager(), communicationProvider()))
                    .requestMatchers()
                    .antMatchers("/web/**")
                .and()
                    .authorizeRequests()
                    .antMatchers(
                            "/web/login",
                            "/web/css/**",
                            "/web/js/**",
                            "/web/images/**"
                    )
                    .permitAll()
                    .antMatchers("/web/**").hasAnyAuthority("ADMIN", "VIEWER")
                    .antMatchers("/web/admin/**").hasAuthority("ADMIN")
                    .antMatchers("/web/api/admin/**").hasAuthority("ADMIN")
                    .antMatchers("/web/audit/**").hasAuthority("ADMIN")
                    .anyRequest().authenticated()
                .and()
                    .headers()
                    .xssProtection();

        http
                .formLogin()
                    .loginPage("/web/login")
                        .successHandler(new LoginSuccessHandler(usersService,"/web/dashboard", auditLogService))
                .and()
                    .logout()
                        .invalidateHttpSession(true)
                        .logoutUrl(LOGOUT_URL)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(new IntraPortalLogoutSuccessHandler(LOGOUT_TARGET_URL, redirectStrategy()))
                        .permitAll()
                .and()
                    .sessionManagement()
                        .invalidSessionStrategy(new IntraPortalRedirectInvalidSessionStrategy(LOGOUT_TARGET_URL, EXPIRED_SESSION_URL, redirectStrategy()))
                    .maximumSessions(5)
                        .expiredUrl(EXPIRED_SESSION_URL)
                        .sessionRegistry(sessionRegistry());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());

        return source;
    }

    @Bean
    public AuthProvider communicationProvider() throws Exception {
        return new AuthProviderImpl(authenticationManager());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 64, 4, 64, 2);
    }

    @Bean
    public RequestRejectedHandler requestRejectedHandler() {
        return new HttpStatusRequestRejectedHandler();
    }

    @Bean
    public RedirectStrategy redirectStrategy() {
        return new DefaultRedirectStrategy();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl( );
    }

}
