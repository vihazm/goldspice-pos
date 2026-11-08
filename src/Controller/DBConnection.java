package Controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    // Database settings are NOT stored in the code. They are read from:
    //   1. environment variables GOLDSPICE_DB_URL / GOLDSPICE_DB_USER / GOLDSPICE_DB_PASSWORD, or
    //   2. a db.properties file in the folder the app is started from (git-ignored).
    // See db.properties.example for the format.
    private static final Properties CONFIG = loadConfig();

    private static final String URL = setting("GOLDSPICE_DB_URL", "db.url", "jdbc:mysql://localhost:3306/goldspice_store");
    private static final String USERNAME = setting("GOLDSPICE_DB_USER", "db.user", "root");
    private static final String PASSWORD = setting("GOLDSPICE_DB_PASSWORD", "db.password", "");

    private static Properties loadConfig() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            props.load(in);
        } catch (IOException e) {
            // No file: fall back to environment variables / defaults
        }
        return props;
    }

    private static String setting(String envName, String propName, String defaultValue) {
        String env = System.getenv(envName);
        if (env != null && !env.isEmpty()) {
            return env;
        }
        return CONFIG.getProperty(propName, defaultValue);
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {//to attempt a connection with the database
            Class.forName("com.mysql.cj.jdbc.Driver");//loads the driver into the memory
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);//Attempts a connection using given variables
            System.out.println("Database Connected Successfully!");//Relays successful connection

          //what happens if the try fails, passed onto exception e.
        } catch (ClassNotFoundException e) {//If the Class.forName fails to find database
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {//If the DriverManager fails to connect to the database.
            System.err.println("Database Connection Failed.");
            e.printStackTrace();
        }
        return connection;//Returns the connection, if successful or failed.
    }
}
