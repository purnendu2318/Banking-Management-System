package Bank;

public class CurrentAccount extends BankAccount {
    private static final long serialVersionUID = 1L;
    private final String tradeLicenseNumber;

    public CurrentAccount(String name, double balance, String tradeLicenseNumber) {
        super(name, balance, 5000);
        if (tradeLicenseNumber == null || tradeLicenseNumber.isBlank()) {
            throw new IllegalArgumentException("Trade licence number is required.");
        }
        this.tradeLicenseNumber = tradeLicenseNumber.trim();
    }

    public String getTradeLicenseNumber() { return tradeLicenseNumber; }
}
