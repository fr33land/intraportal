package org.intraportal.webtool.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.intraportal.webtool.security.listeners.SessionDestroyedListener;
import org.intraportal.api.service.audit.AuditLogService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration("WebMvcConfig")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class WebMvcConfig implements WebMvcConfigurer {

    public static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static LocalDateTimeSerializer LOCAL_DATETIME_SERIALIZER = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/web/**").addResourceLocations("classpath:/static/");
    }

    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        var templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setCacheable(false);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return new ClassLoaderTemplateResolver();
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        var engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        engine.addDialect(springSecurityDialect());
        return engine;
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

    @Bean
    public ViewResolver viewResolver() {
        var resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return resolver;
    }

    @Bean
    public MessageSource messageSource() {
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(createJavaTimeModule(DATE_TIME_FORMAT));
        mapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));

        return mapper;
    }

    private JavaTimeModule createJavaTimeModule(String dateTimePattern) {
        var javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimePattern)));
        return javaTimeModule;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .failOnUnknownProperties(false)
                .serializers(LOCAL_DATETIME_SERIALIZER)
                .serializationInclusion(JsonInclude.Include.NON_NULL);

        return new MappingJackson2HttpMessageConverter(builder.build());
    }

    @Bean
    public YAMLMapper yamlMapper() {
        YAMLMapper mapper = new YAMLMapper();

        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));

        return mapper;
    }

    @Bean
    public MappingJackson2YamlHttpMessageConverter yamlHttpMessageConverter(YAMLMapper yamlMapper) {
        return new MappingJackson2YamlHttpMessageConverter(yamlMapper);
    }

    @Bean
    public SessionDestroyedListener sessionDestroyedListener(AuditLogService auditLogService) {
        return new SessionDestroyedListener(auditLogService);
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public Validator localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }
}
