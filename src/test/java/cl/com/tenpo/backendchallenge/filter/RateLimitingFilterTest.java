package cl.com.tenpo.backendchallenge.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.PrintWriter;

import static org.mockito.Mockito.*;

class RateLimitingFilterTest {

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private FilterChain mockFilterChain;

    @Mock
    private PrintWriter mockPrintWriter;

    private RateLimitingFilter rateLimitingFilter;

    AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        rateLimitingFilter = new RateLimitingFilter();

        ReflectionTestUtils.setField(rateLimitingFilter, "maxRequestsPerMinute", 3);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void testRateLimiting_ShouldAllowRequestsWithinLimit() throws Exception {
        when(mockRequest.getRemoteAddr()).thenReturn("192.168.1.1");
        when(mockResponse.getWriter()).thenReturn(mockPrintWriter);

        // Simulate 3 requests that should be allowed
        rateLimitingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        rateLimitingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        rateLimitingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // On the 4th request, it should be blocked
        rateLimitingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // Verify that the response status is set to TOO_MANY_REQUESTS (429) and the correct message is written
        verify(mockResponse, times(1)).setStatus(429);
        verify(mockPrintWriter, times(1)).write("Too many requests. Please try again later.");
        verify(mockFilterChain, times(3)).doFilter(mockRequest, mockResponse);
    }


    @Test
    void testRateLimiting_ShouldAllowAfterReset() throws Exception {
        when(mockRequest.getRemoteAddr()).thenReturn("192.168.1.1");
        when(mockResponse.getWriter()).thenReturn(mockPrintWriter);

        // Simulate 3 requests that should be allowed
        rateLimitingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        rateLimitingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        rateLimitingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        ReflectionTestUtils.setField(rateLimitingFilter, "requestCountsPerIpAddress",
                new java.util.concurrent.ConcurrentHashMap<>());

        // After reset, request count should be 1 again
        rateLimitingFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // Verify the 4th request goes through since it's allowed after the reset
        verify(mockFilterChain, times(4)).doFilter(mockRequest, mockResponse);
        verify(mockResponse, times(0)).setStatus(429); // No 429 should be triggered
    }

}
