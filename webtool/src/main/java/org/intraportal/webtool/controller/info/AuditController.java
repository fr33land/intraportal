package org.intraportal.webtool.controller.info;

import org.intraportal.persistence.domain.audit.ActionLogDto;
import org.intraportal.persistence.domain.audit.SessionLogDto;
import org.intraportal.api.service.audit.AuditLogService;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller("AuditController")
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/web/audit/log")
public class AuditController {

    private final AuditLogService auditLogService;

    public AuditController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ModelAndView loadAuditLogPage() {
        return new ModelAndView("templates/info/log/auditlogs.html");
    }

    @ResponseBody
    @PostMapping(value = "/session/list")
    public DataTablesOutput<SessionLogDto> getSessionLogs(@RequestBody DataTablesInput input) {
        return auditLogService.fetchSessionLogData(input);
    }

    @ResponseBody
    @PostMapping(value = "/action/list")
    public DataTablesOutput<ActionLogDto> getActionLogs(@RequestBody DataTablesInput input) {
        return auditLogService.fetchActionLogData(input);
    }

}
