package org.intraportal.security.mock;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockIntraPortalUserDetailsSecurityContextFactory.class)
public @interface WithMockIntraPortalUserDetails {

    int userId();

    String username();

    String password() default "password";

    String firstname() default "firstName";

    String lastname() default "lastName";

    String email() default "email";

    String[] authorities() default {};

}
