package cl.com.tenpo.backendchallenge.controller;

import cl.com.tenpo.backendchallenge.entity.CallHistory;
import cl.com.tenpo.backendchallenge.service.CallHistoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("call-history")
public class CallHistoryController {

    private final CallHistoryService callHistoryService;

    public CallHistoryController(CallHistoryService callHistoryService) {
        this.callHistoryService = callHistoryService;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<?> history(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {

        try {
            var history = callHistoryService.history(
                    PageRequest
                            .of(page, size)
                            .withSort(Sort.by(DESC, "dateTime")));

            if (history.isEmpty())
                return ResponseEntity.ok(Collections.emptyList());

            var historyEntries =  history
                    .stream()
                    .map(CallHistory::toEntry)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(historyEntries);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid page number or page size provided");
        }
    }

}
