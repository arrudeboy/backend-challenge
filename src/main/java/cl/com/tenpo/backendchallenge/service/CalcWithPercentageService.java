package cl.com.tenpo.backendchallenge.service;

import cl.com.tenpo.backendchallenge.service.external.PercentageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class CalcWithPercentageService implements CalcService {

    private final PercentageService percentageService;

    public CalcWithPercentageService(@Autowired PercentageService percentageService) {
        this.percentageService = percentageService;
    }

    @Override
    public Number calculate(Number num1, Number num2) {
        var sum = num1.doubleValue() + num2.doubleValue();
        var result = sum + (sum * (percentageService.retrievePercentage() / 100));
        if (result % 1 == 0)
            return (int) result;

        return result;
    }
}
