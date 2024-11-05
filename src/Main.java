import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "3Mas4son7_"; // Replace with your password
    private static final Set<String> EXCLUDED_DATABASES = new HashSet<>(Arrays.asList(
            "information_schema", "mysql", "performance_schema", "sakila", "sys", "world"
    ));

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("===== DBSM - Database Management System =====");
            System.out.println("1. Show Databases");
            System.out.println("2. Create Database");
            System.out.println("3. Delete Database");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            option = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            try {
                switch (option) {
                    case 1:
                        showDatabases(scanner); // Pass scanner to use it in showDatabases
                        break;
                    case 2:
                        createDatabase(scanner);
                        break;
                    case 3:
                        deleteDatabase(scanner);
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid option. Please, try again.");
                        break;
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        } while (option != 0);

        scanner.close();
    }

    public static void showDatabases(Scanner scanner) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String sql = "SHOW DATABASES";
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("Databases on the server:");
            List<String> databases = new ArrayList<>();
            int count = 1;

            while (resultSet.next()) {
                String dbName = resultSet.getString(1);
                if (!EXCLUDED_DATABASES.contains(dbName)) {
                    databases.add(dbName);
                    System.out.println(count + ". " + dbName);
                    count++;
                }
            }

            if (databases.isEmpty()) {
                System.out.println("No databases available.");
                return;
            }

            System.out.println(count + ". Exit");
            System.out.print("Select a database by number to manage it or choose 'Exit': ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            if (choice > 0 && choice <= databases.size()) {
                String selectedDatabase = databases.get(choice - 1);
                selectDatabase(scanner, selectedDatabase);
            } else if (choice == count) {
                System.out.println("Returning to main menu...");
            } else {
                System.out.println("Invalid selection.");
            }
        }
    }

    public static void selectDatabase(Scanner scanner, String dbName) throws SQLException {
        int option;

        do {
            System.out.println("\n===== Database: " + dbName + " =====");
            System.out.println("1. Show Tables");
            System.out.println("2. Other Actions (to be implemented)");
            System.out.println("0. Back to Database List");
            System.out.print("Select an option: ");

            option = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            try {
                switch (option) {
                    case 1:
                        showTables(dbName);
                        break;
                    case 2:
                        System.out.println("Option not implemented yet.");
                        break;
                    case 0:
                        System.out.println("Returning to database selection...");
                        break;
                    default:
                        System.out.println("Invalid option. Please, try again.");
                        break;
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        } while (option != 0);
    }

    public static void showTables(String dbName) throws SQLException {
        String fullUrl = URL + dbName; // Connect to the specific database

        try (Connection connection = DriverManager.getConnection(fullUrl, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String sql = "SHOW TABLES";
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("Tables in database '" + dbName + "':");
            int count = 1;
            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                System.out.println(count + ". " + tableName);
                count++;
            }

            if (count == 1) {
                System.out.println("No tables found in the database.");
            }
        }
    }

    public static void createTable(Scanner scanner, String dbName) throws SQLException {
        System.out.print("Enter the name of the table to create: ");
        String tableName = scanner.nextLine();

        // Prompt for column details
        System.out.print("Enter column definitions (e.g., 'id INT PRIMARY KEY, name VARCHAR(255)'): ");
        String columns = scanner.nextLine();

        String fullUrl = URL + dbName; // Complete URL with database name
        try (Connection connection = DriverManager.getConnection(fullUrl, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            // Create the table
            String sql = "CREATE TABLE " + tableName + " (" + columns + ")";
            statement.executeUpdate(sql);
            System.out.println("Table '" + tableName + "' created successfully in database '" + dbName + "'.");
        } catch (SQLException e) {
            System.out.println("Failed to create table: " + e.getMessage());
        }
    }

    public static void createDatabase(Scanner scanner) throws SQLException {
        System.out.print("Enter the name of the new database: ");
        String dbName = scanner.nextLine();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            // Create the database
            String sql = "CREATE DATABASE " + dbName;
            statement.executeUpdate(sql);
            System.out.println("Database '" + dbName + "' created successfully.");

            // Ask the user if they want to create tables
            System.out.print("Do you want to create tables in the new database? (y/n): ");
            String createTableResponse = scanner.nextLine();
            if (createTableResponse.equalsIgnoreCase("y")) {
                createTable(scanner, dbName);
            }
        } catch (SQLException e) {
            System.out.println("Failed to create database: " + e.getMessage());
        }
    }

    public static void deleteDatabase(Scanner scanner) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            // Display databases as a numbered list
            String sql = "SHOW DATABASES";
            ResultSet resultSet = statement.executeQuery(sql);
            List<String> databases = new ArrayList<>();

            System.out.println("Databases on the server:");
            int count = 1;
            while (resultSet.next()) {
                String dbName = resultSet.getString(1);
                if (!EXCLUDED_DATABASES.contains(dbName)) {
                    databases.add(dbName);
                    System.out.println(count + ". " + dbName);
                    count++;
                }
            }

            if (databases.isEmpty()) {
                System.out.println("No databases available for deletion.");
                return;
            }

            // Prompt the user to select a database to delete
            System.out.print("Enter the number of the database you want to delete: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            if (choice < 1 || choice > databases.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            String selectedDatabase = databases.get(choice - 1);

            // Confirm deletion
            System.out.print("Are you sure you want to delete the database '" + selectedDatabase + "'? (y/n): ");
            String confirmation = scanner.nextLine();

            if (confirmation.equalsIgnoreCase("y")) {
                statement.executeUpdate("DROP DATABASE " + selectedDatabase);
                System.out.println("Database '" + selectedDatabase + "' deleted successfully.");
            } else {
                System.out.println("Operation canceled. The database was not deleted.");
            }
        }
    }
}
