package org.intraportal.webtool.controller.admin;

import org.intraportal.persistence.domain.server.TimeZoneDto;
import org.intraportal.webtool.service.DatePickerPeriodHandler;
import org.intraportal.webtool.service.server.NetworkAddressHandler;
import org.intraportal.api.service.time.TimeZoneHandler;
import org.intraportal.webtool.controller.admin.ServerWebController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AnnotationConfigContextLoader.class})
@WebAppConfiguration
class ServerWebControllerTest {

    MockMvc mockMvc;

    @Mock
    TimeZoneHandler timeZoneHandler;

    @Mock
    NetworkAddressHandler networkAddressHandler;

    @Mock
    DatePickerPeriodHandler datePickerPeriodHandler;

    @InjectMocks
    private ServerWebController serverWebController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(serverWebController).build();
    }

    @Test
    void loadServerConfigurationPage_loadsPageCorrectly() throws Exception {
        var availableTimezones = List.of(
                new TimeZoneDto(),
                new TimeZoneDto()
        );
        when(timeZoneHandler.getAvailableTimeZoneDtos())
                .thenReturn(availableTimezones);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/web/admin/server"))
                .andExpect(status().isOk())
                .andExpect(view().name("templates/admin/server/configuration.html"))
                .andExpect(model().attribute("tab", equalTo("config-network-tab")));

        verify(networkAddressHandler)
                .readAdapterSettings();
    }

}