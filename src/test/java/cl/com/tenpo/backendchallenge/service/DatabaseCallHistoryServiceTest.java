package cl.com.tenpo.backendchallenge.service;

import cl.com.tenpo.backendchallenge.entity.CallHistory;
import cl.com.tenpo.backendchallenge.repository.CallHistoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseCallHistoryServiceTest {

    @Mock
    private CallHistoryRepository callHistoryRepository;

    private DatabaseCallHistoryService callHistoryService;

    AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        callHistoryService = new DatabaseCallHistoryService(callHistoryRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void testHistory_ShouldReturnPageOfCallHistory() {
        var callHistory1 = new CallHistory(LocalDateTime.now(), "/endpoint1", "param1=value1", "response1");
        var callHistory2 = new CallHistory(LocalDateTime.now(), "/endpoint2", "param2=value2", "response2");
        var callHistories = List.of(callHistory1, callHistory2);
        var page = new PageImpl<>(callHistories);
        var pageable = PageRequest.of(0, 10);

        when(callHistoryRepository.findAll(pageable)).thenReturn(page);

        var result = callHistoryService.history(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("/endpoint1", result.getContent().get(0).getInvokedEndpoint());
        assertEquals("/endpoint2", result.getContent().get(1).getInvokedEndpoint());
        verify(callHistoryRepository, times(1)).findAll(pageable);
    }

    @Test
    void testSave_ShouldSaveCallHistory() {
        var invokedEndpoint = "/endpoint1";
        var requestParameters = "param1=value1";
        var response = "response1";

        callHistoryService.save(invokedEndpoint, requestParameters, response);

        var argumentCaptor = ArgumentCaptor.forClass(CallHistory.class);
        verify(callHistoryRepository, times(1)).save(argumentCaptor.capture());

        var capturedCallHistory = argumentCaptor.getValue();
        assertEquals(invokedEndpoint, capturedCallHistory.getInvokedEndpoint());
        assertEquals(requestParameters, capturedCallHistory.getRequestParameters());
        assertEquals(response, capturedCallHistory.getResponse());
        assertNotNull(capturedCallHistory.getDateTime());
    }
}
