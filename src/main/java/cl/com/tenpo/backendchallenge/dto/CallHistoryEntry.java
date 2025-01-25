package cl.com.tenpo.backendchallenge.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CallHistoryEntry(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dateTime,
        String invokedEndpoint,
        String requestParameters,
        String response) {
}
