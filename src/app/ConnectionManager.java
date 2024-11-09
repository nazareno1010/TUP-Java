package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static Connection connection;

    public static boolean initialize(String url, String username, String password) {
        if (connection != null) {
            // Si ya hay una conexión activa, no es necesario volver a inicializar
            return true;
        }

        try {
            connection = DriverManager.getConnection(url, username, password);
            return true;
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            return false;
        }
    }

    public static boolean isConnected() {
        return connection != null;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing the connection: " + e.getMessage());
            }
        }
    }
}



