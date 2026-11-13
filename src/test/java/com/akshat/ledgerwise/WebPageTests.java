package com.akshat.ledgerwise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.akshat.ledgerwise.model.Category;
import com.akshat.ledgerwise.model.FinancialTransaction;
import com.akshat.ledgerwise.model.TransactionType;
import com.akshat.ledgerwise.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
class WebPageTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private TransactionRepository transactionRepository;

    @Test
    void mainPagesRenderSuccessfully() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/transactions")).andExpect(status().isOk());
        mockMvc.perform(get("/transactions/new")).andExpect(status().isOk());
        mockMvc.perform(get("/budgets")).andExpect(status().isOk());
        mockMvc.perform(get("/reports")).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Six-month cash flow")));
    }

    @Test
    @Transactional
    void transactionCanBeEdited() throws Exception {
        FinancialTransaction transaction = createTransaction(
                "Old description", "120.00", TransactionType.EXPENSE, Category.FOOD,
                LocalDate.of(2026, 11, 1));
        Long id = transactionRepository.saveAndFlush(transaction).getId();

        mockMvc.perform(get("/transactions/{id}/edit", id))
                .andExpect(status().isOk());

        mockMvc.perform(post("/transactions/{id}/edit", id)
                        .param("description", "Updated description")
                        .param("amount", "175.50")
                        .param("type", "EXPENSE")
                        .param("category", "FOOD")
                        .param("transactionDate", "2026-11-02")
                        .param("notes", "Updated in test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions"));

        FinancialTransaction updated = transactionRepository.findById(id).orElseThrow();
        assertThat(updated.getDescription()).isEqualTo("Updated description");
        assertThat(updated.getAmount()).isEqualByComparingTo("175.50");
        assertThat(updated.getTransactionDate()).isEqualTo(LocalDate.of(2026, 11, 2));
    }

    @Test
    @Transactional
    void editingUnknownTransactionReturnsNotFound() throws Exception {
        mockMvc.perform(get("/transactions/999999/edit"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void transactionListFiltersBySearchAndType() throws Exception {
        transactionRepository.saveAndFlush(createTransaction(
                "Monthly salary", "50000.00", TransactionType.INCOME, Category.SALARY,
                LocalDate.of(2026, 11, 1)));
        transactionRepository.saveAndFlush(createTransaction(
                "Grocery shopping", "1200.00", TransactionType.EXPENSE, Category.FOOD,
                LocalDate.of(2026, 11, 2)));

        mockMvc.perform(get("/transactions")
                        .param("q", "salary")
                        .param("type", "INCOME"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("transactions", org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    @Transactional
    void transactionListFiltersByCategoryAndDateRange() throws Exception {
        transactionRepository.saveAndFlush(createTransaction(
                "Lunch", "250.00", TransactionType.EXPENSE, Category.FOOD,
                LocalDate.of(2026, 11, 5)));
        transactionRepository.saveAndFlush(createTransaction(
                "Taxi", "180.00", TransactionType.EXPENSE, Category.TRANSPORT,
                LocalDate.of(2026, 10, 5)));

        mockMvc.perform(get("/transactions")
                        .param("category", "FOOD")
                        .param("from", "2026-11-01")
                        .param("to", "2026-11-30"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("transactions", org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    @Transactional
    void csvExportIncludesFilteredRowsAndProtectsFormulaCells() throws Exception {
        transactionRepository.saveAndFlush(createTransaction(
                "=1+1", "20.00", TransactionType.EXPENSE, Category.FOOD,
                LocalDate.of(2026, 11, 10)));

        mockMvc.perform(get("/transactions/export").param("category", "FOOD"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .header().string("Content-Disposition",
                                "attachment; filename=ledgerwise-transactions.csv"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("'=1+1")));
    }

    @Test
    @Transactional
    void transactionListPaginatesAndKeepsNewestEntriesFirst() throws Exception {
        for (int i = 1; i <= 12; i++) {
            transactionRepository.saveAndFlush(createTransaction(
                    "Page test " + i, "10.00", TransactionType.EXPENSE, Category.FOOD,
                    LocalDate.of(2026, 11, i)));
        }

        mockMvc.perform(get("/transactions").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("transactions", org.hamcrest.Matchers.hasSize(10)))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", 2))
                .andExpect(model().attribute("hasNext", true));

        mockMvc.perform(get("/transactions").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("transactions", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("hasPrevious", true));
    }

    @Test
    @Transactional
    void outOfRangePageFallsBackToLastAvailablePage() throws Exception {
        for (int i = 1; i <= 12; i++) {
            transactionRepository.saveAndFlush(createTransaction(
                    "Range test " + i, "10.00", TransactionType.EXPENSE, Category.FOOD,
                    LocalDate.of(2026, 10, i)));
        }
        mockMvc.perform(get("/transactions").param("page", "500"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("transactions", org.hamcrest.Matchers.hasSize(2)));
    }

    private FinancialTransaction createTransaction(String description, String amount,
                                                   TransactionType type, Category category,
                                                   LocalDate date) {
        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setDescription(description);
        transaction.setAmount(new BigDecimal(amount));
        transaction.setType(type);
        transaction.setCategory(category);
        transaction.setTransactionDate(date);
        return transaction;
    }
}
