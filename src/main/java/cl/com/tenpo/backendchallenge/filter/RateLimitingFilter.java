package cl.com.tenpo.backendchallenge.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Component
public class RateLimitingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitingFilter.class);

    private static final long THIRTY_MINUTES_IN_MILLIS = 30 * 60 * 1000;

    private final Map<String, MaxRequestsPerMinuteTrack> requestCountsPerIpAddress = new ConcurrentHashMap<>();

    @Value("${rate.limiting.rpm}")
    private Integer maxRequestsPerMinute;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        var request = (HttpServletRequest) servletRequest;
        var response = (HttpServletResponse) servletResponse;

        var clientIpAddress = request.getRemoteAddr();
        requestCountsPerIpAddress.putIfAbsent(clientIpAddress, new MaxRequestsPerMinuteTrack(System.currentTimeMillis(), new AtomicInteger(0)));
        var maxRequestsPerMinuteTrack = requestCountsPerIpAddress.get(clientIpAddress);
        var requests = maxRequestsPerMinuteTrack.requestCounter().incrementAndGet();

        if (requests > maxRequestsPerMinute) {
            response.setStatus(TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests. Please try again later.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.MINUTES)
    public void resetRequestCounts() {
        long currentTime = System.currentTimeMillis();
        requestCountsPerIpAddress.entrySet()
                .removeIf(
                        entry -> currentTime - entry.getValue().timestamp() > THIRTY_MINUTES_IN_MILLIS);

        LOGGER.info("Max Request Counters for outdated IP addresses have been reset");
    }

    record MaxRequestsPerMinuteTrack(long timestamp, AtomicInteger requestCounter) {}
}
