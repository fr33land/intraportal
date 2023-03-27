package org.intraportal.api.service.parameters;


import org.intraportal.persistence.domain.server.MailSettings;
import org.intraportal.persistence.model.server.ServerParameter;
import org.intraportal.persistence.repository.ServerParameterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("ParameterService")
public class ParameterService {

    public static final String SMTP_SERVER_PARAMS_GROUP = "smtpServerParams";
    public static final String HOST_PARAM_NAME = "host";
    public static final String PORT_PARAM_NAME = "port";
    public static final String USER_NAME_PARAM_NAME = "userName";
    public static final String PASSWORD_PARAM_NAME = "password";
    private final ServerParameterRepository serverParameterRepository;

    @Autowired
    public ParameterService(ServerParameterRepository serverParameterRepository) {
        this.serverParameterRepository = serverParameterRepository;
    }

    public MailSettings getMailSettings() {
        MailSettings mailSettings = new MailSettings();
        List<ServerParameter> params = serverParameterRepository.findByParamGroup(SMTP_SERVER_PARAMS_GROUP);

        mailSettings.setHost(
                params
                        .stream()
                        .filter(p -> p.getParamName().equals("host"))
                        .findFirst().map(ServerParameter::getParamValue)
                        .orElse(null)
        );

        mailSettings.setPort(
                params
                        .stream()
                        .filter(p -> p.getParamName().equals("port"))
                        .findFirst().map(ServerParameter::getParamValue)
                        .map(Integer::parseInt)
                        .orElse(null)
        );

        mailSettings.setUserName(
                params
                        .stream()
                        .filter(p -> p.getParamName().equals("userName"))
                        .findFirst().map(ServerParameter::getParamValue)
                        .orElse(null)
        );

        mailSettings.setPassword(
                params
                        .stream()
                        .filter(p -> p.getParamName().equals("password"))
                        .findFirst().map(ServerParameter::getParamValue)
                        .orElse(null)
        );

        return mailSettings;

    }

    @Transactional
    public void setMailSettings(MailSettings mailSettings) {
        List<ServerParameter> params = serverParameterRepository.findByParamGroup(SMTP_SERVER_PARAMS_GROUP);

        ServerParameter hostParam = params
                .stream()
                .filter(p -> p.getParamName().equals(HOST_PARAM_NAME))
                .findFirst()
                .map(p -> {
                    p.setParamValue(mailSettings.getHost());
                    return p;
                })
                .orElse(new ServerParameter()
                        .builder()
                            .paramGroup(SMTP_SERVER_PARAMS_GROUP)
                            .paramName(HOST_PARAM_NAME)
                            .paramType(mailSettings.getPort().getClass().getSimpleName())
                            .paramValue(mailSettings.getHost())
                        .build()
                );
        serverParameterRepository.save(hostParam);

        ServerParameter portParam = params
                    .stream()
                    .filter(p -> p.getParamName().equals(PORT_PARAM_NAME))
                    .findFirst()
                    .map(p -> {
                        p.setParamValue(mailSettings.getPort().toString());
                        return p;
                    })
                    .orElse(new ServerParameter()
                            .builder()
                            .paramGroup(SMTP_SERVER_PARAMS_GROUP)
                            .paramName(PORT_PARAM_NAME)
                            .paramType(mailSettings.getPort().getClass().getSimpleName())
                            .paramValue(mailSettings.getPort().toString())
                            .build()
                    );
        serverParameterRepository.save(portParam);

        ServerParameter usernameParam = params
                    .stream()
                    .filter(p -> p.getParamName().equals(USER_NAME_PARAM_NAME))
                    .findFirst()
                    .map(p -> {
                        p.setParamValue(mailSettings.getUserName());
                        return p;
                    })
                    .orElse(new ServerParameter()
                            .builder()
                            .paramGroup(SMTP_SERVER_PARAMS_GROUP)
                            .paramName(USER_NAME_PARAM_NAME)
                            .paramType(mailSettings.getUserName().getClass().getSimpleName())
                            .paramValue(mailSettings.getUserName())
                            .build()
                    );
        serverParameterRepository.save(usernameParam);

        ServerParameter passwordParam = params
                        .stream()
                        .filter(p -> p.getParamName().equals(PASSWORD_PARAM_NAME))
                        .findFirst()
                        .map(p -> {
                            p.setParamValue(mailSettings.getPassword());
                            return p;
                        })
                        .orElse(new ServerParameter()
                                .builder()
                                .paramGroup(SMTP_SERVER_PARAMS_GROUP)
                                .paramName(PASSWORD_PARAM_NAME)
                                .paramType(mailSettings.getPassword().getClass().getSimpleName())
                                .paramValue(mailSettings.getPassword())
                                .build()
                        );
        serverParameterRepository.save(passwordParam);

    }

}
