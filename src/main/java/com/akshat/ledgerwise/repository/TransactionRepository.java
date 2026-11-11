package com.akshat.ledgerwise.repository;
import com.akshat.ledgerwise.model.*; import org.springframework.data.jpa.repository.JpaRepository; import java.time.LocalDate; import java.util.List;
public interface TransactionRepository extends JpaRepository<FinancialTransaction,Long> {
 List<FinancialTransaction> findAllByOrderByTransactionDateDescIdDesc();
 List<FinancialTransaction> findByTypeAndTransactionDateBetween(TransactionType type, LocalDate start, LocalDate end);
}