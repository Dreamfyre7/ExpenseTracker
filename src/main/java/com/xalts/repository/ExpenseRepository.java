package com.xalts.repository;

import com.xalts.model.Expense;
import com.xalts.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser(User user);

    List<Expense> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);

    List<Expense> findByUser_Email(String email);
    
    List<Expense> findByUser_EmailAndDateBetween(String email, LocalDate start, LocalDate end);

    Page<Expense> findByUser_Email(String email, Pageable pageable);


}
