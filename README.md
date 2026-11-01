# Banking Management System

A Java Swing desktop application demonstrating common banking operations, object-oriented design, transaction handling, local persistence, and core-logic tests.

> **Educational project only.** This is not production banking software. Do not use it to manage real money, real customer accounts, or sensitive financial information.

## Features

- Create savings, current, and student accounts
- Deposit, withdraw, and transfer funds
- Validate transaction amounts and account-specific balance rules
- Search, view, update, and close accounts
- View transaction history, including linked transfers
- Export account statements as CSV
- View dashboard metrics and daily transaction totals
- Persist local demo data using Java serialization
- Run a dependency-free core-logic regression test suite

## Tech stack

- Java (JDK 11+; JDK 17 or 21 recommended)
- Java Swing
- Object-oriented programming
- Java serialization and file I/O
- CSV export

## Requirements

- JDK 11 or newer
- VS Code with Extension Pack for Java, Eclipse, or another Java IDE

## Run the application

From the project root:

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

Override these with `BANK_ADMIN_USER` and `BANK_ADMIN_PASSWORD` environment variables before launching. This demo login is not secure authentication and must never be used in a real system.

## Run tests

The test harness has no third-party dependencies.

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

## Project structure

```text
src/
├── Application.java
├── Bank/          # Account models, bank operations, and transactions
├── Data/          # Local file persistence
├── Exceptions/    # Domain-specific exceptions
├── GUI/           # Swing screens and dialogs
└── img/           # Application UI assets
tests/             # Core-logic regression tests
screenshot/        # Application screenshots
```

## Data and security notes

- The application creates a local serialized data file named `data`. It is excluded from Git because it can contain account-holder names, account numbers, balances, and demo records.
- Never commit real customer information, credentials, exported statements, or local data files.
- The app uses `double` for money and Java serialization for persistence. A production system would require `BigDecimal` or integer minor units, a transactional database, secure password hashing, role-based access control, audit logging, concurrency controls, and extensive integration/security testing.
- Transfer validation is intended for a single-user desktop application, not concurrent multi-user banking.
- Closing an account simulates a cash payout; it does not move real money.

## License

No license is currently specified. Add a license file if you want others to reuse, modify, or distribute this project under explicit terms.
