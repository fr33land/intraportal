package org.intraportal.webtool.controller.admin;

import org.intraportal.webtool.dto.NetworkInterfaceDto;
import org.intraportal.webtool.service.DatePickerPeriodHandler;
import org.intraportal.webtool.service.server.NetworkAddressHandler;
import org.intraportal.api.service.time.TimeZoneHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller("ServerWebController")
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/web/admin/server")
public class ServerWebController {

    private static final Logger logger = LoggerFactory.getLogger(ServerWebController.class);

    private final TimeZoneHandler timeZoneHandler;
    private final NetworkAddressHandler networkAddressHandler;
    private final DatePickerPeriodHandler datePickerPeriodHandler;

    public ServerWebController(TimeZoneHandler timeZoneHandler, NetworkAddressHandler networkAddressHandler, DatePickerPeriodHandler datePickerPeriodHandler) {
        this.timeZoneHandler = timeZoneHandler;
        this.networkAddressHandler = networkAddressHandler;
        this.datePickerPeriodHandler = datePickerPeriodHandler;
    }

    @GetMapping
    public String loadServerConfigurationPage(Model model) {
        selectDefaultTab("config-network-tab", model);
        model.addAttribute("availableTimezones", timeZoneHandler.getAvailableTimeZoneDtos());
        datePickerPeriodHandler.fillInSingleSelectDatePickerModelParameters(model, true);

        try {
            var networkAdapterDetails = networkAddressHandler.readAdapterSettings();
            model.addAttribute("adapterSettings", networkAdapterDetails);
        } catch (Exception exception) {
            model.addAttribute("adaptersReadError", exception.getMessage());
            model.addAttribute("adapterSettings", new ArrayList<NetworkInterfaceDto>());
            logger.error("Error while reading Network adapter details: {}", exception.getMessage());
        }

        return "templates/admin/server/configuration.html";
    }

    private void selectDefaultTab(String tabName, Model model) {
        if (model.getAttribute("tab") == null) {
            model.addAttribute("tab", tabName);
        }
    }

}