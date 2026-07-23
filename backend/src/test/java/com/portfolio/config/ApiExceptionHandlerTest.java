package com.portfolio.config;

import com.portfolio.controller.HealthController;
import com.portfolio.controller.PortfolioController;
import com.portfolio.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({HealthController.class, PortfolioController.class})
class ApiExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioService portfolioService;

    @Test
    void handleGeneral_returns500WithGenericMessage() throws Exception {
        when(portfolioService.getAll()).thenThrow(new NullPointerException("unexpected null"));

        mockMvc.perform(get("/api/portfolio"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong. Please try again later."));
    }

    @Test
    void handleGeneral_returns500ForArithmeticException() throws Exception {
        when(portfolioService.getAll()).thenThrow(new ArithmeticException("divide by zero"));

        mockMvc.perform(get("/api/portfolio"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong. Please try again later."));
    }

    @Test
    void handleGeneral_returns500ForIllegalStateException() throws Exception {
        when(portfolioService.getAll()).thenThrow(new IllegalStateException("session expired"));

        mockMvc.perform(get("/api/portfolio"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong. Please try again later."));
    }
}
