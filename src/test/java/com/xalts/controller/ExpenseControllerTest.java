package com.xalts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xalts.model.Expense;
import com.xalts.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService expenseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "john@example.com")
    void testGetAllExpensesWithPagination() throws Exception {
        Expense e = new Expense();
        e.setAmount(100.0);
        e.setCategory("Food");
        e.setDate(LocalDate.now());

        Page<Expense> page = new PageImpl<>(List.of(e));
        Mockito.when(expenseService.getAllExpenses(Mockito.anyString(), Mockito.any())).thenReturn(page);

        mockMvc.perform(get("/api/expenses?page=0&size=10")
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].category").value("Food"));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void testAddExpenseValidationFails() throws Exception {
        Expense invalid = new Expense();

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").exists())
                .andExpect(jsonPath("$.category").exists());
    }
}
