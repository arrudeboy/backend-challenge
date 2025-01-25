package cl.com.tenpo.backendchallenge.service.external;

import cl.com.tenpo.backendchallenge.exception.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Service
public class HttpPercentageService implements PercentageService {

    private final CacheManager cacheManager;

    private final URI uri;
    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    public HttpPercentageService(@Value("${percentage.service.url}") String url, CacheManager cacheManager, ObjectMapper objectMapper) {
        uri = URI.create(url);
        httpClient = HttpClient.newHttpClient();
        this.cacheManager = cacheManager;
        this.objectMapper = objectMapper;
    }

    @Retryable
    private double fetchPercentageFromExternalService() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(uri).GET().build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var responseBody = response.body();
        int statusCode = response.statusCode();
        if (statusCode != 200)
            throw new ServiceException(responseBody);

        return objectMapper.readTree(responseBody).get("percentage").asDouble();
    }

    private double fetchPercentage() {
        try {
            return fetchPercentageFromExternalService();

        } catch (IOException | InterruptedException e) {
            return Optional.ofNullable(
                    cacheManager.getCache("percentage"))
                    .map(cache -> cache.get("percentage", Double.class))
                    .orElse(10.0);
        }
    }

    @Override
    @Cacheable("percentage")
    public double retrievePercentage() {
        return fetchPercentage();
    }

}
