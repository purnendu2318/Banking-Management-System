import Bank.Bank;
import Bank.BankAccount;
import Bank.SavingsAccount;
import Bank.CurrentAccount;
import Bank.StudentAccount;
import Exceptions.AccNotFound;
import Exceptions.InvalidAmount;
import Exceptions.MaxBalance;
import Exceptions.MaxWithdraw;
import java.io.*;

/** Dependency-free smoke tests for the core banking domain. Run with java -ea. */
public final class BankingSystemTests {
    private static int passed;
    private BankingSystemTests() { }

    public static void main(String[] args) throws Exception {
        testCreateAndFindAccount();
        testDepositAndRejectInvalidAmounts();
        testWithdrawalMinimumBalance();
        testTransferAndHistory();
        testTransferRejectsSameAccount();
        testTransferLimitsAndDashboardMetrics();
        testCloseAccountRecordsClosure();
        testDefensiveAccountArray();
        testSerializationRoundTrip();
        testAccountTypeRulesAndValidation();
        testUnknownAccountHandling();
        testRejectedWithdrawalDoesNotMutateBalances();
        System.out.println("PASS: " + passed + " core banking assertions");
    }
    private static void testCreateAndFindAccount() {
        Bank bank = new Bank(); SavingsAccount account = new SavingsAccount("Test User", 3000, 1000);
        bank.addAccount(account); check(bank.findAccount(account.getAccountNumber()) == account, "find account");
        check(bank.findAccount("not-an-account") == null, "missing account returns null");
    }
    private static void testDepositAndRejectInvalidAmounts() throws Exception {
        Bank bank = new Bank(); SavingsAccount a = new SavingsAccount("Deposit User", 3000, 1000); bank.addAccount(a);
        bank.deposit(a.getAccountNumber(), 500); check(a.getbalance() == 3500, "deposit balance");
        expect(InvalidAmount.class, () -> bank.deposit(a.getAccountNumber(), 0));
        expect(InvalidAmount.class, () -> bank.deposit(a.getAccountNumber(), Double.POSITIVE_INFINITY));
    }
    private static void testWithdrawalMinimumBalance() throws Exception {
        Bank bank = new Bank(); SavingsAccount a = new SavingsAccount("Withdraw User", 3000, 1000); bank.addAccount(a);
        bank.withdraw(a.getAccountNumber(), 500); check(a.getbalance() == 2500, "withdraw balance");
        expect(MaxBalance.class, () -> bank.withdraw(a.getAccountNumber(), 1000));
        expect(InvalidAmount.class, () -> bank.withdraw(a.getAccountNumber(), -1));
    }
    private static void testTransferAndHistory() throws Exception {
        Bank bank = new Bank(); SavingsAccount from = new SavingsAccount("From User", 5000, 2000);
        CurrentAccount to = new CurrentAccount("To User", 5000, "LIC-123"); bank.addAccount(from); bank.addAccount(to);
        bank.transfer(from.getAccountNumber(), to.getAccountNumber(), 500);
        check(from.getbalance() == 4500 && to.getbalance() == 5500, "transfer balances");
        check(bank.getTransactionsForAccount(from.getAccountNumber()).size() >= 2, "transfer appears in account history");
    }
    private static void testTransferRejectsSameAccount() throws Exception {
        Bank bank = new Bank(); SavingsAccount a = new SavingsAccount("Same User", 3000, 1000); bank.addAccount(a);
        expect(InvalidAmount.class, () -> bank.transfer(a.getAccountNumber(), a.getAccountNumber(), 100));
    }
    private static void testTransferLimitsAndDashboardMetrics() throws Exception {
        Bank bank = new Bank();
        SavingsAccount from = new SavingsAccount("Limit From", 250000, 250000);
        SavingsAccount to = new SavingsAccount("Limit To", 2000, 250000);
        bank.addAccount(from); bank.addAccount(to);
        expect(InvalidAmount.class, () -> bank.transfer(from.getAccountNumber(), to.getAccountNumber(), Bank.MAX_SINGLE_TRANSFER + 1));
        bank.transfer(from.getAccountNumber(), to.getAccountNumber(), 100000);
        bank.transfer(from.getAccountNumber(), to.getAccountNumber(), 100000);
        expect(InvalidAmount.class, () -> bank.transfer(from.getAccountNumber(), to.getAccountNumber(), 1));
        check(bank.getActiveAccountCount() == 2, "dashboard active account count");
        check(bank.getTransactionCount(java.time.LocalDate.now()) >= 6, "dashboard transaction count");
        check(bank.getDailyOutgoingTransferTotal(from.getAccountNumber(), java.time.LocalDate.now()) == 200000, "daily outgoing transfer total");
    }

