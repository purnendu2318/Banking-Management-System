package GUI;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import Data.FileIO;

public class AddStudentAccount extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField, textField_1, textField_2;

    public AddStudentAccount() {
        setTitle("Add Student Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel(); contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); setContentPane(contentPane); contentPane.setLayout(null);
        JLabel title = new JLabel("Add Student Account ");
        title.setFont(new Font("Tahoma", Font.BOLD, 16)); title.setHorizontalAlignment(SwingConstants.CENTER); title.setBounds(10, 11, 414, 34); contentPane.add(title);
        JLabel lblName = new JLabel("Name:"); lblName.setFont(new Font("Tahoma", Font.PLAIN, 11)); lblName.setBounds(10, 72, 124, 14); contentPane.add(lblName);
        textField = new JTextField(); textField.setBounds(144, 69, 254, 20); contentPane.add(textField); textField.setColumns(10);
        JLabel lblBalance = new JLabel("Balance:"); lblBalance.setFont(new Font("Tahoma", Font.PLAIN, 11)); lblBalance.setBounds(10, 118, 124, 14); contentPane.add(lblBalance);
        textField_1 = new JTextField(); textField_1.setColumns(10); textField_1.setBounds(144, 115, 254, 20); contentPane.add(textField_1);
        JLabel lblInstitution = new JLabel("Institution Name:"); lblInstitution.setFont(new Font("Tahoma", Font.PLAIN, 11)); lblInstitution.setBounds(10, 163, 135, 14); contentPane.add(lblInstitution);
        textField_2 = new JTextField(); textField_2.setColumns(10); textField_2.setBounds(144, 160, 254, 20); contentPane.add(textField_2);
        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = textField.getText().trim(), balanceText = textField_1.getText().trim(), institution = textField_2.getText().trim();
                if (name.isEmpty() || balanceText.isEmpty() || institution.isEmpty()) { JOptionPane.showMessageDialog(AddStudentAccount.this, "Please fill in all fields.", "Invalid input", JOptionPane.WARNING_MESSAGE); return; }
                try {
                    double balance = Double.parseDouble(balanceText);
                    if (!Double.isFinite(balance) || balance < 100) { JOptionPane.showMessageDialog(AddStudentAccount.this, "Minimum initial balance is ₹100.", "Invalid balance", JOptionPane.WARNING_MESSAGE); return; }
                    if (JOptionPane.showConfirmDialog(AddStudentAccount.this, "Create this student account?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
                    int index = FileIO.bank.addStudentAccount(name, institution, balance);
                    Bank.BankAccount created = FileIO.bank.getAccounts()[index];
                    FileIO.Write(); GUIForm.UpdateDisplayIfVisible();
                    JOptionPane.showMessageDialog(AddStudentAccount.this, "Student account created successfully!\n\nAccount holder: " + created.getName()
                        + "\nAccount number: " + created.getAccountNumber() + "\nCurrent balance: ₹" + String.format("%.2f", created.getbalance())
                        + "\n\nPlease save this account number for future transactions."); dispose();
                } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(AddStudentAccount.this, "Enter a valid numeric balance.", "Invalid input", JOptionPane.ERROR_MESSAGE); }
                catch (IllegalArgumentException | IllegalStateException ex) { JOptionPane.showMessageDialog(AddStudentAccount.this, ex.getMessage(), "Unable to create account", JOptionPane.ERROR_MESSAGE); }
            }
        });
        btnAdd.setBounds(86, 209, 89, 23); contentPane.add(btnAdd);
        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { textField.setText(null); textField_1.setText(null); textField_2.setText(null); } });
        btnReset.setBounds(309, 209, 89, 23); contentPane.add(btnReset);
    }
}
