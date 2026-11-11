package com.akshat.ledgerwise.model;
import jakarta.persistence.*; import jakarta.validation.constraints.*; import java.math.BigDecimal;
@Entity @Table(name="monthly_budgets",uniqueConstraints=@UniqueConstraint(columnNames={"month_key","category"}))
public class MonthlyBudget {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="month_key",nullable=false,length=7) @Pattern(regexp="\\d{4}-\\d{2}") private String monthKey;
 @Enumerated(EnumType.STRING) @NotNull private Category category;
 @NotNull @DecimalMin("0.01") @Digits(integer=10,fraction=2) private BigDecimal limitAmount;
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getMonthKey(){return monthKey;} public void setMonthKey(String v){monthKey=v;}
 public Category getCategory(){return category;} public void setCategory(Category v){category=v;} public BigDecimal getLimitAmount(){return limitAmount;} public void setLimitAmount(BigDecimal v){limitAmount=v;}
}