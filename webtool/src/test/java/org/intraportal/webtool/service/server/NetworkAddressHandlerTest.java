package org.intraportal.webtool.service.server;

import org.intraportal.api.service.shell.CommandProcessHandler;
import org.intraportal.webtool.dto.NetworkAdapterDetailsDto;
import org.intraportal.webtool.service.server.NetworkAddressHandler;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NetworkAddressHandlerTest {

    @Mock
    CommandProcessHandler commandProcessHandler;

    NetworkAddressHandler networkAddressHandler;

    @BeforeEach
    void setUp() {
        networkAddressHandler = new NetworkAddressHandler(commandProcessHandler, "8.8.8.8");
    }

    @Test
    void readAdapterSettings_nmcliProvidesOutput_parsesCorrectly() throws Exception {
        var expectedDto = new NetworkAdapterDetailsDto("eno1", "10.13.19.52", "255.255.255.0", "10.13.19.1", "8.8.8.1");

        var testOutputAdapters = IOUtils.readLines(Objects.requireNonNull(this.getClass().getResourceAsStream("/command-process/nmcli_active_adapter_list_output.txt")));
        var testOutputAdapterParameters = IOUtils.readLines(Objects.requireNonNull(this.getClass().getResourceAsStream("/command-process/nmcli_single_network_adapter_output.txt")));

        when(commandProcessHandler.executeSingleCommandWithOutput(any(Function.class),
                eq("nmcli"), eq("-t"), eq("connection"), eq("show"), eq("--active")))
                .thenReturn(testOutputAdapters);

        when(commandProcessHandler.executeSingleCommandWithOutput(any(Function.class),
                eq("nmcli"), eq("-t"), eq("connection"), eq("show"), eq("eno1")))
                .thenReturn(testOutputAdapterParameters);

        var dtoOutput = networkAddressHandler.readAdapterSettings();

        assertThat(dtoOutput)
                .containsExactlyInAnyOrder(expectedDto);
    }

}