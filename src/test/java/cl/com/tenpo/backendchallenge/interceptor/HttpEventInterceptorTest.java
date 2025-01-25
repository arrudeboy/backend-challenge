package cl.com.tenpo.backendchallenge.interceptor;

import cl.com.tenpo.backendchallenge.service.DatabaseCallHistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HttpEventInterceptorTest {

    @Mock
    private DatabaseCallHistoryService callHistoryService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ProceedingJoinPoint joinPoint;

    private HttpEventInterceptor httpEventInterceptor;

    AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        httpEventInterceptor = new HttpEventInterceptor(callHistoryService, objectMapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void testRecord_SuccessfulResponseOnGetMethod() throws Throwable {

        var request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/call-history");
        when(request.getMethod()).thenReturn("GET");

        var mockRequestAttribs = mock(ServletRequestAttributes.class);
        when(mockRequestAttribs.getRequest()).thenReturn(request);
        when(objectMapper.writeValueAsString(any(Object.class))).thenReturn("{\"response\":\"success\"}");
        when(joinPoint.proceed()).thenReturn(ResponseEntity.ok().build());

        try(MockedStatic<RequestContextHolder> staticMock = mockStatic(RequestContextHolder.class)) {
            staticMock.when(RequestContextHolder::getRequestAttributes).thenReturn(mockRequestAttribs);

            var response = httpEventInterceptor.record(joinPoint);

            Assertions.assertNotNull(response);
            verify(callHistoryService, times(1)).save(any(), any(), eq("{\"response\":\"success\"}"));
            verify(joinPoint, times(1)).proceed();
        }
    }

    @Test
    void testRecord_SuccessfulResponseOnPostMethod() throws Throwable {

        var request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/call-history");
        when(request.getMethod()).thenReturn("POST");

        var mockRequestAttribs = mock(ServletRequestAttributes.class);
        when(mockRequestAttribs.getRequest()).thenReturn(request);
        when(objectMapper.writeValueAsString(any(Object.class))).thenReturn("{\"response\":\"success\"}");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"{\"num1\":1, \"num2\":2}"});
        when(joinPoint.proceed()).thenReturn(ResponseEntity.ok().build());

        try(MockedStatic<RequestContextHolder> staticMock = mockStatic(RequestContextHolder.class)) {
            staticMock.when(RequestContextHolder::getRequestAttributes).thenReturn(mockRequestAttribs);

            var response = httpEventInterceptor.record(joinPoint);

            Assertions.assertNotNull(response);
            verify(callHistoryService, times(1)).save(any(), any(), eq("{\"response\":\"success\"}"));
            verify(joinPoint, times(1)).proceed();
        }
    }

    @Test
    void testRecord_ExceptionThrown() throws Throwable {

        var request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/calc");
        when(request.getMethod()).thenReturn("PUT");

        var mockRequestAttribs = mock(ServletRequestAttributes.class);
        when(mockRequestAttribs.getRequest()).thenReturn(request);

        var throwable = new RuntimeException("An error occurred");
        when(joinPoint.proceed()).thenThrow(throwable);

        try(MockedStatic<RequestContextHolder> staticMock = mockStatic(RequestContextHolder.class)) {
            staticMock.when(RequestContextHolder::getRequestAttributes).thenReturn(mockRequestAttribs);

            try {
                httpEventInterceptor.record(joinPoint);
            } catch (Throwable ex) {
                verify(callHistoryService, times(1)).save(any(), any(), eq("An error occurred"));
                verify(joinPoint, times(1)).proceed();
                assert (ex == throwable);
            }
        }
    }

}
