package Bank;

public class StudentAccount extends SavingsAccount {
    private static final long serialVersionUID = 1L;
    private final String institutionName;

    public StudentAccount(String name, double balance, String institutionName) {
        super(name, balance, 20000, 100);
        if (institutionName == null || institutionName.isBlank()) {
            throw new IllegalArgumentException("Institution name is required.");
        }
        this.institutionName = institutionName.trim();
    }

    public String getInstitutionName() { return institutionName; }
}
