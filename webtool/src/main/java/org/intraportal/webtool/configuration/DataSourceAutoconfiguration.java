package org.intraportal.webtool.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceAutoconfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceAutoconfiguration.class);

    @Bean("dataSource")
    public DataSource dataSource(DataSourceProperties properties) {
        LOGGER.info(String.format("Autoconfiguring datasource bean"));
        LOGGER.info(String.format("DB location: %s", properties.getUrl()));
        DataSourceBuilder<?> builder = DataSourceBuilder.create();
        builder.username(properties.getUsername());
        builder.password(properties.getPassword());
        builder.url(properties.getUrl());
        LOGGER.info(String.format("Finished autoconfiguring datasource bean"));
        return builder.build();
    }
}
