package org.intraportal.webtool.configuration;

import org.intraportal.webtool.security.auditing.SecurityAuditorAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

import static org.intraportal.persistence.mappers.TimeZoneMapper.getOffsetDateTimeUTC;

@Configuration("JpaConfiguration")
@EnableJpaAuditing(auditorAwareRef = "auditorAware", dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new SecurityAuditorAware();
    }

    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(getOffsetDateTimeUTC());
    }

}
