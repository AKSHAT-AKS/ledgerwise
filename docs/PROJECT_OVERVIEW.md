# LedgerWise — project overview

## Goal
A local-first personal finance tracker for recording income and expenses, setting monthly category budgets, and reviewing spending patterns.

## Architecture
- **Presentation:** Spring MVC controllers and Thymeleaf templates.
- **Application logic:** FinanceService aggregates transactions and budget progress.
- **Persistence:** Spring Data JPA repositories.
- **Database:** H2 file database by default; PostgreSQL profile for persistent deployment.

## Domain
- **FinancialTransaction:** description, amount, type, category, date, and optional notes.
- **MonthlyBudget:** one spending limit per category and month.
- **Category / TransactionType:** enums that keep values consistent.

## Routes
- `/` dashboard
- `/transactions` transaction history
- `/transactions/new` add a transaction
- `/budgets` monthly category budgets
- `/reports` monthly category totals

## Scope and safety
This is a local personal finance tracker, not a banking product. It does not connect to bank accounts, move money, or provide financial advice. Authentication is not implemented in this first version; do not expose it publicly with real financial data.
