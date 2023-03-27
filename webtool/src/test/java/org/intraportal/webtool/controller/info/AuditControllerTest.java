package org.intraportal.webtool.controller.info;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.intraportal.api.service.audit.AuditLogService;
import org.intraportal.webtool.stubs.ActionLogCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.datatables.mapping.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AnnotationConfigContextLoader.class})
@WebMvcTest(AuditController.class)
public class AuditControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuditController auditController;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(auditController).build();
    }

    @Test
    public void loadAuditLogPage() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/web/audit/log"))
                .andExpect(status().isOk())
                .andExpect(view().name("templates/info/log/auditlogs.html"));
    }

    @Test
    public void getSessionLogs() throws Exception {
        when(auditLogService.fetchSessionLogData(any())).thenReturn(ActionLogCreator.getSessionLogDto());

        DataTablesInput input = new DataTablesInput();
        input.setDraw(3);
        input.setLength(4);
        input.setColumns(Collections.singletonList(new Column("id", "id", true, true, new Search())));
        input.setOrder(Collections.singletonList(new Order(0, "asc")));

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/audit/log/session/list")
                                .content(objectMapper.writeValueAsString(input))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        DataTablesOutput output = objectMapper.readValue(result.getResponse().getContentAsString(), DataTablesOutput.class);

        assertThat(output.getDraw()).isEqualTo(1);
        assertThat(output.getData().size()).isEqualTo(3);
        assertThat(output.getRecordsTotal()).isEqualTo(3);
    }

    @Test
    public void getActionLogs() throws Exception {
        when(auditLogService.fetchActionLogData(any())).thenReturn(ActionLogCreator.createActionLogDtos());

        DataTablesInput input = new DataTablesInput();
        input.setDraw(3);
        input.setLength(4);
        input.setColumns(Collections.singletonList(new Column("id", "id", true, true, new Search())));
        input.setOrder(Collections.singletonList(new Order(0, "asc")));

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/web/audit/log/action/list")
                                .content(objectMapper.writeValueAsString(input))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        DataTablesOutput output = objectMapper.readValue(result.getResponse().getContentAsString(), DataTablesOutput.class);

        assertThat(output.getDraw()).isEqualTo(1);
        assertThat(output.getData().size()).isEqualTo(3);
        assertThat(output.getRecordsTotal()).isEqualTo(3);
    }
}