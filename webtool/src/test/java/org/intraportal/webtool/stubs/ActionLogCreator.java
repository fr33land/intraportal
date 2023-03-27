package org.intraportal.webtool.stubs;

import org.intraportal.persistence.domain.audit.ActionLogDto;
import org.intraportal.persistence.domain.audit.SessionLogDto;
import org.intraportal.persistence.model.audit.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActionLogCreator {

    public static ActionLog createActionAuditLog(AuditDomain domain, AuditAction action) {
        var actionLog = new ActionLog();

        actionLog.setActor("JUnit");
        actionLog.setAction(action);
        actionLog.setDomain(domain);
        actionLog.setSessionId("1");

        return actionLog;
    }

    public static DataTablesOutput<ActionLogDto> createActionLogDtos() {
        ActionLog al1 = new ActionLog();
        al1.setId(1);
        al1.setActor("user");
        al1.setAction(AuditAction.CREATE);
        al1.setDomain(AuditDomain.CONFIG);
        al1.setCreatedDate(OffsetDateTime.now());

        ActionLog al2 = new ActionLog();
        al2.setId(2);
        al2.setActor("user");
        al2.setAction(AuditAction.RESTORE);
        al2.setDomain(AuditDomain.CONFIG);
        al2.setCreatedDate(OffsetDateTime.now());

        ActionLog al3 = new ActionLog();
        al3.setId(3);
        al3.setActor("user");
        al3.setAction(AuditAction.EDIT);
        al3.setDomain(AuditDomain.ADLS);
        al3.setCreatedDate(OffsetDateTime.now());

        DataTablesOutput output = new DataTablesOutput();
        output.setData(List.of(al1, al2, al3));
        output.setRecordsTotal(3);
        output.setRecordsFiltered(3);
        output.setDraw(1);

        return output;

    }

    public static DataTablesOutput<ActionLog> getActionLog() {
        List<ActionLog> actionLogList = new ArrayList<>();

        ActionLog al1 = new ActionLog();
        al1.setId(1);
        al1.setActor("user");
        al1.setAction(AuditAction.CREATE);
        al1.setDomain(AuditDomain.CONFIG);
        al1.setCreatedDate(OffsetDateTime.now());

        actionLogList.add(al1);

        ActionLog al2 = new ActionLog();
        al2.setId(2);
        al2.setActor("user");
        al2.setAction(AuditAction.RESTORE);
        al2.setDomain(AuditDomain.CONFIG);
        al2.setCreatedDate(OffsetDateTime.now());

        actionLogList.add(al2);

        ActionLog al3 = new ActionLog();
        al3.setId(3);
        al3.setActor("user");
        al3.setAction(AuditAction.EDIT);
        al3.setDomain(AuditDomain.ADLS);
        al3.setCreatedDate(OffsetDateTime.now());

        actionLogList.add(al3);

        DataTablesOutput output = new DataTablesOutput();
        output.setData(actionLogList);
        output.setRecordsTotal(3);
        output.setRecordsFiltered(3);
        output.setDraw(1);

        return output;

    }

    public static DataTablesOutput<SessionLogDto> getSessionLogDto() {
        List<SessionLogDto> sessionLogList = new ArrayList<>();

        SessionLogDto al1 = new SessionLogDto();
        al1.setId(1);
        al1.setUsername("user");
        al1.setAction(AuditSessionAction.STARTED);
        al1.setCreatedDate(LocalDateTime.now());

        sessionLogList.add(al1);

        SessionLogDto al2 = new SessionLogDto();
        al2.setId(2);
        al2.setUsername("user");
        al2.setAction(AuditSessionAction.ENDED);
        al2.setCreatedDate(LocalDateTime.now());

        sessionLogList.add(al2);

        SessionLogDto al3 = new SessionLogDto();
        al3.setId(3);
        al3.setUsername("user");
        al3.setAction(AuditSessionAction.LOGOUT);
        al3.setCreatedDate(LocalDateTime.now());

        sessionLogList.add(al3);

        DataTablesOutput output = new DataTablesOutput();
        output.setData(sessionLogList);
        output.setRecordsTotal(3);
        output.setRecordsFiltered(3);
        output.setDraw(1);

        return output;
    }

    public static DataTablesOutput<SessionLog> getSessionLog() {
        List<SessionLog> sessionLogList = new ArrayList<>();

        SessionLog al1 = new SessionLog();
        al1.setId(1);
        al1.setUsername("user");
        al1.setAction(AuditSessionAction.STARTED);
        al1.setCreatedDate(OffsetDateTime.now());

        sessionLogList.add(al1);

        SessionLog al2 = new SessionLog();
        al2.setId(2);
        al2.setUsername("user");
        al2.setAction(AuditSessionAction.ENDED);
        al2.setCreatedDate(OffsetDateTime.now());

        sessionLogList.add(al2);

        SessionLog al3 = new SessionLog();
        al3.setId(3);
        al3.setUsername("user");
        al3.setAction(AuditSessionAction.LOGOUT);
        al3.setCreatedDate(OffsetDateTime.now());

        sessionLogList.add(al3);

        DataTablesOutput output = new DataTablesOutput();
        output.setData(sessionLogList);
        output.setRecordsTotal(3);
        output.setRecordsFiltered(3);
        output.setDraw(1);

        return output;
    }

}
