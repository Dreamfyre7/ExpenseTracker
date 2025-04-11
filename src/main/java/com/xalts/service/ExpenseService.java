package com.xalts.service;

import com.xalts.dto.MonthlyReportResponse;
import com.xalts.exception.ResourceNotFoundException;
import com.xalts.model.Expense;
import com.xalts.model.User;
import com.xalts.repository.ExpenseRepository;
import com.xalts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    public Expense addExpense(String email, Expense expense) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    public Page<Expense> getAllExpenses(String email, Pageable pageable) {
        return expenseRepository.findByUser_Email(email, pageable);
    }



    public Expense updateExpense(String email, Long expenseId, Expense updatedExpense) {
        Expense existing = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + expenseId));

        if (!existing.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Access Denied: You can only update your own expense");
        }

        existing.setAmount(updatedExpense.getAmount());
        existing.setCategory(updatedExpense.getCategory());
        existing.setDescription(updatedExpense.getDescription());
        existing.setDate(updatedExpense.getDate());

        return expenseRepository.save(existing);
    }

    public void deleteExpense(String email, Long expenseId) {
        Expense existing = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + expenseId));

        if (!existing.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Access Denied: You can only delete your own expense");
        }

        expenseRepository.deleteById(expenseId);
    }

    public List<Expense> getExpensesInRange(String email, LocalDate start, LocalDate end) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return expenseRepository.findByUserAndDateBetween(user, start, end);
    }
    
    public Map<String, Double> getCategorySummary(String email) {
        List<Expense> expenses = expenseRepository.findByUser_Email(email);
        return expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));
    }
    
    public MonthlyReportResponse getMonthlyReport(String email, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        List<Expense> expenses = expenseRepository.findByUser_EmailAndDateBetween(email, start, end);

        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        Map<String, Double> breakdown = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        return new MonthlyReportResponse(month.toString(), total, breakdown);
    }


}
