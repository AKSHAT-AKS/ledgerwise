package com.akshat.ledgerwise.model;
import jakarta.persistence.*; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.LocalDate;
@Entity @Table(name="financial_transactions")
public class FinancialTransaction {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank @Size(max=100) private String description;
 @NotNull @DecimalMin("0.01") @Digits(integer=10,fraction=2) private BigDecimal amount;
 @NotNull @Enumerated(EnumType.STRING) private TransactionType type;
 @NotNull @Enumerated(EnumType.STRING) private Category category;
 @NotNull private LocalDate transactionDate;
 @Size(max=500) private String notes;
 public Long getId(){return id;} public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;} public TransactionType getType(){return type;} public void setType(TransactionType v){type=v;}
 public Category getCategory(){return category;} public void setCategory(Category v){category=v;} public LocalDate getTransactionDate(){return transactionDate;} public void setTransactionDate(LocalDate v){transactionDate=v;}
 public String getNotes(){return notes;} public void setNotes(String v){notes=v;}
}