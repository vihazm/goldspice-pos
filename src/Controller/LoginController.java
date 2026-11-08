package Controller;

import Model.user;
import View.Login;
import View.AdminDash;
import View.ManagerDash;
import View.CashierDash;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class LoginController {
    private Login view;
    private UserController userController; 

    public LoginController(Login view) {
        this.view = view;
        this.userController = new UserController();
        this.view.addLoginListener(new LoginListener());
    }

    class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {//Abstraction - Hides all bulky operations in a method.
            String username = view.getUsername();
            String password = view.getPassword();

            // Check for hardcoded admin credentials first
            if ("admin".equals(username) && "admin123".equals(password)) {
                JOptionPane.showMessageDialog(view, "Admin Login Successful! Welcome Admin.");
                view.dispose();
                new AdminDash().setVisible(true);
                return; // Stop further processing
            }

            // If not admin, authenticate by referring against the database
            user authenticatedUser = userController.authenticate(username, password);

            if (authenticatedUser != null) {
                JOptionPane.showMessageDialog(view, "Login successful! Welcome " + authenticatedUser.getRole());
                view.dispose();

                switch (authenticatedUser.getRole().toLowerCase()) {
                    case "admin": // This case can be removed now, but it's okay to keep for robustness
                        new AdminDash().setVisible(true);
                        break;
                    case "manager":
                        new ManagerDash().setVisible(true);
                        break;
                    case "cashier":
                        new CashierDash(authenticatedUser.getUserId()).setVisible(true);
                        break;
                    default:
                        JOptionPane.showMessageDialog(view, "Unknown role: " + authenticatedUser.getRole());
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(view, "Invalid username or password.");
            }
        }
    }
}