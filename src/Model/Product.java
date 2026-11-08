package Model;

/**
 * A simple Java object (POJO) to represent a product from the database.
 * This acts as the "Model" in the MVC pattern.
 */
public class Product {
    private int P_id;
    private String P_name;
    private double P_price;
    
    // Constructor to initialize a Product object
    public Product(int P_id, String P_name, double P_price) {
        this.P_id = P_id;
        this.P_name = P_name;
        this.P_price = P_price;
    }

    // Getters for the properties
    public int getP_id() {
        return P_id;
    }

    public String getP_name() {
        return P_name;
    }

    public double getP_price() {
        return P_price;
    }
}
