package GUI;

import Bank.BankAccount;
import Data.FileIO;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/** Dialog that lets the user select an account without memorizing its number. */
public final class AccountPicker {
    private AccountPicker() { }

    /** Returns the selected account number, or null if cancelled/no accounts exist. */
    public static String chooseAccount(Component parent) {
        DefaultListModel<BankAccount> model = new DefaultListModel<>();
        for (BankAccount account : FileIO.bank.getAccounts()) {
            if (account != null) model.addElement(account);
        }
        if (model.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No accounts exist yet. Create an account first.",
                    "No accounts", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        JList<BankAccount> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(Math.min(12, model.size()));
        list.setSelectedIndex(0);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(680, Math.min(330, 60 + model.size() * 28)));
        int result = JOptionPane.showConfirmDialog(parent, scroll,
                "Select an account (name, account number and balance)",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION || list.getSelectedValue() == null) return null;
        return list.getSelectedValue().getAccountNumber();
    }
}
