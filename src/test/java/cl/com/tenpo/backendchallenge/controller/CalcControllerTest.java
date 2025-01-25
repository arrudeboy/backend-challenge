package cl.com.tenpo.backendchallenge.controller;

import cl.com.tenpo.backendchallenge.dto.CalcInput;
import cl.com.tenpo.backendchallenge.service.CalcWithPercentageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class CalcControllerTest {

    @Mock
    private CalcWithPercentageService calcService;

    @InjectMocks
    private CalcController calcController;

    private MockMvc mockMvc;

    AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(calcController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void testCalc() throws Exception {
        CalcInput<? extends Number> calcInput = new CalcInput<>(5, 3);
        when(calcService.calculate(calcInput.num1(), calcInput.num2())).thenReturn(8);

        mockMvc.perform(post("/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"num1\": 5, \"num2\": 3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(8));
    }

    @Test
    void testCalcValidationError() throws Exception {
        String invalidInput = "{\"num1\": null, \"num2\": 3}";

        mockMvc.perform(post("/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidInput))
                .andExpect(status().isBadRequest());
    }
}
