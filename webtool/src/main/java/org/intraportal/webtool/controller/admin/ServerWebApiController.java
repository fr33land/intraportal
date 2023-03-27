package org.intraportal.webtool.controller.admin;

import org.intraportal.api.exception.DataObjectValidationException;
import org.intraportal.api.service.parameters.ParameterService;
import org.intraportal.api.service.time.ServerTimeHandler;
import org.intraportal.api.service.time.TimeZoneHandler;
import org.intraportal.api.service.validation.ParameterValidationService;
import org.intraportal.persistence.domain.ValidationResult;
import org.intraportal.persistence.domain.server.MailSettings;
import org.intraportal.persistence.domain.server.NtpSettings;
import org.intraportal.persistence.domain.server.ServerTime;
import org.intraportal.webtool.dto.NetworkAdapterDetailsDto;
import org.intraportal.webtool.dto.TimeSyncStatusDto;
import org.intraportal.webtool.service.server.NetworkAddressHandler;
import org.apache.commons.net.util.SubnetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@Controller("ServerWebApiController")
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/web/api/admin/server")
public class ServerWebApiController {

    private static final Logger logger = LoggerFactory.getLogger(ServerWebApiController.class);

    private final NetworkAddressHandler networkAddressHandler;
    private final ServerTimeHandler serverTimeHandler;
    private final TimeZoneHandler timeZoneHandler;
    private final ParameterService parameterService;
    private final ParameterValidationService parameterValidationService;

    public ServerWebApiController(NetworkAddressHandler networkAddressHandler, ServerTimeHandler serverTimeHandler, TimeZoneHandler timeZoneHandler, ParameterService parameterService, ParameterValidationService parameterValidationService) {
        this.networkAddressHandler = networkAddressHandler;
        this.serverTimeHandler = serverTimeHandler;
        this.timeZoneHandler = timeZoneHandler;
        this.parameterService = parameterService;
        this.parameterValidationService = parameterValidationService;
    }

    @PostMapping(path = "/network", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changeServerNetworkAddress(@Valid @RequestBody NetworkAdapterDetailsDto adapterDetailsDto) throws Exception {
        SubnetUtils.SubnetInfo subnetInfo;
        try {
            subnetInfo = new SubnetUtils(adapterDetailsDto.getIpAddress(), adapterDetailsDto.getSubnetMask())
                    .getInfo();
        } catch (IllegalArgumentException exception) {
            throw new DataObjectValidationException("ipAddress", "IP Address or Subnet mask is incorrect");
        }

        networkAddressHandler.changeNetworkAddress(adapterDetailsDto.getName(), subnetInfo.getCidrSignature(), adapterDetailsDto.getDefaultGateway(), adapterDetailsDto.getDns());
        return noContent().build();
    }

    @PostMapping("/time")
    public ResponseEntity<String> setServerDateTime(@RequestParam("dateTime") LocalDateTime dateTime) throws Exception {
        logger.info("POST set Server date time: [{}]", dateTime);
        serverTimeHandler.setDateTime(dateTime);
        return noContent().build();
    }

    @GetMapping(path = "/time/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ServerTime> getDetailedTime() {
        return ok(serverTimeHandler.getDetailedTime());
    }

    @PostMapping("/time/zone")
    public ResponseEntity<Void> setTimeZone(@RequestParam("timeZone") String timeZone) throws Exception {
        logger.info("set server's Time zone to: {}", timeZone);

        var timeZoneIdIsValid = timeZoneHandler.validateTimeZoneId(timeZone);
        if (!timeZoneIdIsValid) {
            throw new DataObjectValidationException("timeZone", "Time Zone is not valid");
        }

        timeZoneHandler.setTimeZone(timeZone);
        return noContent().build();
    }

    @GetMapping(path = "/time/synchronization/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TimeSyncStatusDto> getTimeSynchronizationStatus() throws Exception {
        var ntpActive = serverTimeHandler.readNTPActive();
        var clockSynchronized = serverTimeHandler.readClockSynchronized();
        return ok(new TimeSyncStatusDto(ntpActive, clockSynchronized));
    }

    @PostMapping("/time/ntp/active")
    public ResponseEntity<Void> toggleNTP(@RequestParam("ntpActive") Boolean ntpActive) throws Exception {
        logger.info("POST update NTP active: [{}]", ntpActive);
        serverTimeHandler.toggleNTP(ntpActive);
        return noContent().build();
    }

    @GetMapping(path = "/time/ntp/servers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NtpSettings> getNtpServers() throws Exception {
        var ntpServers = serverTimeHandler.readNtpServers();
        return ok(new NtpSettings(ntpServers));
    }

    @PostMapping(path = "/time/ntp/servers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveNTPServers(@RequestBody NtpSettings ntpSettings) throws Exception {
        logger.info("save NTP servers: [{}]", ntpSettings);
        serverTimeHandler.saveNTPServers(ntpSettings.getServerConfigurations());
        return noContent().build();
    }

    @GetMapping(path = "/mail/smtp/settings", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MailSettings> getSmtpSettings() {
        return ok(parameterService.getMailSettings());
    }

    @PostMapping(path = "/mail/smtp/settings", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveSmtpSettings(@RequestBody MailSettings mailSettings) throws DataObjectValidationException {
        ValidationResult result = parameterValidationService.validateSmtpServerSettings(mailSettings);

        if(result.isNotValid()) {
            throw new DataObjectValidationException("Form has errors", result.getValidationMessage());
        }

        parameterService.setMailSettings(mailSettings);
        return noContent().build();
    }

}