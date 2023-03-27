package org.intraportal.webtool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class, DataSourceAutoConfiguration.class })
@EnableJpaRepositories(
        basePackages = {"org.intraportal.persistence.repository"},
        repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class
)
@EntityScan(basePackages = {"org.intraportal.persistence.model"})
@ComponentScan(basePackages = {"org.intraportal"})
@ConfigurationPropertiesScan("org.intraportal")
@EnableScheduling
@EnableCaching
public class LauncherApplication {

	public static void main(String[] args) {
		SpringApplication.run(LauncherApplication.class, args);
	}

}
