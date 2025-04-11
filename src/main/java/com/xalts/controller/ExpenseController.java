package com.xalts.controller;

import com.xalts.dto.MonthlyReportResponse;
import com.xalts.model.Expense;
import com.xalts.service.ExpenseService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    private String getAuthenticatedUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping
    public ResponseEntity<Expense> addExpense(@Valid @RequestBody Expense expense) {
        String email = getAuthenticatedUserEmail();
        return ResponseEntity.ok(expenseService.addExpense(email, expense));
    }

    @GetMapping
    public ResponseEntity<Page<Expense>> getAllExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return ResponseEntity.ok(expenseService.getAllExpenses(email, pageable));
    }


    @PutMapping("/{expenseId}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long expenseId, @Valid @RequestBody Expense expense) {
        String email = getAuthenticatedUserEmail();
        return ResponseEntity.ok(expenseService.updateExpense(email, expenseId, expense));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long expenseId) {
        String email = getAuthenticatedUserEmail();
        expenseService.deleteExpense(email, expenseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/range")
    public ResponseEntity<List<Expense>> getExpensesInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        String email = getAuthenticatedUserEmail();
        return ResponseEntity.ok(expenseService.getExpensesInRange(email, start, end));
    }
    
    @GetMapping("/category-summary")
    public ResponseEntity<Map<String, Double>> getCategorySummary() {
        String email = getAuthenticatedUserEmail();
        return ResponseEntity.ok(expenseService.getCategorySummary(email));
    }
    
    @GetMapping("/monthly-report")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(@RequestParam String month) {
        String email = getAuthenticatedUserEmail();
        YearMonth yearMonth = YearMonth.parse(month);
        return ResponseEntity.ok(expenseService.getMonthlyReport(email, yearMonth));
    }


}
