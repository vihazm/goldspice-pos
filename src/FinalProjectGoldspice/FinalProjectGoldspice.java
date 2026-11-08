package FinalProjectGoldspice;

import Controller.LoginController;
import View.Login;

public class FinalProjectGoldspice {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            Login loginView = new Login();
            new LoginController(loginView);  // Removed userModel
            loginView.setVisible(true);
        });
    }
}
