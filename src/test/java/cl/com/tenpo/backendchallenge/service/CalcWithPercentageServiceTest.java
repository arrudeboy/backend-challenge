package cl.com.tenpo.backendchallenge.service;

import cl.com.tenpo.backendchallenge.service.external.HttpPercentageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class CalcWithPercentageServiceTest {

    @Mock
    private HttpPercentageService percentageService;

    private CalcWithPercentageService calcService;

    AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        calcService = new CalcWithPercentageService(percentageService);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void testCalculate_WithWholeNumberResult() {
        double num1 = 100;
        double num2 = 50;
        double percentage = 10;  // 10% of 150 (sum) = 15
        when(percentageService.retrievePercentage()).thenReturn(percentage);

        Number result = calcService.calculate(num1, num2);

        assertEquals(165, result);  // 150 + 15 = 165, which is a whole number
    }

    @Test
    void testCalculate_WithFractionalResult() {
        double num1 = 100;
        double num2 = 75;
        double percentage = 25;  // 25% of 175 (sum) = 43.75
        when(percentageService.retrievePercentage()).thenReturn(percentage);

        Number result = calcService.calculate(num1, num2);

        assertEquals(218.75, result);  // 175 + 43.75 = 218.75, fractional result
        assertInstanceOf(Double.class, result);
    }

    @Test
    void testCalculate_WithPercentageZero() {
        double num1 = 100;
        double num2 = 50;
        double percentage = 0;  // 0% of 150 (sum) = 0
        when(percentageService.retrievePercentage()).thenReturn(percentage);

        Number result = calcService.calculate(num1, num2);

        assertEquals(150, result);  // 150 + 0 = 150, which is a whole number
    }

    @Test
    void testCalculate_WithNegativeNumbers() {
        double num1 = -100;
        double num2 = -50;
        double percentage = 10;  // 10% of -150 (sum) = -15
        when(percentageService.retrievePercentage()).thenReturn(percentage);

        Number result = calcService.calculate(num1, num2);

        assertEquals(-165, result);  // -150 + (-15) = -165, which is a whole number
    }

    @Test
    void testCalculate_WithPercentageGreaterThan100() {
        double num1 = 100;
        double num2 = 100;
        double percentage = 150;  // 150% of 200 (sum) = 300
        when(percentageService.retrievePercentage()).thenReturn(percentage);

        Number result = calcService.calculate(num1, num2);

        assertEquals(500, result);  // 200 + 300 = 500, which is a whole number
    }
}
