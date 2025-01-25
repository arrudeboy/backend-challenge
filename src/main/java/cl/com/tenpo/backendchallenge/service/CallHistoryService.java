package cl.com.tenpo.backendchallenge.service;

import cl.com.tenpo.backendchallenge.entity.CallHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public sealed interface CallHistoryService permits DatabaseCallHistoryService {

    Page<CallHistory> history(Pageable pageable);
    void save(String invokedEndpoint, String requestParameters, String response);
}
