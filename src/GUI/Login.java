package GUI;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login {
    public JFrame frame;
    private JTextField textField;
    private JPasswordField textField_1;
    private int failedAttempts = 0;
    private static final int MAX_ATTEMPTS = 5;

    public Login() { initialize(); }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Banking System");
        frame.getContentPane().setLayout(null);
        JLabel label = new JLabel("Banking System");
        label.setFont(new Font("Tahoma", Font.BOLD, 17));
        label.setBounds(147, 11, 151, 41);
        frame.getContentPane().add(label);
        JLabel lblLoginScreen = new JLabel("Login Screen");
        lblLoginScreen.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblLoginScreen.setBounds(170, 63, 101, 23);
        frame.getContentPane().add(lblLoginScreen);
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblUsername.setBounds(55, 119, 64, 23);
        frame.getContentPane().add(lblUsername);
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblPassword.setBounds(55, 159, 64, 23);
        frame.getContentPane().add(lblPassword);
        textField = new JTextField();
        textField.setBounds(130, 121, 86, 20);
        frame.getContentPane().add(textField);
        textField.setColumns(10);
        textField.setText(System.getenv().getOrDefault("BANK_ADMIN_USER", "admin"));
        textField_1 = new JPasswordField();
        textField_1.setBounds(130, 161, 86, 20);
        frame.getContentPane().add(textField_1);
        textField_1.setColumns(10);
        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (failedAttempts >= MAX_ATTEMPTS) {
                    JOptionPane.showMessageDialog(frame, "Login locked after 5 failed attempts. Restart the application to reset the demo lock.", "Login locked", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String user = textField.getText().trim();
                char[] entered = textField_1.getPassword();
                String pass = new String(entered);
                java.util.Arrays.fill(entered, '\0');
                String expectedUser = System.getenv().getOrDefault("BANK_ADMIN_USER", "admin");
                String expectedPass = System.getenv().getOrDefault("BANK_ADMIN_PASSWORD", "admin");
                if (user.equals(expectedUser) && java.security.MessageDigest.isEqual(pass.getBytes(java.nio.charset.StandardCharsets.UTF_8), expectedPass.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
                    failedAttempts = 0;
                    JOptionPane.showMessageDialog(frame, "Login successful");
                    frame.setVisible(false);
                    GUIForm.menu.setVisible(true);
                } else {
                    failedAttempts++;
                    int remaining = MAX_ATTEMPTS - failedAttempts;
                    JOptionPane.showMessageDialog(frame, remaining == 0 ? "Login locked after 5 failed attempts." : "Login failed. Attempts remaining: " + remaining, "Authentication failed", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        btnLogin.setBounds(260, 138, 89, 23);
        frame.getContentPane().add(btnLogin);
    }
}
