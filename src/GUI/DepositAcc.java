package GUI;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import Exceptions.AccNotFound;
import Exceptions.InvalidAmount;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.awt.event.ActionEvent;
import Data.FileIO;
import java.awt.SystemColor;

public class DepositAcc extends JFrame implements Serializable {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField, textField_1;

    public DepositAcc() {
        setTitle("Deposit To Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBackground(SystemColor.activeCaption);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        JLabel title = new JLabel("Deposit To Account");
        title.setFont(new Font("Tahoma", Font.BOLD, 16));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(10, 11, 414, 36);
        contentPane.add(title);
        JLabel lblName = new JLabel("Account Number:");
        lblName.setHorizontalAlignment(SwingConstants.RIGHT);
        lblName.setBounds(0, 86, 111, 14);
        contentPane.add(lblName);
        textField = new JTextField();
        textField.setBounds(121, 83, 211, 20);
        contentPane.add(textField);
        textField.setColumns(10);
        JButton btnSelectAccount = new JButton("Select...");
        btnSelectAccount.setToolTipText("Choose an account from the account list");
        btnSelectAccount.setBounds(338, 81, 86, 25);
        btnSelectAccount.addActionListener(e -> {
            String selected = AccountPicker.chooseAccount(this);
            if (selected != null) textField.setText(selected);
        });
        contentPane.add(btnSelectAccount);
        textField_1 = new JTextField();
        textField_1.setColumns(10);
        textField_1.setBounds(121, 147, 211, 20);
        contentPane.add(textField_1);
        JLabel lblAmount = new JLabel("Amount:");
        lblAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        lblAmount.setBounds(0, 150, 111, 14);
        contentPane.add(lblAmount);
        JButton btnDeposit = new JButton("Deposit");
        btnDeposit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String accountNumber = textField.getText().trim(), amountText = textField_1.getText().trim();
                if (accountNumber.isEmpty() || amountText.isEmpty()) { JOptionPane.showMessageDialog(DepositAcc.this, "Enter account number and amount."); return; }
                try {
                    double amount = Double.parseDouble(amountText);
                    if (JOptionPane.showConfirmDialog(DepositAcc.this, "Confirm deposit?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
                    FileIO.bank.deposit(accountNumber, amount); FileIO.Write(); GUIForm.UpdateDisplayIfVisible();
                    JOptionPane.showMessageDialog(DepositAcc.this, "Deposit successful."); dispose();
                } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(DepositAcc.this, "Enter a valid numeric amount.", "Invalid input", JOptionPane.ERROR_MESSAGE); }
                  catch (InvalidAmount ex) { JOptionPane.showMessageDialog(DepositAcc.this, ex.getMessage(), "Invalid amount", JOptionPane.WARNING_MESSAGE); }
                  catch (AccNotFound ex) { JOptionPane.showMessageDialog(DepositAcc.this, ex.getMessage(), "Account not found", JOptionPane.WARNING_MESSAGE); }
                  catch (IllegalStateException ex) { JOptionPane.showMessageDialog(DepositAcc.this, ex.getMessage(), "Save failed", JOptionPane.ERROR_MESSAGE); }
            }
        });
        btnDeposit.setBounds(73, 212, 89, 23);
        contentPane.add(btnDeposit);
        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(243, 212, 89, 23);
        contentPane.add(btnReset);
        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { textField.setText(null); textField_1.setText(null); }
        });
    }
}
