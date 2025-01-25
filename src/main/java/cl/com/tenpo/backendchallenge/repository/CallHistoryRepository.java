package cl.com.tenpo.backendchallenge.repository;

import cl.com.tenpo.backendchallenge.entity.CallHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CallHistoryRepository extends JpaRepository<CallHistory, Long> {
}
