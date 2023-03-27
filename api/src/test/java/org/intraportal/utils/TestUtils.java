package org.intraportal.utils;

import org.intraportal.persistence.domain.audit.ActionLogDto;
import org.intraportal.persistence.domain.audit.SessionLogDto;
import org.intraportal.persistence.model.audit.ActionLog;
import org.intraportal.persistence.model.audit.AuditAction;
import org.intraportal.persistence.model.audit.AuditSessionAction;
import org.intraportal.persistence.model.audit.SessionLog;
import org.springframework.security.core.session.SessionInformation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestUtils {

    public static List<Object> mockUserDetailsList() {
        List<Object> udl = new ArrayList<>();

        org.springframework.security.core.userdetails.User udl1 =
                new org.springframework.security.core.userdetails.User("USER1", "PASS1", new ArrayList<>());
        org.springframework.security.core.userdetails.User udl2 =
                new org.springframework.security.core.userdetails.User("USER2", "PASS2", new ArrayList<>());
        org.springframework.security.core.userdetails.User udl3 =
                new org.springframework.security.core.userdetails.User("USER3", "PASS3", new ArrayList<>());

        udl.add(udl1);
        udl.add(udl2);
        udl.add(udl3);

        return udl;
    }

    public static List<SessionInformation> mockSessionInformationList() {
        List<SessionInformation> sil = new ArrayList<>();

        mockUserDetailsList().forEach(
                u -> sil.add(new SessionInformation(u, "SESS_" + ((org.springframework.security.core.userdetails.User) u).getUsername(), new Date()))
        );

        return sil;
    }

    public static List<SessionLog> mockSessionLogList() {
        List<SessionLog> sll = new ArrayList<>();

        SessionLog sl1 = new SessionLog();
        sl1.setSessionId("SESS_1");
        sl1.setUsername("user1");
        sl1.setAction(AuditSessionAction.STARTED);
        sl1.setId(1);
        sll.add(sl1);

        SessionLog sl2 = new SessionLog();
        sl2.setSessionId("SESS_2");
        sl2.setUsername("user1");
        sl2.setAction(AuditSessionAction.ENDED);
        sl2.setId(2);
        sll.add(sl2);

        SessionLog sl3 = new SessionLog();
        sl3.setSessionId("SESS_3");
        sl3.setUsername("user3");
        sl3.setAction(AuditSessionAction.ENDED);
        sl3.setId(3);
        sll.add(sl3);

        return sll;

    }

    public static List<SessionLogDto> createSessionLogDtos() {
        List<SessionLogDto> sll = new ArrayList<>();

        var sl1 = new SessionLogDto();
        sl1.setSessionId("SESS_1");
        sl1.setUsername("user1");
        sl1.setAction(AuditSessionAction.STARTED);
        sl1.setId(1);
        sll.add(sl1);

        var sl2 = new SessionLogDto();
        sl2.setSessionId("SESS_2");
        sl2.setUsername("user1");
        sl2.setAction(AuditSessionAction.ENDED);
        sl2.setId(2);
        sll.add(sl2);

        var sl3 = new SessionLogDto();
        sl3.setSessionId("SESS_3");
        sl3.setUsername("user3");
        sl3.setAction(AuditSessionAction.ENDED);
        sl3.setId(3);
        sll.add(sl3);

        return sll;
    }

    public static List<ActionLogDto> createSessionActionDtos() {
        var sl1 = new ActionLogDto();
        sl1.setSessionId("SESS_1");
        sl1.setActor("user1");
        sl1.setAction(AuditAction.CREATE);
        sl1.setId(1);

        var sl2 = new ActionLogDto();
        sl2.setSessionId("SESS_2");
        sl2.setActor("user2");
        sl2.setAction(AuditAction.RESTORE);
        sl2.setId(2);

        var sl3 = new ActionLogDto();
        sl3.setSessionId("SESS_3");
        sl3.setActor("user3");
        sl3.setAction(AuditAction.CHANGE_TIME);
        sl3.setId(3);

        return List.of(sl1, sl2, sl3);
    }

    public static List<ActionLog> mockSessionActionList() {
        List<ActionLog> sll = new ArrayList<>();

        ActionLog sl1 = new ActionLog();
        sl1.setSessionId("SESS_1");
        sl1.setActor("user1");
        sl1.setAction(AuditAction.CREATE);
        sl1.setId(1);
        sll.add(sl1);

        ActionLog sl2 = new ActionLog();
        sl2.setSessionId("SESS_2");
        sl2.setActor("user2");
        sl2.setAction(AuditAction.RESTORE);
        sl2.setId(2);
        sll.add(sl2);

        ActionLog sl3 = new ActionLog();
        sl3.setSessionId("SESS_3");
        sl3.setActor("user3");
        sl3.setAction(AuditAction.CHANGE_TIME);
        sl3.setId(3);
        sll.add(sl3);

        return sll;
    }
}
