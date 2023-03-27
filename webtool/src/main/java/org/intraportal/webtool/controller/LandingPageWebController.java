package org.intraportal.webtool.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("LandingPageWebController")
@RequestMapping("/web")
public class LandingPageWebController {

    @GetMapping("/login")
    public String getLoginPage() {
        var userNotAuthenticated = SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken;
        return userNotAuthenticated
                ? "templates/login.html"
                : "redirect:/web/dashboard";
    }

    @GetMapping("/home")
    public String getHomeMenuPage() {
        return "redirect:/web/dashboard";
    }

}