package org.intraportal.webtool.controller.info;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.intraportal.persistence.model.MigrationRecord;
import org.intraportal.persistence.repository.SchemaVersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AnnotationConfigContextLoader.class})
@WebMvcTest(SystemStatusController.class)
class SystemStatusControllerTest {

    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private MockMvc mockMvc;

    @Mock
    private SchemaVersionRepository schemaVersionRepository;

    @InjectMocks
    private SystemStatusController systemStatusController;

    private BuildProperties buildProperties;
    private ObjectMapper objectMapper = new ObjectMapper();

    private LocalDateTime buildTimeUTC = LocalDateTime.of(2022, 11, 3, 12, 0, 0);
    private LocalDateTime installedDBOnUTC = LocalDateTime.of(2022, 9, 1, 12, 0, 0);

    @BeforeEach
    public void setup() {


        Properties properties = new Properties();
        properties.setProperty("name", "intraportal");
        properties.setProperty("version", "1.3.3");
        properties.setProperty("time", String.valueOf(buildTimeUTC.toInstant(ZoneOffset.UTC).toEpochMilli()));
        properties.setProperty("artifact", "intraportal");
        properties.setProperty("group", "org.intraportal");

        buildProperties = new BuildProperties(properties);
        ReflectionTestUtils.setField(systemStatusController, "buildProperties", buildProperties);
        mockMvc = MockMvcBuilders.standaloneSetup(systemStatusController).build();
    }

    @Test
    void loadRadarStatusPage() throws Exception {
        var localDBTime = OffsetDateTime.of(installedDBOnUTC, ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault());
        var expectedDBFormattedDate = localDBTime.format(dateFormatter);

        var zonedBuildTime = OffsetDateTime.of(buildTimeUTC, ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault());
        var expectedBuildFormattedDate = zonedBuildTime.format(dateFormatter);

        MigrationRecord record = new MigrationRecord();
        record.setVersion("10");
        record.setDescription("New changes");
        record.setInstalledOn(installedDBOnUTC);

        when(schemaVersionRepository.findTopByOrderByInstalledRankDesc()).thenReturn(record);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/web/info/status")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("templates/info/status/overview.html"))
                .andExpect(model().attributeExists("buildVersion"))
                .andExpect(model().attributeExists("buildTime"))
                .andExpect(model().attributeExists("dbDescription"))
                .andExpect(model().attributeExists("dbVersion"))
                .andExpect(model().attributeExists("dbMigrationDate"))
                .andExpect(model().attribute("buildVersion", equalTo(buildProperties.getVersion())))
                .andExpect(model().attribute("buildTime", equalTo(expectedBuildFormattedDate)))
                .andExpect(model().attribute("dbDescription", equalTo("New changes")))
                .andExpect(model().attribute("dbVersion", equalTo("10")))
                .andExpect(model().attribute("dbMigrationDate", equalTo(expectedDBFormattedDate)))
                .andReturn();
    }
}