# LedgerWise

**LedgerWise** is a personal finance tracker built with Java 21 and Spring Boot. Record income and expenses, create monthly category budgets, and review monthly spending summaries through a responsive web interface.

## Features

- Record income and expenses with category, date, amount, and notes.
- View a paginated chronological transaction ledger, edit or delete entries, and search by description or notes.
- Filter transactions by type, category, and date range.
- Export the currently filtered ledger to CSV, with spreadsheet formula injection protection.
- Create monthly spending limits by category.
- Track income, expenses, net balance, and budget progress.
- Review monthly totals grouped by expense category.
- Store data locally with H2 or use PostgreSQL.
- Validate form input and run tests through GitHub Actions.

## Tech stack

- Java 21
- Spring Boot 3.5
- Spring MVC + Thymeleaf
- Spring Data JPA / Hibernate
- H2 (default) and PostgreSQL
- Maven, JUnit 5, GitHub Actions

## Run locally

1. Install JDK 21 and Maven 3.9+.
2. Clone this repository.
3. Start the application:

   ```bash
   mvn spring-boot:run
   ```

4. Open [http://localhost:8080](http://localhost:8080).

The default H2 database is file-backed at `./data/ledgerwise`, so entries survive application restarts. The H2 console is available at [http://localhost:8080/h2-console](http://localhost:8080/h2-console) while the app is running. Use JDBC URL `jdbc:h2:file:./data/ledgerwise`, username `sa`, and an empty password.

## Run with PostgreSQL

Start PostgreSQL using Docker Compose:

```bash
docker compose up -d
```

Then launch Spring Boot with the PostgreSQL profile:

**Linux/macOS**
```bash
SPRING_PROFILES_ACTIVE=postgres mvn spring-boot:run
```

**PowerShell**
```powershell
$env:SPRING_PROFILES_ACTIVE="postgres"
mvn spring-boot:run
```

The default Compose credentials are for local development only. Override `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` for another database. Do not commit real credentials.

## Tests

```bash
mvn test
```

A GitHub Actions workflow runs `mvn --batch-mode verify` on pushes and pull requests to `main`.

## Application pages

| Route | Purpose |
|---|---|
| `/` | Monthly overview and recent transactions |
| `/transactions` | Transaction history |
| `/transactions/new` | Add an income or expense |
| `/transactions/export` | Download the currently filtered transactions as CSV |
| `/budgets` | Configure monthly category budgets |
| `/reports` | Monthly category spending summary |

## Project structure

```
src/main/java/com/akshat/ledgerwise/
  controller/    MVC route handlers
  model/         JPA entities and enums
  repository/    Spring Data repositories
  service/       Finance calculations and business logic
src/main/resources/
  templates/     Thymeleaf pages
  static/css/    Application styles
  application*.properties
src/test/        Automated tests
docs/            Design and architecture notes
```

## Scope and security

This is a local-first learning and portfolio project. It does not connect to banks, move money, or provide financial advice. Authentication and multi-user data isolation are not implemented in this version. **Do not deploy it publicly or store sensitive financial information in a shared environment without adding authentication, authorization, CSRF-aware security configuration, and appropriate operational controls.**

See [the project overview](docs/PROJECT_OVERVIEW.md) for the architecture and domain model.
