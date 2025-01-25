package cl.com.tenpo.backendchallenge.controller;

import cl.com.tenpo.backendchallenge.entity.CallHistory;
import cl.com.tenpo.backendchallenge.service.DatabaseCallHistoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class CallHistoryControllerTest {

    @Mock
    private DatabaseCallHistoryService callHistoryService;

    @InjectMocks
    private CallHistoryController callHistoryController;

    private MockMvc mockMvc;

    AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(callHistoryController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void testHistory() throws Exception {
        var callHistory = new CallHistory(LocalDateTime.parse("2025-01-24T20:49:32"), "/call-history", "[0, 1]", "{}");
        var callHistories = List.of(callHistory);
        var callHistoryPage = new PageImpl<>(callHistories, PageRequest.of(0, 20, Sort.by(Sort.Order.desc("dateTime"))), callHistories.size());

        when(callHistoryService.history(any())).thenReturn(callHistoryPage);

        mockMvc.perform(get("/call-history")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].invokedEndpoint").value("/call-history"))
                .andExpect(jsonPath("$[0].requestParameters").value("[0, 1]"));
    }

    @Test
    void testHistoryWithDefaultPagination() throws Exception {
        var callHistory = new CallHistory(LocalDateTime.parse("2025-01-24T20:49:32"), "/call-history", "[0, 1]", "{}");
        var callHistories = List.of(callHistory);
        var callHistoryPage = new PageImpl<>(callHistories, PageRequest.of(0, 20), callHistories.size());

        when(callHistoryService.history(any())).thenReturn(callHistoryPage);

        mockMvc.perform(get("/call-history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].invokedEndpoint").value("/call-history"))
                .andExpect(jsonPath("$[0].dateTime").value("2025-01-24T20:49:32"))
                .andExpect(jsonPath("$[0].requestParameters").value("[0, 1]"));
    }

    @Test
    void testHistoryWithEmptyResult() throws Exception {
        Page<CallHistory> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(callHistoryService.history(any())).thenReturn(emptyPage);

        mockMvc.perform(get("/call-history")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testHistoryWithInvalidPagination() throws Exception {
        mockMvc.perform(get("/call-history")
                        .param("page", "-1")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}