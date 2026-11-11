package com.akshat.ledgerwise.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonthlyBudgetValidationTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsAValidYearAndMonth() {
        MonthlyBudget budget = new MonthlyBudget();
        budget.setMonthKey("2026-11");

        assertTrue(validator.validateProperty(budget, "monthKey").isEmpty());
    }

    @Test
    void rejectsMonthsOutsideTheCalendarRange() {
        MonthlyBudget budget = new MonthlyBudget();
        budget.setMonthKey("2026-13");

        assertFalse(validator.validateProperty(budget, "monthKey").isEmpty());
    }
}