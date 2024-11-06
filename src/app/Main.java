package app;

import table.OperateDatabases;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    public static final Set<String> EXCLUDED_DATABASES = new HashSet<>();

    static {
        EXCLUDED_DATABASES.add("information_schema");
        EXCLUDED_DATABASES.add("mysql");
        EXCLUDED_DATABASES.add("performance_schema");
        EXCLUDED_DATABASES.add("sakila");
        EXCLUDED_DATABASES.add("sys");
        EXCLUDED_DATABASES.add("world");
    }

    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String password;

        do {
            System.out.println("\n===== DBSM - Database Management System =====");
            System.out.print("Enter the MySQL password: ");
            password = scanner.nextLine();
        } while (!ConnectionManager.initialize(URL, USER, password));

        interfaceDatabase();
        scanner.close();
        ConnectionManager.closeConnection();
    }

    public static void interfaceDatabase(){

        try (Statement statement = ConnectionManager.getConnection().createStatement()) {
            int option;
            do {
                System.out.println("\n===== DBSM - Database Management System =====");
                System.out.println("1. Show Databases");
                System.out.println("2. Create Database");
                System.out.println("3. Delete Database");
                System.out.println("0. Exit");
                System.out.print("Select an option: ");

                option = scanner.nextInt();
                scanner.nextLine();

                try {
                    switch (option) {
                        case 1:
                            OperateDatabases.showDatabases(statement);
                            break;
                        case 2:
                            DatabaseManager.createDatabase(statement, scanner);
                            break;
                        case 3:
                            DatabaseManager.deleteDatabase(statement, scanner);
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
        } catch (SQLException e) {
            System.out.println("Error initializing database connection: " + e.getMessage());
        }

    }

}