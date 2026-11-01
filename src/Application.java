import java.awt.EventQueue;
import GUI.GUIForm;

public class Application {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                GUIForm.login.frame.setLocationRelativeTo(null);
                GUIForm.login.frame.setVisible(true);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null, "Could not start Banking System: " + ex.getMessage(), "Startup error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
