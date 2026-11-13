package com.akshat.ledgerwise.controller;

import com.akshat.ledgerwise.service.FinanceService;
import java.time.YearMonth;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reports")
public class ReportController {
    private final FinanceService finance;

    public ReportController(FinanceService finance) {
        this.finance = finance;
    }

    @GetMapping
    public String report(@RequestParam(required = false) String month, Model model) {
        String selected = (month == null || month.isBlank())
                ? YearMonth.now().toString() : month;
        Map<String, Object> data = finance.dashboard(selected);
        model.addAttribute("month", selected);
        model.addAttribute("income", data.get("income"));
        model.addAttribute("expense", data.get("expense"));
        model.addAttribute("balance", data.get("balance"));
        model.addAttribute("categoryTotals", data.get("categoryTotals"));
        model.addAttribute("cashFlow", finance.cashFlowTrend(selected));
        return "reports";
    }
}
