package org.intraportal.webtool.controller.info;

import org.intraportal.api.service.audit.AuditLogService;
import org.intraportal.api.service.shell.CommandProcessHandler;
import org.intraportal.persistence.repository.SchemaVersionRepository;
import org.intraportal.webtool.configuration.WebMvcConfig;
import org.intraportal.webtool.exception.GlobalMVCExceptionHandler;
import org.intraportal.api.service.time.ServerTimeHandler;
import org.intraportal.api.service.time.TimeZoneHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.TimeZone;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@ContextConfiguration(classes = {
        SystemStatusController.class,
        ServerTimeHandler.class,
        TimeZoneHandler.class,
        WebMvcConfig.class,
        GlobalMVCExceptionHandler.class})
class SystemStatusControllerClockTest {

    final ZoneId testZoneId = ZoneId.of("Europe/Copenhagen");
    final TimeZone testTimeZone = TimeZone.getTimeZone(testZoneId);
    final LocalDateTime currentTestTime = LocalDateTime.of(2022, 10, 7, 8, 15, 30);

    MockedStatic<Clock> clockMock;

    MockedStatic<Calendar> calendarMock;

    @Mock
    Calendar calendar;

    @MockBean
    AuditLogService auditLogService;

    @MockBean
    BuildProperties buildProperties;

    @MockBean
    CommandProcessHandler commandProcessHandler;

    @MockBean
    SchemaVersionRepository schemaVersionRepository;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        var currentTestInstant = currentTestTime.atZone(testZoneId).toInstant();
        var fixedClock = Clock.fixed(currentTestInstant, testZoneId);
        var fixedUTCClock = Clock.fixed(currentTestInstant, ZoneOffset.UTC);
        clockMock = mockStatic(Clock.class);
        clockMock.when(Clock::systemDefaultZone).thenReturn(fixedClock);
        clockMock.when(() -> Clock.system(ZoneOffset.UTC))
                .thenReturn(fixedUTCClock);

        calendarMock = mockStatic(Calendar.class);
        calendarMock.when(Calendar::getInstance).thenReturn(calendar);
        when(calendar.getTimeZone()).thenReturn(testTimeZone);
    }

    @AfterEach
    void tearDown() {
        clockMock.close();
        calendarMock.close();
    }

    @Test
    void getServerSystemTime_returnsDateTime() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/web/info/status/time")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateTime").value("2022-10-07 08:15:30"))
                .andExpect(jsonPath("$.utc").value("false"))
                .andReturn();
    }

}