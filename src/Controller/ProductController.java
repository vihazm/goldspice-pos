/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Controller;

import Controller.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Vihas
 */
public class ProductController {

    public boolean addProduct(int pId, String pName, double pPrice, int stockLevel) {
        String query = "INSERT INTO products (P_id, P_name, P_price, StockLevel) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, pId);
            pst.setString(2, pName);
            pst.setDouble(3, pPrice);
            pst.setInt(4, stockLevel);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding product to database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    
    // Place this inside your ProductController class, after your other methods

    public boolean deleteProduct(int pId) {
        String query = "DELETE FROM products WHERE P_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, pId);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting product from database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    
    // Place this inside your ProductController class, after your other methods

    public boolean editProduct(int pId, String pName, double pPrice, int stockLevel) {
        String query = "UPDATE products SET P_name = ?, P_price = ?, StockLevel = ? WHERE P_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, pName);
            pst.setDouble(2, pPrice);
            pst.setInt(3, stockLevel);
            pst.setInt(4, pId);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error editing product: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    public void loadProducts(DefaultTableModel model) {
        String query = "SELECT P_id, P_name, P_price, StockLevel FROM products";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            // Clear existing data from the table
            model.setRowCount(0);

            while (rs.next()) {
                int pId = rs.getInt("P_id");
                String pName = rs.getString("P_name");
                double pPrice = rs.getDouble("P_price");
                int stockLevel = rs.getInt("StockLevel");
                
                model.addRow(new Object[]{pId, pName, pPrice, stockLevel});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading products from database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Place this method inside your ProductController class

    public ResultSet searchProductByName(String productName) {
        String query = "SELECT P_id, P_name, P_price, StockLevel FROM products WHERE LOWER(P_name) LIKE LOWER(?)";
    
    // We can't use try-with-resources because we need to return an open ResultSet
    try {
        Connection con = DBConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, "%" + productName + "%");
        return pst.executeQuery();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error searching product: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        return null;
    }
    }
    
    // Place this method inside your ProductController class

    public boolean updateProductPriceByName(String productName, double newPrice) {
        String sql = "UPDATE products SET P_price = ? WHERE P_name = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setDouble(1, newPrice);
            pst.setString(2, productName);
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating product price by name: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    public ResultSet getAllProducts() throws SQLException {
    String sql = "SELECT * FROM products";
    Connection con = null;
    PreparedStatement pst = null;
    try {
        con = DBConnection.getConnection();
        pst = con.prepareStatement(sql);
        return pst.executeQuery();
    } catch (SQLException e) {
        // Since we can't return the ResultSet if an error occurs, we must close
        // the resources here before re-throwing the exception.
        if (pst != null) pst.close();
        if (con != null) con.close();
        throw e;
    }
}
    
    // You can add methods for edit and delete here later
}



