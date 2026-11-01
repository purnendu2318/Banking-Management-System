package Bank;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;
import Exceptions.InvalidAmount;
import Exceptions.MaxBalance;
import Exceptions.MaxWithdraw;

public class BankAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private double balance;
    private final double min_balance;
    private final String acc_num;

    public BankAccount(String name, double balance, double minBalance) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Account holder name is required.");
        if (!Double.isFinite(balance) || !Double.isFinite(minBalance) || minBalance < 0 || balance < minBalance)
            throw new IllegalArgumentException("Initial balance must be at least the minimum balance of ₹" + String.format("%.2f", minBalance) + ".");
        this.name = name.trim(); this.balance = balance; this.min_balance = minBalance;
        this.acc_num = Integer.toString(ThreadLocalRandom.current().nextInt(100000000, 1000000000));
    }
    public void deposit(double amount) throws InvalidAmount {
        if (!Double.isFinite(amount) || amount <= 0) throw new InvalidAmount("Deposit amount must be a finite number greater than zero.");
        if (!Double.isFinite(balance + amount)) throw new InvalidAmount("Deposit would exceed the supported balance limit.");
        balance += amount;
    }
    public void withdraw(double amount) throws MaxWithdraw, MaxBalance {
        if (!Double.isFinite(amount) || amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be a finite number greater than zero.");
        if (amount > balance || balance - amount < min_balance) throw new MaxBalance("Insufficient balance. Maintain at least ₹" + String.format("%.2f", min_balance) + ".");
        balance -= amount;
    }
    /** Used only by Bank to complete a validated transfer. */
    void creditForTransfer(double amount) throws InvalidAmount { deposit(amount); }
    public double getbalance() { return balance; }
    public String getAccountNumber() { return acc_num; }
    public String getName() { return name; }
    public double getMinimumBalance() { return min_balance; }
    public void setName(String newName) {
        if (newName == null || newName.isBlank()) throw new IllegalArgumentException("Account holder name is required.");
        name = newName.trim();
    }
    @Override public String toString() {
        return "Name: " + name + ", Id: " + acc_num + ", Balance: ₹" + String.format("%.2f", balance) + ", Minimum: ₹" + String.format("%.2f", min_balance) + ", Type: " + getClass().getSimpleName();
    }
}
