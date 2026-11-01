# Banking Management System

A Java Swing desktop application that demonstrates common banking workflows, object-oriented programming, account-specific rules, transaction records, local file persistence, and a lightweight regression test harness.

> **Learning/demo project only.** This is not production banking software. Do not use it to store real customer data, credentials, or money.

## Screenshots

| Login screen | Main dashboard | Add account |
|---|---|---|
| ![Login screen](screenshots/login.png) | ![Main dashboard](screenshots/dashboard.png) | ![Add account](screenshots/add-account.png) |

## Features

- Create savings, current, and student accounts
- Deposit and withdraw with amount and account-rule validation
- Transfer between two different accounts
- Search, display, update, and close accounts
- View transaction history, including linked transfer records
- Export account statements to CSV
- Dashboard metrics for account types, balances, and daily transactions
- Per-transfer and per-account daily outgoing transfer limits
- Local serialized persistence
- Core-logic regression tests without third-party test dependencies

## Technology

- Java (JDK 11 or newer; JDK 17/21 recommended)
- Java Swing
- Object-oriented programming
- Java serialization and file I/O
- CSV export

## Requirements

- JDK 11+
- VS Code with Extension Pack for Java, Eclipse, or another Java IDE

## Run the application

Open a terminal in the project root (the folder containing `src/` and `README.md`).

**Windows PowerShell**
```powershell
javac -encoding UTF-8 -d bin -sourcepath src src\Application.java
java -cp bin Application
```

**macOS / Linux**
```bash
javac -encoding UTF-8 -d bin -sourcepath src src/Application.java
java -cp bin Application
```

### Demo login

- Username: `admin`
- Password: `admin`

The application also supports the `BANK_ADMIN_USER` and `BANK_ADMIN_PASSWORD` environment variables. The default/demo login is not suitable for real authentication.

## Run tests

The test harness has no third-party dependency.

**Windows PowerShell**
```powershell
javac -encoding UTF-8 -d bin -sourcepath src src\Application.java tests\BankingSystemTests.java
java -ea -cp bin BankingSystemTests
```

**macOS / Linux**
```bash
javac -encoding UTF-8 -d bin -sourcepath src src/Application.java tests/BankingSystemTests.java
java -ea -cp bin BankingSystemTests
```

The test harness covers account lookup, deposits, invalid amounts, minimum balance, transfers, transfer limits, dashboard metrics, transaction history, account closure, defensive copies, and serialization round-tripping.

## Project structure

```text
src/
├── Application.java
├── Bank/          # Account models, bank operations, transactions
├── Data/          # Local file persistence
├── Exceptions/    # Domain-specific exceptions
├── GUI/           # Swing windows and dialogs
└── img/           # UI assets
tests/             # Core-logic regression tests
screenshots/       # Application screenshots
Bank.pdf           # Supplied project documentation
```

## Data and security notes

- The app stores local records in a serialized `data` file. Local data files are intentionally excluded from version control because they may contain account names, account numbers, balances, and transaction records.
- Do not commit real customer details, passwords, exported statements, or local data files.
- This project uses `double` for money and Java serialization for persistence. A production design should use `BigDecimal` or integer minor units, a transactional database, secure password hashing, role-based authorization, audit controls, concurrency handling, and thorough integration/security tests.
- Transfer validation is designed for a single-user desktop application, not concurrent multi-user banking.
- Account closure simulates a cash payout; no real funds are moved.

## Potential next improvements

- Add a MySQL-backed repository and explicit transaction boundaries
- Separate UI, service, and persistence layers more strictly
- Replace demo authentication with secure password hashing and user roles
- Add CI to compile and run the test harness on every push
- Add input-validation and GUI-level tests

## License

No license is currently specified. Add a license before inviting reuse or redistribution.
