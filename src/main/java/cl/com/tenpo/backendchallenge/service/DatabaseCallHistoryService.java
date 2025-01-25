package cl.com.tenpo.backendchallenge.service;

import cl.com.tenpo.backendchallenge.entity.CallHistory;
import cl.com.tenpo.backendchallenge.repository.CallHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public final class DatabaseCallHistoryService implements CallHistoryService {

    private final CallHistoryRepository callHistoryRepository;

    public DatabaseCallHistoryService(CallHistoryRepository callHistoryRepository) {
        this.callHistoryRepository = callHistoryRepository;
    }

    @Override
    public Page<CallHistory> history(Pageable pageable) {
        return callHistoryRepository.findAll(pageable);
    }

    @Override
    public void save(String invokedEndpoint, String requestParameters, String response) {
        callHistoryRepository.save(
                new CallHistory(LocalDateTime.now(), invokedEndpoint, requestParameters, response));
    }
}
