package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static Connection connection;

    public static boolean initialize(String url, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found: " + e.getMessage());
            return false;
        }

        if (connection == null || !isConnectionActive()) {
            try {
                closeConnection();

                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Connection established successfully.");
                return true;
            } catch (SQLException e) {
                System.out.println("Connection failed: " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("Using existing connection.");
            return true;
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("No active database connection.");
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    private static boolean isConnectionActive() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}



