package Bank;

import Exceptions.MaxBalance;
import Exceptions.MaxWithdraw;

public class SavingsAccount extends BankAccount {
    private static final long serialVersionUID = 1L;
    private final float rate = 0.05f;
    private final double maxWithLimit;

    public SavingsAccount(String name, double balance, double maxWithLimit) {
        this(name, balance, maxWithLimit, 2000);
    }

    protected SavingsAccount(String name, double balance, double maxWithLimit, double minimumBalance) {
        super(name, balance, minimumBalance);
        if (!Double.isFinite(maxWithLimit) || maxWithLimit <= 0) {
            throw new IllegalArgumentException("Maximum withdrawal limit must be greater than zero.");
        }
        this.maxWithLimit = maxWithLimit;
    }

    public double getMaxWithdrawLimit() { return maxWithLimit; }

    public double getNetBalance() {
        return getbalance() + (getbalance() * rate);
    }

    @Override
    public void withdraw(double amount) throws MaxWithdraw, MaxBalance {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        if (amount > maxWithLimit) {
            throw new MaxWithdraw("Maximum withdrawal limit exceeded: " + maxWithLimit);
        }
        super.withdraw(amount);
    }
}
