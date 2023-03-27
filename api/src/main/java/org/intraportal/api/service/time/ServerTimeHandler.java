package org.intraportal.api.service.time;

import org.intraportal.api.exception.ApiExceptionHandler;
import org.intraportal.api.service.shell.CommandProcessHandler;
import org.intraportal.persistence.domain.server.NtpServerConfiguration;
import org.intraportal.persistence.domain.server.NtpServerType;
import org.intraportal.persistence.domain.server.ServerTime;
import org.intraportal.persistence.domain.server.SystemTime;
import org.intraportal.persistence.repository.audit.ActionAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.stream.Collectors.toList;

import static org.intraportal.persistence.mappers.TimeZoneMapper.getOffsetDateTimeUTC;
import static org.intraportal.persistence.model.audit.AuditAction.CHANGE_NTP;
import static org.intraportal.persistence.model.audit.AuditAction.CHANGE_TIME;
import static org.intraportal.persistence.model.audit.AuditDomain.SERVER;

@Service("ServerTimeHandler")
public class ServerTimeHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServerTimeHandler.class);

    public static final String SUDO_COMMAND = "sudo";
    public static final String TIME_DATE_COMMAND = "timedatectl";
    public static final String NTP_TOGGLE_OPERATION = "set-ntp";
    public static final String SET_TIME_OPERATION = "set-time";
    public static final String FILTER_OPERATION = "grep";
    public static final String SERVICE_COMMAND = "systemctl";
    public static final String RESTART_OPERATION = "restart";
    public static final String CHRONY_DAEMON = "chronyd";
    public static final String DEFAULT_CHRONY_SERVER_OPTIONS = "iburst";
    private static DateTimeFormatter setTimeDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TimeZoneHandler timeZoneHandler;
    private final CommandProcessHandler commandProcessHandler;
    private final String chronyConfigFilePath;
    private final String filterNTPActiveParameter;
    private final String filterClockSynchronizedParameter;
    private final List<String> supportedServerConfigParameters;

    public ServerTimeHandler(TimeZoneHandler timeZoneHandler, CommandProcessHandler commandProcessHandler,
                             @Value("${time.ntp.chrony.config-file}") String chronyConfigFilePath,
                             @Value("${time.ntp.active.filter-parameter-command}") String filterNTPActiveParameter,
                             @Value("${time.ntp.synchronized.filter-parameter-command}") String filterClockSynchronizedParameter) {
        this.timeZoneHandler = timeZoneHandler;
        this.commandProcessHandler = commandProcessHandler;
        this.chronyConfigFilePath = chronyConfigFilePath;
        this.filterNTPActiveParameter = filterNTPActiveParameter;
        this.filterClockSynchronizedParameter = filterClockSynchronizedParameter;
        this.supportedServerConfigParameters = Arrays.stream(NtpServerType.values())
                .map(value -> value.getValue())
                .collect(toList());
    }

    public SystemTime getServerTime() {
        var systemLocalDateTime = LocalDateTime.now();
        var utcTime = OffsetDateTime.now().getOffset().getTotalSeconds() == 0;
        return new SystemTime(systemLocalDateTime, utcTime);
    }

    public ServerTime getDetailedTime() {
        var serverDate = LocalDate.now();
        var serverTime = LocalTime.now();
        var timeUTC = getOffsetDateTimeUTC().toLocalTime();
        var timeZoneDto = timeZoneHandler.getServerTimeZone();

        return new ServerTime(serverDate, serverTime, timeUTC, timeZoneDto);
    }

    @ActionAudit(action = CHANGE_TIME, domain = SERVER)
    public void setDateTime(LocalDateTime localDateTime) throws Exception {
        commandProcessHandler.executeSingleCommand(ApiExceptionHandler.handleProcessException(),
                SUDO_COMMAND, TIME_DATE_COMMAND, SET_TIME_OPERATION, localDateTime.format(setTimeDateTimeFormatter));
    }


    @ActionAudit(action = CHANGE_NTP, domain = SERVER)
    public void toggleNTP(Boolean ntpEnabled) throws Exception {
        toggleNTPNoAudit(ntpEnabled);
    }

    public void toggleNTPNoAudit(Boolean ntpEnabled)  throws Exception {
        commandProcessHandler.executeSingleCommand(ApiExceptionHandler.handleProcessException(),
                SUDO_COMMAND, TIME_DATE_COMMAND, NTP_TOGGLE_OPERATION, ntpEnabled.toString().toLowerCase());
    }

    public Boolean readNTPActive() throws Exception {
        var mainCommand = List.of(TIME_DATE_COMMAND);
        var pipedCommand = List.of(FILTER_OPERATION, filterNTPActiveParameter);
        var commandResult = commandProcessHandler.executePipelineCommandWithOutput(ApiExceptionHandler.handleProcessException(),
                mainCommand, pipedCommand);

        if (commandResult.size() > 0) {
            var ntpActiveOutput = commandResult.get(0).trim();
            logger.debug("Investigating parameter for NTP active: {}", ntpActiveOutput);

            var indexOfValue = ntpActiveOutput.indexOf(":");
            if (indexOfValue > -1 && indexOfValue + 2 < ntpActiveOutput.length()) {
                var syncActiveValue = ntpActiveOutput.substring(indexOfValue + 1).toLowerCase().trim();
                logger.debug("syncActiveValue: {}", syncActiveValue);

                switch (syncActiveValue) {
                    case "inactive":
                        return false;
                    case "active":
                        return true;
                }

                return false;
            }
        } else {
            logger.warn("Did not found valid NTP sync active parameter int output: \"{}\". By main command = {}, piped command = {}",
                    commandResult, mainCommand, pipedCommand);
            return false;
        }

        var errorMessage = String.format("Did not found valid NTP sync active parameter int output: \"%s\". By main command = %s, piped command = %s",
                commandResult, mainCommand, pipedCommand);
        logger.error(errorMessage);

        throw ApiExceptionHandler.handleProcessException().apply(new Exception(errorMessage));
    }

    public Boolean readClockSynchronized() throws Exception {
        var mainCommand = List.of(TIME_DATE_COMMAND);
        var pipedCommand = List.of(FILTER_OPERATION, filterClockSynchronizedParameter);
        var commandResult = commandProcessHandler.executePipelineCommandWithOutput(ApiExceptionHandler.handleProcessException(),
                mainCommand, pipedCommand);

        if (commandResult.size() > 0) {
            var clockSynchronizedOutput = commandResult.get(0).trim();
            logger.debug("Investigating parameter for Clock synchronized: {}", clockSynchronizedOutput);

            var indexOfValue = clockSynchronizedOutput.indexOf(":");
            if (indexOfValue > -1 && indexOfValue + 2 < clockSynchronizedOutput.length()) {
                var clockSynchronizedValue = clockSynchronizedOutput.substring(indexOfValue + 1).toLowerCase().trim();
                logger.debug("clockSynchronizedValue: {}", clockSynchronizedValue);

                switch (clockSynchronizedValue) {
                    case "no":
                        return false;
                    case "yes":
                        return true;
                }

                return false;
            }
        } else {
            logger.warn("Did not found valid System clock synchronized parameter in output: \"{}\". By main command = {}, piped command = {}",
                    commandResult, mainCommand, pipedCommand);
            return false;
        }

        var errorMessage = String.format("Did not found valid System clock synchronized parameter in output: \"%s\". By main command = %s, piped command = %s",
                commandResult, mainCommand, pipedCommand);
        logger.error(errorMessage);

        throw ApiExceptionHandler.handleProcessException().apply(new Exception(errorMessage));
    }

    public void saveNTPServers(List<NtpServerConfiguration> ntpServers) throws Exception {
        List<String> configFileLines = new ArrayList<>();
        Path configFilePath = Paths.get(chronyConfigFilePath);
        try (BufferedReader reader = Files.newBufferedReader(configFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                var lineChunks = Arrays.stream(line.trim().toLowerCase().split(" "))
                        .filter(chunk -> !chunk.isBlank())
                        .collect(toList());

                if (lineChunks.size() > 1 && isServerConfigLine(lineChunks.get(0))) {
                    continue;
                }

                configFileLines.add(line);
            }
        } catch (IOException ioException) {
            logger.error("Error reading file: {}", ioException.getMessage());
            throw ApiExceptionHandler.handleProcessException().apply(ioException);
        }

        var serverConfigLines = ntpServers.stream()
                .map(ntpServer -> String.format("%s %s %s", ntpServer.getType().getValue(), ntpServer.getAddress(), DEFAULT_CHRONY_SERVER_OPTIONS))
                .collect(toList());
        logger.info("New Server configuration records will be saved: {}", serverConfigLines);
        configFileLines.addAll(serverConfigLines);

        try {
            Files.write(configFilePath, configFileLines, TRUNCATE_EXISTING);
            logger.info("Successfully updated NTP servers config file: {}", configFilePath);
        } catch (IOException ioException) {
            logger.error("Error writing config file: {}", ioException.getMessage());
            throw ApiExceptionHandler.handleProcessException().apply(ioException);
        }

        restartChrony();
    }

    public void restartChrony() throws Exception {
        logger.debug("restart chrony daemon");
        restartChronyNoAudit();
    }

    public void restartChronyNoAudit() throws Exception {
        commandProcessHandler.executeSingleCommand(ApiExceptionHandler.handleProcessException(),
                SUDO_COMMAND, SERVICE_COMMAND, RESTART_OPERATION, CHRONY_DAEMON);
    }

    public List<NtpServerConfiguration> readNtpServers() throws Exception {
        List<NtpServerConfiguration> serverConfigurations = new ArrayList<>();

        Path path = Paths.get(chronyConfigFilePath);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseChronyNTPConfigurationLine(line)
                        .ifPresent(serverConfiguration -> serverConfigurations.add(serverConfiguration));
            }
        } catch (IOException ioException) {
            logger.error("Error reading file: {}", ioException.getMessage());
            throw ApiExceptionHandler.handleIOException().apply(ioException);
        }

        return serverConfigurations;
    }

    private boolean isServerConfigLine(String line) {
        return supportedServerConfigParameters.contains(line);
    }

    private Optional<NtpServerConfiguration> parseChronyNTPConfigurationLine(String configFileLine) {
        var lineChunks = Arrays.stream(configFileLine.trim().toLowerCase().split(" "))
                .filter(chunk -> !chunk.isBlank())
                .collect(toList());

        if (lineChunks.size() > 1 && isServerConfigLine(lineChunks.get(0))) {
            var type = NtpServerType.resolve(lineChunks.get(0));
            var address = lineChunks.get(1);
            String options = null;
            if (lineChunks.size() > 2) {
                var optionChunks = lineChunks.subList(2, lineChunks.size());
                options = String.join(" ", optionChunks);
            }
            logger.debug("ignoring Options in chrony server configuration: {}", options);

            return Optional.of(new NtpServerConfiguration(type, address));
        }

        return Optional.empty();
    }

}
