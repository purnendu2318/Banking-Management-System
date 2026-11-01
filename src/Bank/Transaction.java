package Bank;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/** Immutable audit record for a banking operation. */
public final class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String type, accountNumber, relatedAccountNumber, description;
    private final double amount, resultingBalance;

    public Transaction(String type, String accountNumber, String relatedAccountNumber, double amount, double resultingBalance, String description) {
        this.type = type; this.accountNumber = accountNumber; this.relatedAccountNumber = relatedAccountNumber;
        this.amount = amount; this.resultingBalance = resultingBalance; this.description = description;
    }
    public String getId() { return id; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public String getAccountNumber() { return accountNumber; }
    public String getRelatedAccountNumber() { return relatedAccountNumber; }
    public double getAmount() { return amount; }
    public double getResultingBalance() { return resultingBalance; }
    public LocalDateTime getTimestamp() { return timestamp; }
    @Override public String toString() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | " + id + " | " + type + " | Account " + accountNumber
            + (relatedAccountNumber == null ? "" : " -> " + relatedAccountNumber) + " | Amount: ₹" + String.format("%.2f", amount)
            + " | Balance: ₹" + String.format("%.2f", resultingBalance) + (description == null || description.isBlank() ? "" : " | " + description);
    }
}
