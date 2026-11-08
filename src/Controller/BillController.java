package Controller;

import Controller.DBConnection;
import Model.Product;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import Model.Product; // Assuming you have this model
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate; // For date calculations
import java.time.Month; // For date calculations
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class BillController {

    // Method to fetch product details from the database
    public Product getProductDetails(int productId) {
        String query = "SELECT P_id, P_name, P_price FROM products WHERE P_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, productId);
            //returns  a product   
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Product(rs.getInt("P_id"), rs.getString("P_name"), rs.getDouble("P_price"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to save the bill and its items to the database
    public boolean saveBill(String customerName, String billDate, double totalAmount, int cashierId, Vector<Vector> billItemsData) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Start a database transaction

            // Handle date parsing to avoid IllegalArgumentException
            Date sqlDate = null;
            try {
                sqlDate = Date.valueOf(billDate);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid date format. Please use 'YYYY-MM-DD'.");
                e.printStackTrace();
                return false; // Exit if the date format is wrong
            }
            
            // 1. Insert into 'bills' table
            String billQuery = "INSERT INTO bills (customer_name, bill_date, total_amount, cashier_id) VALUES (?, ?, ?, ?)";
            PreparedStatement billPst = con.prepareStatement(billQuery, Statement.RETURN_GENERATED_KEYS);
            billPst.setString(1, customerName);
            billPst.setDate(2, sqlDate); 
            billPst.setDouble(3, totalAmount);
            billPst.setInt(4, cashierId);
            billPst.executeUpdate();

            // Get the generated bill_id
            ResultSet rs = billPst.getGeneratedKeys();
            if (!rs.next()) {
                con.rollback(); // If no bill ID is generated, roll back the transaction
                return false;
            }
            int billId = rs.getInt(1);

            // 2. Insert into 'bill_items' table
            String itemQuery = "INSERT INTO bill_items (bill_id, P_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement itemPst = con.prepareStatement(itemQuery);

            for (Vector item : billItemsData) {
                itemPst.setInt(1, billId);
                itemPst.setInt(2, (int) item.get(0));       // P_id
                itemPst.setInt(3, (int) item.get(2));       // Quantity
                itemPst.setDouble(4, (double) item.get(3)); // Unit Price
                itemPst.setDouble(5, (double) item.get(4)); // Subtotal
                itemPst.addBatch();
            }

            itemPst.executeBatch(); // Execute all item inserts at once
            con.commit();           // Commit the transaction
            return true;

        } catch (SQLException e) {
            // New: This line will print the specific database error to your console
            System.err.println("SQL Exception occurred during bill creation:");
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback(); // Rollback the transaction on any error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } 
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true); // Restore auto-commit
                    con.close(); // Close the connection
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public DefaultTableModel getAllBills() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Bill ID", "Customer Name", "Bill Date", "Total Amount", "Cashier ID"}, 0);
        String query = "SELECT bill_id, customer_name, bill_date, total_amount, cashier_id FROM bills ORDER BY bill_date DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("bill_id"),
                    rs.getString("customer_name"),
                    rs.getDate("bill_date"), // Retrieves as java.sql.Date
                    rs.getDouble("total_amount"),
                    rs.getInt("cashier_id")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all bills:");
            e.printStackTrace();
        }
        return model;
    }

    /**
     * Fetches bills from the database filtered by quarter and year.
     * @param quarter The quarter number (1-4).
     * @param year The year.
     * @return A DefaultTableModel containing filtered bill data.
     */
    public DefaultTableModel getBillsByQuarter(int quarter) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Bill ID", "Customer Name", "Bill Date", "Total Amount", "Cashier ID"}, 0);
        
        LocalDate startDate;
        LocalDate endDate;
        // Use the current year for the date range
        int year = java.time.Year.now().getValue();

        switch (quarter) {
            case 1: // Jan-Mar
                startDate = LocalDate.of(year, 1, 1);
                endDate = LocalDate.of(year, 3, 31);
                break;
            case 2: // Apr-Jun
                startDate = LocalDate.of(year, 4, 1);
                endDate = LocalDate.of(year, 6, 30);
                break;
            case 3: // Jul-Sep
                startDate = LocalDate.of(year, 7, 1);
                endDate = LocalDate.of(year, 9, 30);
                break;
            case 4: // Oct-Dec
                startDate = LocalDate.of(year, 10, 1);
                endDate = LocalDate.of(year, 12, 31);
                break;
            default:
                // Return an empty model if the quarter is invalid
                return model;
        }

        // The query now uses a more robust comparison to handle DATETIME columns
        // bill_date >= startDate AND bill_date < (endDate + 1 day)
        String query = "SELECT bill_id, customer_name, bill_date, total_amount, cashier_id " +
                       "FROM bills WHERE bill_date >= ? AND bill_date < ? ORDER BY bill_date DESC";
        
        // Add one day to the end date to ensure all of the last day is included
        LocalDate endDatePlusOneDay = endDate.plusDays(1);
        
        System.out.println("DEBUG: Executing query with dates: " + startDate + " to " + endDatePlusOneDay);
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            
            pst.setDate(1, Date.valueOf(startDate));
            pst.setDate(2, Date.valueOf(endDatePlusOneDay));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("bill_id"),
                        rs.getString("customer_name"),
                        rs.getDate("bill_date"),
                        rs.getDouble("total_amount"),
                        rs.getInt("cashier_id")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bills by quarter:");
            e.printStackTrace();
        }
        return model;
    }
}