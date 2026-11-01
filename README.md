# Banking Management System

A Java Swing desktop application that demonstrates banking workflows, account-specific rules, transaction records, local file persistence, and a dependency-free regression test harness.

## Screenshots

| Login | Dashboard | Add Account |
|---|---|---|
| ![Login screen](screenshots/login.png) | ![Dashboard](screenshots/dashboard.png) | ![Add account screen](screenshots/add-account.png) |

> **Educational/demo project only.** This is not production banking software. Do not use it to store real customer data, credentials, or money.

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

- Java (JDK 11 or newer; JDK 17 recommended)
- Java Swing
- Object-oriented programming
- Java serialization and file I/O
- CSV export

## Run the application

Open a terminal in the project root (the folder containing `src/` and `README.md`).

**Windows PowerShell**
```powershell
New-Item -ItemType Directory -Force bin | Out-Null
javac -encoding UTF-8 -d bin -sourcepath src src\Application.java
java -cp bin Application
```

**macOS / Linux**
```bash
mkdir -p bin
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
New-Item -ItemType Directory -Force bin | Out-Null
javac -encoding UTF-8 -d bin -sourcepath src src\Application.java tests\BankingSystemTests.java
java -ea -cp bin BankingSystemTests
```

**macOS / Linux**
```bash
mkdir -p bin
javac -encoding UTF-8 -d bin -sourcepath src src/Application.java tests/BankingSystemTests.java
java -ea -cp bin BankingSystemTests
```

The regression suite covers account lookup, deposits, invalid amounts, minimum balance, transfers, transfer limits, dashboard metrics, transaction history, account closure, defensive copies, and serialization round-tripping.

## Project structure

```text
src/
├── Application.java
├── Bank/          # Account models, bank operations, transactions
├── Data/          # Local file persistence
├── Exceptions/    # Domain-specific exceptions
└── GUI/           # Swing windows and dialogs
tests/             # Core-logic regression tests
```

## Data and security notes

- The app stores local records in a serialized `data` file. Local data files are intentionally excluded from version control because they may contain account names, account numbers, balances, and transaction records.
- Do not commit real customer details, passwords, exported statements, or local data files.
- This project uses `double` for money and Java serialization for persistence. A production design should use `BigDecimal` or integer minor units, a transactional database, secure password hashing, role-based authorization, audit controls, concurrency handling, and thorough integration/security tests.
- Transfer validation is designed for a single-user desktop application, not concurrent multi-user banking.
- Account closure simulates a cash payout; no real funds are moved.

## Future improvements

- Add a MySQL-backed repository and explicit transaction boundaries
- Separate UI, service, and persistence layers more strictly
- Replace demo authentication with secure password hashing and user roles
- Add GUI-level and integration tests

## License

No license is currently specified. Add a license before inviting reuse or redistribution.
