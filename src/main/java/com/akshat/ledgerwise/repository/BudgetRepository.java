package com.akshat.ledgerwise.repository;
import com.akshat.ledgerwise.model.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface BudgetRepository extends JpaRepository<MonthlyBudget,Long> { List<MonthlyBudget> findByMonthKeyOrderByCategoryAsc(String monthKey); }