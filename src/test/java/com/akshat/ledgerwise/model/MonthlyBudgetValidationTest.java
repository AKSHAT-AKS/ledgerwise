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

        assertFalse(validator.validateProperty(budget, "monthKey").stream()
                .anyMatch(violation -> true));
    }

    @Test
    void rejectsMonthsOutsideTheCalendarRange() {
        MonthlyBudget budget = new MonthlyBudget();
        budget.setMonthKey("2026-13");

        assertTrue(validator.validateProperty(budget, "monthKey").stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("monthKey")));
    }
}