package org.intraportal.webtool.controller.info;

import org.intraportal.api.service.time.ServerTimeHandler;
import org.intraportal.persistence.domain.server.SystemTime;
import org.intraportal.persistence.repository.SchemaVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.intraportal.api.model.mappers.DateTimeMapper.toLocalDateTime;
import static org.springframework.http.ResponseEntity.ok;

@Controller("SystemStatusController")
@PreAuthorize("hasAnyAuthority('ADMIN', 'VIEWER')")
@RequestMapping("/web/info/status")
public class SystemStatusController {

    private static final Logger logger = LoggerFactory.getLogger(SystemStatusController.class);

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final BuildProperties buildProperties;
    private final SchemaVersionRepository schemaVersionRepository;
    private final ServerTimeHandler serverTimeHandler;

    public SystemStatusController(BuildProperties buildProperties, SchemaVersionRepository schemaVersionRepository, ServerTimeHandler serverTimeHandler) {
        this.buildProperties = buildProperties;
        this.schemaVersionRepository = schemaVersionRepository;
        this.serverTimeHandler = serverTimeHandler;
    }

    @GetMapping
    public String loadStatusPage(ModelMap mm) {

        mm.addAttribute("buildVersion", buildProperties.getVersion());

        var buildTimeUTC = buildProperties.getTime().atOffset(ZoneOffset.UTC);
        var buildLocalDateTime = toLocalDateTime(buildTimeUTC);
        var buildTime = buildLocalDateTime.format(dateTimeFormatter);
        mm.addAttribute("buildTime", buildTime);

        logger.debug("Application Build Version: {}", buildProperties.getVersion());
        logger.debug("Application Build Time: {}", buildTime);

        var lastMigration = schemaVersionRepository.findTopByOrderByInstalledRankDesc();
        logger.debug("Last DB migration: {}", lastMigration);

        mm.addAttribute("dbVersion", lastMigration.getVersion());
        mm.addAttribute("dbDescription", lastMigration.getDescription());
        var dbMigrationDateUTC = lastMigration.getInstalledOn().atOffset(ZoneOffset.UTC);
        var dbMigrationLocalDateTime = toLocalDateTime(dbMigrationDateUTC);
        var dbMigrationDate = dbMigrationLocalDateTime.format(dateTimeFormatter);
        mm.addAttribute("dbMigrationDate", dbMigrationDate);

        return "templates/info/status/overview.html";
    }

    @GetMapping(path = "/time", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SystemTime> getServerDateTime() {
        var serverTime = serverTimeHandler.getServerTime();
        return ok(serverTime);
    }

}
