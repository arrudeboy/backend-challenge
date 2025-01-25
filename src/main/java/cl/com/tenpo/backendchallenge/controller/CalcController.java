package cl.com.tenpo.backendchallenge.controller;

import cl.com.tenpo.backendchallenge.dto.CalcInput;
import cl.com.tenpo.backendchallenge.service.CalcService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("calc")
public class CalcController {

    private final CalcService calcService;

    public CalcController(CalcService calcService) {
        this.calcService = calcService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<? extends Map<String, ?>> calc(@RequestBody @Valid CalcInput<? extends Number> calcInput) {

        var result = calcService.calculate(calcInput.num1(), calcInput.num2());
        return ResponseEntity.ok(Map.of("result", result));
    }
}
