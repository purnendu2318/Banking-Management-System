package GUI;

import Bank.BankAccount;
import Bank.Transaction;
import Data.FileIO;
import Exceptions.AccNotFound;
import Exceptions.InvalidAmount;
import Exceptions.MaxBalance;
import Exceptions.MaxWithdraw;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Menu extends JFrame {
    private static final long serialVersionUID = 1L;
    public Menu() {
        setTitle("Banking System"); setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); setSize(720, 590); setLocationRelativeTo(null);
        FileIO.Read();
        JPanel root = new JPanel(new BorderLayout(12, 12)); root.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        JLabel title = new JLabel("Banking System", SwingConstants.CENTER); title.setFont(new Font("SansSerif", Font.BOLD, 26)); root.add(title, BorderLayout.NORTH);
        JPanel actions = new JPanel(new GridLayout(0, 2, 10, 10));
        addButton(actions, "Add Account", () -> GUIForm.addaccount.setVisible(true));
        addButton(actions, "Deposit", () -> GUIForm.depositacc.setVisible(true));
        addButton(actions, "Withdraw", () -> GUIForm.withdraw.setVisible(true));
        addButton(actions, "Dashboard / Reports", this::showDashboard);
        addButton(actions, "View All Accounts", this::showAccounts);
        addButton(actions, "Search Account", this::searchAccount);
        addButton(actions, "Transfer Money", this::transferMoney);
        addButton(actions, "Transaction History", this::showTransactions);
        addButton(actions, "Export Statement (CSV)", this::exportStatement);
        addButton(actions, "Update Account Name", this::renameAccount);
        addButton(actions, "Close Account", this::closeAccount);
        addButton(actions, "Save Data", this::saveData);
        addButton(actions, "Exit", this::exitApplication);
        root.add(actions, BorderLayout.CENTER); setContentPane(root);
        addWindowListener(new java.awt.event.WindowAdapter() { @Override public void windowClosing(java.awt.event.WindowEvent e) { exitApplication(); } });
    }
    private void addButton(JPanel panel, String label, Runnable action) { JButton b = new JButton(label); b.setFocusPainted(false); b.addActionListener(e -> action.run()); panel.add(b); }
    private void showDashboard() {
        BankAccount[] accounts = FileIO.bank.getAccounts();
        int savings = 0, current = 0, student = 0;
        for (BankAccount account : accounts) {
            if (account instanceof Bank.StudentAccount) student++;
            else if (account instanceof Bank.SavingsAccount) savings++;
            else if (account instanceof Bank.CurrentAccount) current++;
        }
        String report = "BANKING SYSTEM — ADMIN DASHBOARD\n\n"
            + "Open accounts: " + FileIO.bank.getActiveAccountCount() + "\n"
            + "Savings accounts: " + savings + "\n"
            + "Current accounts: " + current + "\n"
            + "Student accounts: " + student + "\n"
            + "Total current balance: ₹" + String.format("%.2f", FileIO.bank.getTotalCurrentBalance()) + "\n"
            + "Transactions today: " + FileIO.bank.getTransactionCount(java.time.LocalDate.now()) + "\n"
            + "Today's date: " + java.time.LocalDate.now() + "\n\n"
            + "Transfer controls: max ₹" + String.format("%.2f", Bank.Bank.MAX_SINGLE_TRANSFER)
            + " per transfer; max ₹" + String.format("%.2f", Bank.Bank.MAX_DAILY_OUTGOING_TRANSFERS) + " outgoing per account per day.";
        JTextArea area = new JTextArea(report, 14, 48);
        area.setEditable(false); area.setFont(new Font("Monospaced", Font.PLAIN, 13)); area.setMargin(new Insets(10, 10, 10, 10));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Dashboard / Reports", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAccounts() {
        DefaultListModel<String> model = FileIO.bank.display();
        if (model.isEmpty()) { JOptionPane.showMessageDialog(this, "No accounts have been created yet."); return; }
        JList<String> list = new JList<>(model); list.setVisibleRowCount(14); JScrollPane pane = new JScrollPane(list); pane.setPreferredSize(new Dimension(650, 300));
        JOptionPane.showMessageDialog(this, pane, "All Accounts", JOptionPane.INFORMATION_MESSAGE);
    }
    private void searchAccount() {
        String number = ask("Enter account number:"); if (number == null) return;
        BankAccount account = FileIO.bank.findAccount(number);
        if (account == null) { JOptionPane.showMessageDialog(this, "Account not found.", "Search", JOptionPane.WARNING_MESSAGE); return; }
        StringBuilder details = new StringBuilder(account.toString());
        if (account instanceof Bank.SavingsAccount) details.append("\nMaximum withdrawal: ₹").append(String.format("%.2f", ((Bank.SavingsAccount) account).getMaxWithdrawLimit()));
        if (account instanceof Bank.CurrentAccount) details.append("\nTrade licence: ").append(((Bank.CurrentAccount) account).getTradeLicenseNumber());
        if (account instanceof Bank.StudentAccount) details.append("\nInstitution: ").append(((Bank.StudentAccount) account).getInstitutionName());
        JOptionPane.showMessageDialog(this, details.toString(), "Account Details", JOptionPane.INFORMATION_MESSAGE);
    }
    private void transferMoney() {
        String fromNumber = AccountPicker.chooseAccount(this);
        if (fromNumber == null) return;
        String toNumber = AccountPicker.chooseAccount(this);
        if (toNumber == null) return;
        if (fromNumber.equals(toNumber)) {
            JOptionPane.showMessageDialog(this, "Choose two different accounts.", "Transfer failed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String rawAmount = JOptionPane.showInputDialog(this, "Amount to transfer (₹):");
        if (rawAmount == null) return;
        try {
            double amount = Double.parseDouble(rawAmount.trim());
            FileIO.bank.transfer(fromNumber, toNumber, amount);
            FileIO.Write(); refreshDisplay();
            JOptionPane.showMessageDialog(this, "Transfer completed successfully.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid numeric amount.", "Transfer failed", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Transfer failed", JOptionPane.ERROR_MESSAGE);
        } catch (AccNotFound | InvalidAmount | MaxBalance | MaxWithdraw ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Transfer failed", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Save/transfer issue", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showTransactions() {
        String account = JOptionPane.showInputDialog(this, "Account number (leave blank for all transactions):"); if (account == null) return;
        java.util.List<Transaction> list = account.trim().isEmpty() ? FileIO.bank.getTransactions() : FileIO.bank.getTransactionsForAccount(account.trim());
        if (list.isEmpty()) { JOptionPane.showMessageDialog(this, "No transactions found."); return; }
        StringBuilder text = new StringBuilder(); for (Transaction t : list) text.append(t).append('\n');
        JTextArea area = new JTextArea(text.toString(), 18, 80); area.setEditable(false); area.setCaretPosition(0);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Transaction History", JOptionPane.INFORMATION_MESSAGE);
    }
    private void exportStatement() {
        String account = ask("Account number to export:"); if (account == null) return;
        if (FileIO.bank.findAccount(account) == null) { JOptionPane.showMessageDialog(this, "Account not found."); return; }
        JFileChooser chooser = new JFileChooser(); chooser.setSelectedFile(new java.io.File("statement-" + account + ".csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (BufferedWriter w = Files.newBufferedWriter(chooser.getSelectedFile().toPath(), StandardCharsets.UTF_8)) {
            w.write("Timestamp,Transaction Type,Account Number,Related Account,Amount,Resulting Balance,Description\n");
            for (Transaction t : FileIO.bank.getTransactionsForAccount(account)) {
                w.write(csv(t.getTimestamp().toString()) + "," + csv(t.getType()) + "," + csv(t.getAccountNumber()) + "," + csv(t.getRelatedAccountNumber()) + "," + t.getAmount() + "," + t.getResultingBalance() + "," + csv(t.toString()) + "\n");
            }
            JOptionPane.showMessageDialog(this, "Statement exported to:\n" + chooser.getSelectedFile().getAbsolutePath());
        } catch (IOException ex) { JOptionPane.showMessageDialog(this, "Could not export statement: " + ex.getMessage(), "Export failed", JOptionPane.ERROR_MESSAGE); }
    }
    private String csv(String s) { if (s == null) return "\"\""; return "\"" + s.replace("\"", "\"\"") + "\""; }
    private void renameAccount() {
        String number = ask("Account number:"); if (number == null) return; String name = ask("New account holder name:"); if (name == null) return;
        try { FileIO.bank.renameAccount(number, name); FileIO.Write(); refreshDisplay(); JOptionPane.showMessageDialog(this, "Account name updated."); }
        catch (AccNotFound | IllegalArgumentException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Update failed", JOptionPane.WARNING_MESSAGE); }
        catch (IllegalStateException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Save failed", JOptionPane.ERROR_MESSAGE); }
    }
    private void closeAccount() {
        String number = AccountPicker.chooseAccount(this);
        if (number == null) return;
        if (JOptionPane.showConfirmDialog(this, "Close this account and record its remaining balance as a cash payout?", "Confirm closure", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { FileIO.bank.closeAccount(number); FileIO.Write(); refreshDisplay(); JOptionPane.showMessageDialog(this, "Account closed."); }
        catch (AccNotFound | IllegalStateException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot close account", JOptionPane.WARNING_MESSAGE); }
    }
    private void saveData() { try { FileIO.Write(); JOptionPane.showMessageDialog(this, "Data saved successfully."); } catch (IllegalStateException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Save failed", JOptionPane.ERROR_MESSAGE); } }
    private void exitApplication() { try { FileIO.Write(); } catch (IllegalStateException ex) { if (JOptionPane.showConfirmDialog(this, ex.getMessage() + "\nExit without confirming saved data?", "Save failed", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return; } dispose(); System.exit(0); }
    private String ask(String message) { String s = JOptionPane.showInputDialog(this, message); return s == null ? null : s.trim(); }
    private void refreshDisplay() { if (GUIForm.displaylist != null && GUIForm.displaylist.isVisible()) GUIForm.UpdateDisplay(); }
}
