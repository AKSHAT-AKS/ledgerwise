package com.akshat.ledgerwise.service;

import com.akshat.ledgerwise.model.*;
import com.akshat.ledgerwise.repository.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FinanceServiceTest {
    private final TransactionRepository transactions = mock(TransactionRepository.class);
    private final BudgetRepository budgets = mock(BudgetRepository.class);
    private final FinanceService finance = new FinanceService(transactions, budgets);

    @Test
    void totalAddsOnlyTransactionsOfRequestedTypeAndPeriod() {
        FinancialTransaction salary = transaction("Salary", "45000.00", TransactionType.INCOME);
        FinancialTransaction freelance = transaction("Freelance", "5000.00", TransactionType.INCOME);
        when(transactions.findByTypeAndTransactionDateBetween(
                TransactionType.INCOME, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30)))
                .thenReturn(List.of(salary, freelance));

        BigDecimal total = finance.total(TransactionType.INCOME,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30));

        assertEquals(new BigDecimal("50000.00"), total);
    }

    @Test
    void expenseTotalsGroupsAmountsByCategory() {
        FinancialTransaction lunch = transaction("Lunch", "250.00", TransactionType.EXPENSE);
        lunch.setCategory(Category.FOOD);
        FinancialTransaction groceries = transaction("Groceries", "750.00", TransactionType.EXPENSE);
        groceries.setCategory(Category.FOOD);
        FinancialTransaction cab = transaction("Cab", "180.00", TransactionType.EXPENSE);
        cab.setCategory(Category.TRANSPORT);
        when(transactions.findByTypeAndTransactionDateBetween(
                TransactionType.EXPENSE, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30)))
                .thenReturn(List.of(lunch, groceries, cab));

        var totals = finance.expenseTotals("2026-11");

        assertEquals(new BigDecimal("1000.00"), totals.get(Category.FOOD));
        assertEquals(new BigDecimal("180.00"), totals.get(Category.TRANSPORT));
    }

    private FinancialTransaction transaction(String description, String amount, TransactionType type) {
        FinancialTransaction t = new FinancialTransaction();
        t.setDescription(description);
        t.setAmount(new BigDecimal(amount));
        t.setType(type);
        t.setCategory(Category.OTHER);
        t.setTransactionDate(LocalDate.of(2026, 11, 10));
        return t;
    }
}
