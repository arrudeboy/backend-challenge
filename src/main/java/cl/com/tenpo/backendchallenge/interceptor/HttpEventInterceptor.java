package cl.com.tenpo.backendchallenge.interceptor;

import cl.com.tenpo.backendchallenge.service.CallHistoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Aspect
@Component
public class HttpEventInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpEventInterceptor.class);

    private final CallHistoryService callHistoryService;

    private final ObjectMapper objectMapper;

    public HttpEventInterceptor(CallHistoryService callHistoryService, ObjectMapper objectMapper) {
        this.callHistoryService = callHistoryService;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object record(ProceedingJoinPoint joinPoint) throws Throwable {

        var request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        var requestURI = request.getRequestURI();
        var requestParameters = switch (request.getMethod()) {
            case "GET" -> Arrays.toString(joinPoint.getArgs());
            case "POST" -> getRequestBody(joinPoint).orElse("");
            default -> "";
        };

        try {
            var response = joinPoint.proceed();
            var responseBody = objectMapper.writeValueAsString(response);
            callHistoryService.save(requestURI, requestParameters, responseBody);

            return response;
        } catch (Throwable throwable) {
            callHistoryService.save(requestURI, requestParameters, throwable.getMessage());
            throw throwable;
        }
    }

    private Optional<String> getRequestBody(ProceedingJoinPoint joinPoint) {
        var args = joinPoint.getArgs();
        if (args.length > 0) {
            try {
                return Arrays.stream(args)
                        .map(this::convertObjectToJson).map(Optional::orElseThrow)
                        .reduce((arg1, arg2) -> arg1 + ", " + arg2);

            } catch (Exception e) {
                LOGGER.error("Error serializing request body", e);
            }
        }
        return Optional.empty();
    }

    private Optional<String> convertObjectToJson(Object object) {
        try {
            return Optional.of(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object));
        } catch (JsonProcessingException e) {
            LOGGER.error("Error serializing object to JSON", e);
            return Optional.empty();
        }
    }

}
