package org.intraportal.webtool.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("DashboardController")
@PreAuthorize("isFullyAuthenticated()")
@RequestMapping("/web/dashboard")
public class DashboardController {

    @GetMapping
    public String loadDashboardPage() {
        return "templates/dashboard.html";
    }
}
