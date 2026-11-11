package com.akshat.ledgerwise.controller;

import com.akshat.ledgerwise.model.*;
import com.akshat.ledgerwise.service.FinanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionController {
    private final FinanceService finance;

    public TransactionController(FinanceService finance) {
        this.finance = finance;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) TransactionType type,
                       @RequestParam(required = false) Category category,
                       @RequestParam(required = false) LocalDate from,
                       @RequestParam(required = false) LocalDate to, Model model) {
        List<FinancialTransaction> filtered = filteredTransactions(q, type, category, from, to);
        model.addAttribute("transactions", filtered);
        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("selectedType", type);
        model.addAttribute("types", TransactionType.values());
        model.addAttribute("categories", Category.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "transactions";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam(required = false) String q,
                                         @RequestParam(required = false) TransactionType type,
                                         @RequestParam(required = false) Category category,
                                         @RequestParam(required = false) LocalDate from,
                                         @RequestParam(required = false) LocalDate to) {
        StringBuilder csv = new StringBuilder();
        csv.append("Date,Type,Category,Description,Amount,Notes\r\n");
        for (FinancialTransaction transaction : filteredTransactions(q, type, category, from, to)) {
            csv.append(csvCell(transaction.getTransactionDate().toString(), false)).append(',')
                    .append(csvCell(transaction.getType().name(), false)).append(',')
                    .append(csvCell(transaction.getCategory().label(), false)).append(',')
                    .append(csvCell(transaction.getDescription(), true)).append(',')
                    .append(csvCell(transaction.getAmount().toPlainString(), false)).append(',')
                    .append(csvCell(transaction.getNotes(), true)).append("\r\n");
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ledgerwise-transactions.csv")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private List<FinancialTransaction> filteredTransactions(String q, TransactionType type,
                                                              Category category, LocalDate from, LocalDate to) {
        return finance.allTransactions().stream()
                .filter(t -> q == null || q.isBlank()
                        || t.getDescription().toLowerCase().contains(q.trim().toLowerCase())
                        || (t.getNotes() != null && t.getNotes().toLowerCase().contains(q.trim().toLowerCase())))
                .filter(t -> type == null || t.getType() == type)
                .filter(t -> category == null || t.getCategory() == category)
                .filter(t -> from == null || !t.getTransactionDate().isBefore(from))
                .filter(t -> to == null || !t.getTransactionDate().isAfter(to))
                .toList();
    }

    private String csvCell(String value, boolean protectFormula) {
        String safe = value == null ? "" : value;
        if (protectFormula && !safe.isEmpty()) {
            char first = safe.stripLeading().charAt(0);
            if (first == '=' || first == '+' || first == '-' || first == '@') {
                safe = "'" + safe;
            }
        }
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    @GetMapping("/new")
    public String form(Model model) {
        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setTransactionDate(LocalDate.now());
        transaction.setType(TransactionType.EXPENSE);
        prepareForm(model, transaction, false);
        return "transaction-form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("transaction") FinancialTransaction transaction,
                       BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepareForm(model, transaction, false);
            return "transaction-form";
        }
        finance.saveTransaction(transaction);
        redirect.addFlashAttribute("success", "Transaction saved.");
        return "redirect:/transactions";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        prepareForm(model, getTransaction(id), true);
        return "transaction-form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("transaction") FinancialTransaction submitted,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        FinancialTransaction existing = getTransaction(id);
        if (result.hasErrors()) {
            prepareForm(model, submitted, true);
            return "transaction-form";
        }
        existing.setDescription(submitted.getDescription());
        existing.setAmount(submitted.getAmount());
        existing.setType(submitted.getType());
        existing.setCategory(submitted.getCategory());
        existing.setTransactionDate(submitted.getTransactionDate());
        existing.setNotes(submitted.getNotes());
        finance.saveTransaction(existing);
        redirect.addFlashAttribute("success", "Transaction updated.");
        return "redirect:/transactions";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        finance.deleteTransaction(id);
        redirect.addFlashAttribute("success", "Transaction deleted.");
        return "redirect:/transactions";
    }

    private FinancialTransaction getTransaction(Long id) {
        return finance.findTransaction(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private void prepareForm(Model model, FinancialTransaction transaction, boolean editing) {
        model.addAttribute("transaction", transaction);
        model.addAttribute("categories", Category.values());
        model.addAttribute("types", TransactionType.values());
        model.addAttribute("editing", editing);
    }
}