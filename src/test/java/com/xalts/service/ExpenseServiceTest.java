package com.xalts.service;

import com.xalts.model.Expense;
import com.xalts.model.User;
import com.xalts.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {

    @InjectMocks
    private ExpenseService expenseService;

    @Mock
    private ExpenseRepository expenseRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllExpenses() {
        String email = "john@example.com";
        Pageable pageable = PageRequest.of(0, 10);
        Expense expense = new Expense();
        expense.setAmount(100.0);
        expense.setCategory("Food");
        expense.setDate(LocalDate.now());

        Page<Expense> page = new PageImpl<>(List.of(expense));
        when(expenseRepository.findByUser_Email(email, pageable)).thenReturn(page);

        Page<Expense> result = expenseService.getAllExpenses(email, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Food", result.getContent().get(0).getCategory());
    }

    @Test
    void testGetExpensesInRange() {
        String email = "john@example.com";
        LocalDate start = LocalDate.of(2025, 4, 1);
        LocalDate end = LocalDate.of(2025, 4, 30);
        Expense expense = new Expense();
        expense.setAmount(100.0);
        expense.setCategory("Travel");

        when(expenseRepository.findByUser_EmailAndDateBetween(email, start, end)).thenReturn(List.of(expense));

        List<Expense> result = expenseService.getExpensesInRange(email, start, end);
        assertEquals(1, result.size());
        assertEquals("Travel", result.get(0).getCategory());
    }

    @Test
    void testGetCategorySummary() {
        String email = "john@example.com";
        Expense food = new Expense();
        food.setAmount(200.0);
        food.setCategory("Food");

        Expense travel = new Expense();
        travel.setAmount(300.0);
        travel.setCategory("Travel");

        when(expenseRepository.findByUser_Email(email)).thenReturn(List.of(food, travel));

        Map<String, Double> result = expenseService.getCategorySummary(email);
        assertEquals(2, result.size());
        assertEquals(200.0, result.get("Food"));
        assertEquals(300.0, result.get("Travel"));
    }

    @Test
    void testGetMonthlyReport() {
        String email = "john@example.com";
        YearMonth month = YearMonth.of(2025, 4);
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        Expense food = new Expense();
        food.setAmount(150.0);
        food.setCategory("Food");

        Expense travel = new Expense();
        travel.setAmount(250.0);
        travel.setCategory("Travel");

        when(expenseRepository.findByUser_EmailAndDateBetween(email, start, end)).thenReturn(List.of(food, travel));

        var result = expenseService.getMonthlyReport(email, month);

        assertEquals("2025-04", result.getMonth());
        assertEquals(400.0, result.getTotal());
        assertEquals(150.0, result.getCategoryBreakdown().get("Food"));
        assertEquals(250.0, result.getCategoryBreakdown().get("Travel"));
    }
}