    private static void testCloseAccountRecordsClosure() throws Exception {
        Bank bank = new Bank(); SavingsAccount a = new SavingsAccount("Close User", 3000, 1000); bank.addAccount(a);
        bank.closeAccount(a.getAccountNumber()); check(bank.findAccount(a.getAccountNumber()) == null, "account closed");
        check(bank.getTransactionsForAccount(a.getAccountNumber()).stream().anyMatch(t -> "ACCOUNT_CLOSED".equals(t.getType())), "closure audited");
    }
    private static void testDefensiveAccountArray() {
        Bank bank = new Bank(); SavingsAccount a = new SavingsAccount("Copy User", 3000, 1000); bank.addAccount(a);
        BankAccount[] copy = bank.getAccounts(); copy[0] = null;
        check(bank.findAccount(a.getAccountNumber()) == a, "external array cannot mutate bank registry");
    }
    private static void testSerializationRoundTrip() throws Exception {
        Bank bank = new Bank(); SavingsAccount a = new SavingsAccount("Persist User", 3000, 1000); bank.addAccount(a); bank.deposit(a.getAccountNumber(), 200);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) { out.writeObject(bank); }
        Bank restored;
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) { restored = (Bank) in.readObject(); }
        check(restored.findAccount(a.getAccountNumber()) != null, "account survives serialization");
        check(restored.getTransactions().size() == 2, "history survives serialization");
    }

    private static void testAccountTypeRulesAndValidation() throws Exception {
        Bank bank = new Bank();
        SavingsAccount savings = new SavingsAccount("Savings User", 4000, 1000);
        CurrentAccount current = new CurrentAccount("Current User", 5000, "LIC-456");
        StudentAccount student = new StudentAccount("Student User", "Example Institute", 1000);
        bank.addAccount(savings);
        bank.addAccount(current);
        bank.addAccount(student);

        expect(MaxWithdraw.class, () -> bank.withdraw(savings.getAccountNumber(), 1001));
        check(savings.getbalance() == 4000, "rejected savings withdrawal preserves balance");
        bank.withdraw(savings.getAccountNumber(), 1000);
        check(savings.getbalance() == 3000, "savings withdrawal at configured limit");
        check("LIC-456".equals(current.getTradeLicenseNumber()), "current account trade licence");
        check("Example Institute".equals(student.getInstitutionName()), "student account institution");

        expect(IllegalArgumentException.class, () -> new CurrentAccount("Invalid", 5000, " "));
        expect(IllegalArgumentException.class, () -> new StudentAccount("Invalid", " ", 1000));
        expect(IllegalArgumentException.class, () -> new SavingsAccount("Invalid", 3000, 0));
    }

    private static void testUnknownAccountHandling() throws Exception {
        Bank bank = new Bank();
        expect(AccNotFound.class, () -> bank.deposit("missing-account", 100));
        expect(AccNotFound.class, () -> bank.withdraw("missing-account", 100));
        expect(AccNotFound.class, () -> bank.transfer("missing-source", "missing-destination", 100));
        expect(InvalidAmount.class, () -> bank.deposit("missing-account", Double.NaN));
    }

    private static void testRejectedWithdrawalDoesNotMutateBalances() throws Exception {
        Bank bank = new Bank();
        SavingsAccount from = new SavingsAccount("Insufficient User", 3000, 2000);
        SavingsAccount to = new SavingsAccount("Recipient User", 2500, 2000);
        bank.addAccount(from);
        bank.addAccount(to);
        int historyBefore = bank.getTransactions().size();

        expect(MaxBalance.class, () -> bank.transfer(from.getAccountNumber(), to.getAccountNumber(), 1500));
        check(from.getbalance() == 3000, "rejected transfer preserves source balance");
        check(to.getbalance() == 2500, "rejected transfer preserves destination balance");
        check(bank.getTransactions().size() == historyBefore, "rejected transfer does not add transfer records");
    }

    private static void check(boolean condition, String name) { if (!condition) throw new AssertionError(name); passed++; }
    private static void expect(Class<? extends Throwable> type, ThrowingRunnable action) throws Exception {
        try { action.run(); } catch (Throwable ex) { if (type.isInstance(ex)) { passed++; return; } throw new AssertionError("Expected " + type.getSimpleName() + " but got " + ex, ex); }
        throw new AssertionError("Expected " + type.getSimpleName() + " but nothing was thrown");
    }
    @FunctionalInterface private interface ThrowingRunnable { void run() throws Exception; }
}
