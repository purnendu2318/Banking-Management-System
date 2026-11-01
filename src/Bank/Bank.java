package Bank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import Exceptions.AccNotFound;
import Exceptions.InvalidAmount;
import Exceptions.MaxBalance;
import Exceptions.MaxWithdraw;

public class Bank implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int CAPACITY = 1000;
    /** Educational safety limits; configure for your demo, not real banking. */
    public static final double MAX_SINGLE_TRANSFER = 100000.00;
    public static final double MAX_DAILY_OUTGOING_TRANSFERS = 200000.00;
    private BankAccount[] accounts = new BankAccount[CAPACITY];
    private List<Transaction> transactions = new ArrayList<>();

    public int addAccount(BankAccount account) {
        if (account == null) throw new IllegalArgumentException("Account cannot be null.");
        if (findAccount(account.getAccountNumber()) != null) throw new IllegalStateException("Duplicate account number generated. Please try again.");
        for (int i = 0; i < accounts.length; i++) if (accounts[i] == null) { accounts[i] = account; record("ACCOUNT_OPENED", account, null, account.getbalance(), "Account created"); return i; }
        throw new IllegalStateException("The bank has reached its maximum capacity of " + CAPACITY + " accounts.");
    }
    public int addAccount(String name, double balance, double maxWithdrawLimit) { return addAccount(new SavingsAccount(name, balance, maxWithdrawLimit)); }
    public int addAccount(String name, double balance, String tradeLicense) { return addAccount(new CurrentAccount(name, balance, tradeLicense)); }
    public int addAccount(String name, String institutionName, double balance, double minBalance) { return addStudentAccount(name, institutionName, balance); }
    public int addStudentAccount(String name, String institutionName, double balance) { return addAccount(new StudentAccount(name, balance, institutionName)); }
    public BankAccount findAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) return null;
        String sought = accountNumber.trim();
        for (BankAccount account : accounts) if (account != null && account.getAccountNumber().equals(sought)) return account;
        return null;
    }
    public void deposit(String accountNumber, double amount) throws InvalidAmount, AccNotFound {
        if (!Double.isFinite(amount) || amount <= 0) throw new InvalidAmount("Deposit amount must be a finite number greater than zero.");
        BankAccount account = findAccount(accountNumber); if (account == null) throw new AccNotFound("Account not found: " + accountNumber);
        account.deposit(amount); record("DEPOSIT", account, null, amount, "Cash deposit");
    }
    public void withdraw(String accountNumber, double amount) throws MaxBalance, AccNotFound, MaxWithdraw, InvalidAmount {
        if (!Double.isFinite(amount) || amount <= 0) throw new InvalidAmount("Withdrawal amount must be a finite number greater than zero.");
        BankAccount account = findAccount(accountNumber); if (account == null) throw new AccNotFound("Account not found: " + accountNumber);
        try { account.withdraw(amount); } catch (IllegalArgumentException ex) { throw new InvalidAmount(ex.getMessage()); }
        record("WITHDRAWAL", account, null, amount, "Cash withdrawal");
    }
    public void transfer(String fromNumber, String toNumber, double amount) throws AccNotFound, InvalidAmount, MaxBalance, MaxWithdraw {
        if (!Double.isFinite(amount) || amount <= 0) throw new InvalidAmount("Transfer amount must be a finite number greater than zero.");
        if (amount > MAX_SINGLE_TRANSFER) throw new InvalidAmount("Transfer exceeds the per-transaction limit of ₹" + String.format("%.2f", MAX_SINGLE_TRANSFER) + ".");
        if (fromNumber == null || fromNumber.trim().equals(toNumber == null ? "" : toNumber.trim())) throw new InvalidAmount("Choose two different accounts.");
        BankAccount from = findAccount(fromNumber), to = findAccount(toNumber);
        if (from == null) throw new AccNotFound("Source account not found.");
        if (to == null) throw new AccNotFound("Destination account not found.");
        if (!Double.isFinite(to.getbalance() + amount)) throw new InvalidAmount("Transfer would exceed the destination balance limit.");
        double alreadyTransferredToday = getDailyOutgoingTransferTotal(fromNumber, java.time.LocalDate.now());
        if (alreadyTransferredToday + amount > MAX_DAILY_OUTGOING_TRANSFERS) {
            throw new InvalidAmount("Daily outgoing transfer limit of ₹" + String.format("%.2f", MAX_DAILY_OUTGOING_TRANSFERS) + " exceeded.");
        }
        from.withdraw(amount);
        try { to.creditForTransfer(amount); }
        catch (InvalidAmount ex) { try { from.deposit(amount); } catch (InvalidAmount impossible) { throw new IllegalStateException("Transfer rollback failed", impossible); } throw ex; }
        record("TRANSFER_OUT", from, to.getAccountNumber(), amount, "Transfer to " + to.getAccountNumber());
        record("TRANSFER_IN", to, from.getAccountNumber(), amount, "Transfer from " + from.getAccountNumber());
    }
    public List<Transaction> getTransactions() { return Collections.unmodifiableList(new ArrayList<>(transactions)); }
    public List<Transaction> getTransactionsForAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) return Collections.emptyList();
        String sought = accountNumber.trim();
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (sought.equals(t.getAccountNumber()) || sought.equals(t.getRelatedAccountNumber())) result.add(t);
        }
        return Collections.unmodifiableList(result);
    }
    public void renameAccount(String accountNumber, String newName) throws AccNotFound {
        BankAccount account = findAccount(accountNumber); if (account == null) throw new AccNotFound("Account not found.");
        account.setName(newName); record("ACCOUNT_UPDATED", account, null, 0, "Account holder name updated");
    }
    public void closeAccount(String accountNumber) throws AccNotFound, IllegalStateException {
        String sought = accountNumber == null ? "" : accountNumber.trim();
        for (int i = 0; i < accounts.length; i++) {
            BankAccount account = accounts[i];
            if (account != null && account.getAccountNumber().equals(sought)) {
                double payout = account.getbalance();
                record("ACCOUNT_CLOSED", account, null, payout, "Account closed; remaining balance paid out in cash");
                accounts[i] = null;
                return;
            }
        }
        throw new AccNotFound("Account not found.");
    }
    public int getActiveAccountCount() {
        int count = 0;
        for (BankAccount account : accounts) if (account != null) count++;
        return count;
    }
    public double getTotalCurrentBalance() {
        double total = 0;
        for (BankAccount account : accounts) if (account != null) total += account.getbalance();
        return total;
    }
    public double getDailyOutgoingTransferTotal(String accountNumber, java.time.LocalDate date) {
        if (accountNumber == null || date == null) return 0;
        double total = 0;
        for (Transaction transaction : transactions) {
            if ("TRANSFER_OUT".equals(transaction.getType())
                    && accountNumber.trim().equals(transaction.getAccountNumber())
                    && transaction.getTimestamp().toLocalDate().equals(date)) total += transaction.getAmount();
        }
        return total;
    }
    public long getTransactionCount(java.time.LocalDate date) {
        if (date == null) return 0;
        return transactions.stream().filter(t -> t.getTimestamp().toLocalDate().equals(date)).count();
    }
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (accounts == null) accounts = new BankAccount[CAPACITY];
        else if (accounts.length < CAPACITY) accounts = java.util.Arrays.copyOf(accounts, CAPACITY);
        if (transactions == null) transactions = new ArrayList<>();
    }
    private void record(String type, BankAccount account, String related, double amount, String description) {
        transactions.add(new Transaction(type, account.getAccountNumber(), related, amount, account.getbalance(), description));
    }
    public DefaultListModel<String> display() { DefaultListModel<String> list = new DefaultListModel<>(); for (BankAccount a : accounts) if (a != null) list.addElement(a.toString()); return list; }
    public BankAccount[] getAccounts() { return java.util.Arrays.copyOf(accounts, accounts.length); }
    public void setAccounts(BankAccount[] accounts) {
        if (accounts == null) throw new IllegalArgumentException("Accounts array cannot be null.");
        this.accounts = accounts.length < CAPACITY ? java.util.Arrays.copyOf(accounts, CAPACITY) : java.util.Arrays.copyOf(accounts, accounts.length);
    }
}
