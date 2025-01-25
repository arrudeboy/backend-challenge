package cl.com.tenpo.backendchallenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.http.HttpClient;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class BackendChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendChallengeApplication.class, args);
    }

    @Bean
    public CacheManager cacheManager() {
        var cacheManager = new CaffeineCacheManager();
        cacheManager
                .setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES));

        return cacheManager;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

}
