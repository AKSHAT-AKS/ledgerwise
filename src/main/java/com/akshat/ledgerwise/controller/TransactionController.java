package com.akshat.ledgerwise.controller;

import com.akshat.ledgerwise.model.*;
import com.akshat.ledgerwise.service.FinanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller
@RequestMapping("/transactions")
public class TransactionController {
    private final FinanceService finance;

    public TransactionController(FinanceService finance) {
        this.finance = finance;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("transactions", finance.allTransactions());
        return "transactions";
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
            submitted.setTransactionDate(submitted.getTransactionDate());
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