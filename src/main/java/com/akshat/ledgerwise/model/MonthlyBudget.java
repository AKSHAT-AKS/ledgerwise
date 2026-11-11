package com.akshat.ledgerwise.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "monthly_budgets",
        uniqueConstraints = @UniqueConstraint(columnNames = {"month_key", "category"}))
public class MonthlyBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "month_key", nullable = false, length = 7)
    @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])",
            message = "Month must be a valid month in YYYY-MM format")
    private String monthKey;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Category category;

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal limitAmount;

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public String getMonthKey() { return monthKey; }
    public void setMonthKey(String value) { monthKey = value; }
    public Category getCategory() { return category; }
    public void setCategory(Category value) { category = value; }
    public BigDecimal getLimitAmount() { return limitAmount; }
    public void setLimitAmount(BigDecimal value) { limitAmount = value; }
}