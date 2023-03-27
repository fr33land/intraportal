package org.intraportal.api.service.users;

import org.intraportal.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersSessionServiceTest {

    @Mock
    SessionRegistry sessionRegistry;

    @InjectMocks
    UsersSessionService usersSessionService;

    @Test
    void expireUserSessions_withExpireCurrent() {
        List<Object> udl = TestUtils.mockUserDetailsList();
        List<SessionInformation> sil = TestUtils.mockSessionInformationList();

        when(sessionRegistry.getAllPrincipals()).thenReturn(udl);
        when(sessionRegistry.getAllSessions(any(), eq(true))).thenReturn(sil);
        usersSessionService.expireUserSessions("SESS_" + udl.get(1), true);

        verify(sessionRegistry, times(1)).getAllPrincipals();
        verify(sessionRegistry, times(3)).getAllSessions(any(), eq(true));
    }

    @Test
    void expireUserSessions_withDontExpireCurrent() {
        List<Object> udl = TestUtils.mockUserDetailsList();
        List<SessionInformation> sil = TestUtils.mockSessionInformationList();

        when(sessionRegistry.getAllPrincipals()).thenReturn(udl);
        when(sessionRegistry.getAllSessions(any(), eq(true))).thenReturn(sil);
        usersSessionService.expireUserSessions("SESS_" + udl.get(1), false);

        verify(sessionRegistry, times(1)).getAllPrincipals();
        verify(sessionRegistry, times(3)).getAllSessions(any(), eq(true));
    }
}