package cl.com.tenpo.backendchallenge.service.external;

import cl.com.tenpo.backendchallenge.exception.ServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class HttpPercentageServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @Mock
    private HttpClient httpClient;

    @Mock
    private ObjectMapper objectMapper;

    private HttpPercentageService httpPercentageService;

    AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        httpPercentageService = new HttpPercentageService(
                "https://percentaje-external-service.com", cacheManager, httpClient, objectMapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void testRetrievePercentage_ShouldCallExternalService() throws Exception {
        var expectedPercentage = 15.0;
        var responseBody = "{\"percentage\": 15.0}";
        var response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(responseBody);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        var jsonNode = mock(JsonNode.class);
        when(jsonNode.get("percentage")).thenReturn(jsonNode);
        when(jsonNode.asDouble()).thenReturn(expectedPercentage);
        when(objectMapper.readTree(responseBody)).thenReturn(jsonNode);

        var result = httpPercentageService.retrievePercentage();

        assertEquals(expectedPercentage, result);
        verify(httpClient, times(1)).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void testRetrievePercentage_ShouldReturnCachedValue_WhenServiceFails() throws IOException, InterruptedException {
        var cachedPercentage = 10.0;
        when(cacheManager.getCache("percentage")).thenReturn(cache);
        when(cache.get("percentage", Double.class)).thenReturn(cachedPercentage);

        doThrow(new IOException("Service failed")).when(httpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));

        var result = httpPercentageService.retrievePercentage();

        assertEquals(cachedPercentage, result);
        verify(httpClient, times(1)).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void testRetrievePercentage_ShouldFallbackToDefault_WhenCacheAndServiceFail() throws IOException, InterruptedException {
        when(cacheManager.getCache("percentage")).thenReturn(null);

        doThrow(new IOException("Service failed")).when(httpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));

        var result = httpPercentageService.retrievePercentage();

        assertEquals(10.0, result);  // Default value when both cache and service fail
    }

    @Test
    void testFetchPercentageFromExternalService_ShouldThrowException_WhenServiceFails() throws IOException, InterruptedException {
        var errorMessage = "Service failed";
        var response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(500);
        when(response.body()).thenReturn(errorMessage);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        var exception = assertThrows(ServiceException.class, () -> httpPercentageService.retrievePercentage());
        assertEquals(errorMessage, exception.getMessage());
    }
}
