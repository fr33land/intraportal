package org.intraportal.webtool.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.intraportal.api.service.audit.AuditLogService;
import org.intraportal.api.service.shell.CommandProcessHandler;
import org.intraportal.persistence.domain.server.ServerTime;
import org.intraportal.persistence.domain.server.TimeZoneDto;
import org.intraportal.webtool.configuration.WebMvcConfig;
import org.intraportal.webtool.dto.NetworkAdapterDetailsDto;
import org.intraportal.webtool.exception.GlobalMVCExceptionHandler;
import org.intraportal.webtool.service.server.NetworkAddressHandler;
import org.intraportal.api.service.time.ServerTimeHandler;
import org.intraportal.api.service.time.TimeZoneHandler;
import org.intraportal.webtool.dto.TimeSyncStatusDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.*;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@ContextConfiguration(classes = {
        ServerWebApiController.class,
        ServerTimeHandler.class,
        TimeZoneHandler.class,
        NetworkAddressHandler.class,
        WebMvcConfig.class,
        GlobalMVCExceptionHandler.class})
class ServerWebApiControllerTest {

    final ZoneId testZoneId = ZoneId.of("Europe/Copenhagen");
    final TimeZone testTimeZone = TimeZone.getTimeZone(testZoneId);
    final LocalDateTime currentTestTime = LocalDateTime.of(2022, 10, 7, 8, 15, 30);

    MockedStatic<Clock> clockMock;

    MockedStatic<Calendar> calendarkMock;

    @Mock
    Calendar calendar;

    @MockBean
    AuditLogService auditLogService;

    @MockBean
    CommandProcessHandler commandProcessHandler;

    @Autowired
    ObjectMapper objectMapper;

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

        calendarkMock = mockStatic(Calendar.class);
        calendarkMock.when(Calendar::getInstance).thenReturn(calendar);
        when(calendar.getTimeZone()).thenReturn(testTimeZone);
    }

    @AfterEach
    void tearDown() {
        clockMock.close();
        calendarkMock.close();
    }

    @Test
    void changeServerNetworkAddress_validInput_callsScriptCorrectly() throws Exception {
        var networkAdapterDetailsDto = new NetworkAdapterDetailsDto("em1", "10.13.19.52", "255.255.255.0", "10.13.19.1", "8.8.4.4");
        var dtoAsString = objectMapper.writeValueAsString(networkAdapterDetailsDto);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/api/admin/server/network")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(dtoAsString)
                )
                .andExpect(status().isNoContent());

        verify(commandProcessHandler).executeSingleCommand(any(Function.class),
                eq("./set-address.sh"), eq("em1"), eq("10.13.19.52/24"), eq("10.13.19.1"), eq("8.8.4.4"));
    }

    @Test
    void changeServerNetworkAddress_noDNS_setsDefaultDNS() throws Exception {
        var networkAdapterDetailsDto = new NetworkAdapterDetailsDto("em1", "10.13.19.52", "255.255.255.0", "10.13.19.1", null);
        var dtoAsString = objectMapper.writeValueAsString(networkAdapterDetailsDto);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/api/admin/server/network")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(dtoAsString)
                )
                .andExpect(status().isNoContent());

        verify(commandProcessHandler).executeSingleCommand(any(Function.class),
                eq("./set-address.sh"), eq("em1"), eq("10.13.19.52/24"), eq("10.13.19.1"), eq("8.8.8.8"));

    }


    @Test
    void getServerTimeDetails_returnsCorrectDtoWithTimeAndZone() throws Exception {
        var expectedTimeZone = new TimeZoneDto("Europe/Copenhagen", "Central European Standard Time", "+01:00");
        var expectedResult = new ServerTime(
                LocalDate.of(2022, 10, 7),
                LocalTime.of(8, 15, 30),
                LocalTime.of(6, 15, 30), expectedTimeZone);

        var mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/web/api/admin/server/time/details")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        var mappedResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ServerTime.class);

        assertThat(mappedResponse)
                .isEqualTo(expectedResult);
    }

    @Test
    void getTimeSynchronizationStatus_ntpActive_clockSynced_returnsBothTrue() throws Exception {
        when(commandProcessHandler.executePipelineCommandWithOutput(any(), anyList(), anyList()))
                .thenAnswer(
                        i -> {
                            List<String> pipedCommand = i.getArgument(2);

                            if ("NTP service".equals(pipedCommand.get(1))) {
                                return List.of("NTP service: active");
                            }

                            if ("System clock synchronized".equals(pipedCommand.get(1))) {
                                return List.of("System clock synchronized: yes");
                            }

                            throw new RuntimeException("Input not supported by this Test Scenario");
                        }
                );

        var expectedResult = new TimeSyncStatusDto(true, true);

        var mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/web/api/admin/server/time/synchronization/status")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        var mappedResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TimeSyncStatusDto.class);

        assertThat(mappedResponse)
                .isEqualTo(expectedResult);
    }

    @Test
    void getTimeSynchronizationStatus_ntpActive_clockNotSynced_returnsTrueFalse() throws Exception {
        when(commandProcessHandler.executePipelineCommandWithOutput(any(), anyList(), anyList()))
                .thenAnswer(
                        i -> {
                            List<String> pipedCommand = i.getArgument(2);

                            if ("NTP service".equals(pipedCommand.get(1))) {
                                return List.of("NTP service: active");
                            }

                            if ("System clock synchronized".equals(pipedCommand.get(1))) {
                                return List.of("System clock synchronized: no");
                            }

                            throw new RuntimeException("Input not supported by this Test Scenario");
                        }
                );

        var expectedResult = new TimeSyncStatusDto(true, false);

        var mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/web/api/admin/server/time/synchronization/status")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        var mappedResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TimeSyncStatusDto.class);

        assertThat(mappedResponse)
                .isEqualTo(expectedResult);
    }

    @Test
    void getTimeSynchronizationStatus_bothNotActive_returnsFalseFalse() throws Exception {
        when(commandProcessHandler.executePipelineCommandWithOutput(any(), anyList(), anyList()))
                .thenAnswer(
                        i -> {
                            List<String> pipedCommand = i.getArgument(2);

                            if ("NTP service".equals(pipedCommand.get(1))) {
                                return List.of("NTP service: inactive");
                            }

                            if ("System clock synchronized".equals(pipedCommand.get(1))) {
                                return List.of("System clock synchronized: no");
                            }

                            throw new RuntimeException("Input not supported by this Test Scenario");
                        }
                );

        var expectedResult = new TimeSyncStatusDto(false, false);

        var mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/web/api/admin/server/time/synchronization/status")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        var mappedResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TimeSyncStatusDto.class);

        assertThat(mappedResponse)
                .isEqualTo(expectedResult);
    }

}