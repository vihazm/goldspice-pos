package Controller;

import Model.user;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class UserController {

    // ✔️ Updated: Removed userId from INSERT — assuming AUTO_INCREMENT is enabled
    public boolean addUser(int userId, String name, String username, String password, String userRole, String joinDate) {
        String sql = "INSERT INTO user (Name, Username, Password, User_role, Join_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, name);
            pst.setString(2, username);
            pst.setString(3, password);
            pst.setString(4, userRole);
            pst.setString(5, joinDate);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding user: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String query = "DELETE FROM user WHERE User_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, userId);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting user: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    public void loadUsers(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT User_id, Name, Username, Password, User_role, Join_date FROM user";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("User_id");
                String name = rs.getString("Name");
                String username = rs.getString("Username");
                String password = rs.getString("Password");
                String role = rs.getString("User_role");
                String joinDate = rs.getString("Join_date");

                Object[] rowData = {userId, name, username, password, role, joinDate};
                model.addRow(rowData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public user authenticate(String username, String password) {
         String query = "SELECT User_id, Name, Username, Password, User_role, Join_date FROM user WHERE Username = ? AND Password = ?";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(query)) {

        pst.setString(1, username);
        pst.setString(2, password);

        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                // A user was found, create a user object and return it
                int userId = rs.getInt("User_id");
                String name = rs.getString("Name");
                String userUsername = rs.getString("Username");
                String userPassword = rs.getString("Password");
                String userRole = rs.getString("User_role");
                String joinDate = rs.getString("Join_date");

                // CORRECTED LINE: The order of userPassword and userRole is now correct.
                return new user(userId, name, userUsername, userPassword, userRole, joinDate);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database Error during authentication: " + e.getMessage(), "Authentication Error", JOptionPane.ERROR_MESSAGE);
    }
    return null; // No user found or an error occurred
    }
}
