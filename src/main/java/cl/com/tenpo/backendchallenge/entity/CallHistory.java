package cl.com.tenpo.backendchallenge.entity;

import cl.com.tenpo.backendchallenge.dto.CallHistoryEntry;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class CallHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    LocalDateTime dateTime;

    String invokedEndpoint;

    String requestParameters;

    String response;

    public CallHistory(LocalDateTime dateTime, String invokedEndpoint, String requestParameters, String response) {
        this.dateTime = dateTime;
        this.invokedEndpoint = invokedEndpoint;
        this.requestParameters = requestParameters;
        this.response = response;
    }

    public static CallHistoryEntry toEntry(CallHistory callHistory) {
        return new CallHistoryEntry(
                callHistory.getDateTime(),
                callHistory.getInvokedEndpoint(),
                callHistory.getRequestParameters(),
                callHistory.getResponse());
    }

    @PrePersist
    @PreUpdate
    private void truncateResponse() {
        if (this.response != null && this.response.length() > 255) {
            this.response = this.response.substring(0, 255);
        }
    }

}
