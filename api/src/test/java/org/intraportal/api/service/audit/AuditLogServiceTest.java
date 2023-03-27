package org.intraportal.api.service.audit;

import org.intraportal.persistence.domain.audit.ActionLogDto;
import org.intraportal.persistence.domain.audit.SessionLogDto;
import org.intraportal.persistence.model.audit.AuditSessionAction;
import org.intraportal.persistence.repository.audit.ActionLogRepository;
import org.intraportal.persistence.repository.audit.SessionLogRepository;
import org.intraportal.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    SessionLogRepository sessionLogRepository;

    @Mock
    ActionLogRepository actionLogRepository;

    @InjectMocks
    AuditLogService auditLogService;

    @Test
    void fetchSessionLogData() {
        DataTablesInput input = new DataTablesInput();
        DataTablesOutput output = new DataTablesOutput();
        List<SessionLogDto> logList = TestUtils.createSessionLogDtos();
        output.setData(logList);

        when(sessionLogRepository.findAll(any(DataTablesInput.class), any(Function.class))).thenReturn(output);
        DataTablesOutput<SessionLogDto> result = auditLogService.fetchSessionLogData(input);

        assertArrayEquals(result.getData().toArray(), logList.toArray());
    }

    @Test
    void fetchActionLogData() {
        DataTablesInput input = new DataTablesInput();
        DataTablesOutput output = new DataTablesOutput();
        List<ActionLogDto> logList = TestUtils.createSessionActionDtos();
        output.setData(logList);

        when(actionLogRepository.findAll(any(DataTablesInput.class), any(Function.class))).thenReturn(output);
        DataTablesOutput<ActionLogDto> result = auditLogService.fetchActionLogData(input);

        assertArrayEquals(result.getData().toArray(), logList.toArray());
    }

    @Test
    void logUserSessionAction() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user1", "test");
        auditLogService.logUserSessionAction(UUID.randomUUID().toString(), auth, AuditSessionAction.STARTED);

        verify(sessionLogRepository, times(1)).save(any());
    }

    @Test
    void getUserActionLogData() {
        auditLogService.getUserActionLogData("user1", new DataTablesInput());
        verify(actionLogRepository, times(1)).findAll(any(DataTablesInput.class), any(Specification.class), eq(null), any(Function.class));
    }

    @Test
    void getUserSessionLogData() {
        auditLogService.getUserSessionLogData("user1", new DataTablesInput());
        verify(sessionLogRepository, times(1)).findAll(any(DataTablesInput.class), any(Specification.class), eq(null), any(Function.class));
    }

    @Test
    void removeAuditData() {
        auditLogService.removeAuditData();

        verify(sessionLogRepository, times(1)).deleteAll();
        verify(sessionLogRepository, times(1)).deleteAll();
    }
}