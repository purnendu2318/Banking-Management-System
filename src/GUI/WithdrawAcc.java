package GUI;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import Exceptions.AccNotFound;
import Exceptions.InvalidAmount;
import Exceptions.MaxBalance;
import Exceptions.MaxWithdraw;
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

public class WithdrawAcc extends JFrame implements Serializable {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField, textField_1;

    public WithdrawAcc() {
        setTitle("Withdraw From Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBackground(SystemColor.activeCaption);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        JLabel title = new JLabel("Withdraw From Account");
        title.setFont(new Font("Tahoma", Font.BOLD, 16));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(10, 11, 414, 36);
        contentPane.add(title);
        JLabel lblName = new JLabel("Account Number:");
        lblName.setHorizontalAlignment(SwingConstants.RIGHT);
        lblName.setBounds(0, 86, 106, 14);
        contentPane.add(lblName);
        textField = new JTextField();
        textField.setBounds(116, 83, 216, 20);
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
        textField_1.setBounds(116, 147, 216, 20);
        contentPane.add(textField_1);
        JLabel lblAmount = new JLabel("Amount:");
        lblAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        lblAmount.setBounds(10, 150, 96, 14);
        contentPane.add(lblAmount);
        JButton btnWithdraw = new JButton("Withdraw");
        btnWithdraw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String accountNumber = textField.getText().trim(), amountText = textField_1.getText().trim();
                if (accountNumber.isEmpty() || amountText.isEmpty()) { JOptionPane.showMessageDialog(WithdrawAcc.this, "Enter account number and amount."); return; }
                try {
                    double amount = Double.parseDouble(amountText);
                    if (JOptionPane.showConfirmDialog(WithdrawAcc.this, "Confirm withdrawal?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
                    FileIO.bank.withdraw(accountNumber, amount); FileIO.Write(); GUIForm.UpdateDisplayIfVisible();
                    JOptionPane.showMessageDialog(WithdrawAcc.this, "Withdrawal successful."); dispose();
                } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(WithdrawAcc.this, "Enter a valid numeric amount.", "Invalid input", JOptionPane.ERROR_MESSAGE); }
                  catch (MaxBalance | MaxWithdraw | AccNotFound | InvalidAmount ex) { JOptionPane.showMessageDialog(WithdrawAcc.this, ex.getMessage(), "Withdrawal failed", JOptionPane.WARNING_MESSAGE); }
                  catch (IllegalStateException ex) { JOptionPane.showMessageDialog(WithdrawAcc.this, ex.getMessage(), "Save failed", JOptionPane.ERROR_MESSAGE); }
            }
        });
        btnWithdraw.setBounds(73, 212, 89, 23);
        contentPane.add(btnWithdraw);
        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(243, 212, 89, 23);
        contentPane.add(btnReset);
        btnReset.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { textField.setText(null); textField_1.setText(null); } });
    }
}
