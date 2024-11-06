package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static Connection connection;

    public static boolean initialize(String url, String user, String password) {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Connection established successfully.");
                return true;
            } catch (SQLException e) {
                System.out.println("Invalid password. Please try again.");
                return false;
            }
        }
        return true;
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
            }
        }
    }
}