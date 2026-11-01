# Security Review and Known Limitations

## Intended use

This project is an educational single-user desktop demonstration. It is not suitable for real banking, real customer information, or real financial transactions.

## Current limitations observed in the source

| Area | Current implementation | Why it matters |
|---|---|---|
| Authentication | Demo username/password defaults to `admin/admin`; optional values come from environment variables | Environment variables do not provide password hashing, identity management, or secure account recovery |
| Authorization | One administrator-style login | No user roles, customer access controls, or permission checks per operation |
| Money representation | Balances and amounts use Java `double` | Binary floating-point is not appropriate for precise financial accounting |
| Persistence | Java serialization to a local `data` file | No database constraints, database transaction support, or multi-user consistency |
| Deserialization | The data file is read using `ObjectInputStream` | Serialized files must be treated as trusted local input and protected from tampering |
| Concurrency | In-memory state is used by a single desktop process | No locking or concurrency strategy for multiple simultaneous users |
| Audit | Transactions are stored in the same serialized application state | This is not a tamper-evident or externally retained audit ledger |
| Account closure | Remaining balance is recorded as a simulated cash payout | No external payment or settlement actually takes place |

## Safe demo practices

- Use fictional account-holder names and test balances only.
- Keep the `data` file and backups on a trusted local machine.
- Never commit `data`, `data.backup`, exported statements, or real credentials.
- Do not reuse personal or production passwords for the demo login.
- Do not expose this application as a real banking service.

## Recommended hardening order

1. Replace `double` with `BigDecimal` or integer minor units and define rounding rules.
2. Replace demo authentication with a proper identity component, password hashing, account lockout policy, and role-based authorization.
3. Replace Java serialization with a database-backed repository and enforce operations using database transactions and constraints.
4. Add a service layer between Swing screens and the domain/persistence code.
5. Add validation and integration tests for persistence failures, concurrent updates, CSV export, and account lifecycle edge cases.
6. Add structured audit logging with appropriate access controls and retention.

These are recommendations for a future version, not features currently implemented.
