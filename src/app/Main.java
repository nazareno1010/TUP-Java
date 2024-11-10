package app;

import table.OperateDatabases;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
//import static table.OperateDatabases.exportDatabaseToCSV;
import static CSV.csvInteface.exportToCSV;
import static table.OperateDatabases.interfaceTable;
import static table.TableManager.showTables;

public class Main {
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
        ConfigManager.loginUser(scanner);

        if (ConfigManager.isLoggedIn() && ConnectionManager.isConnected()) {
            System.out.println("Proceeding to database interface...");
            interfaceDatabase();
        } else {
            System.out.println("Could not establish a connection to the database or no user selected.");
        }

        scanner.close();
        ConnectionManager.closeConnection();
    }
    public static void interfaceDatabase() {
        try (Statement statement = ConnectionManager.getConnection().createStatement()) {
            int option = -1;
            do {
                System.out.println("\n===== DBSM - Database Management System =====");
                System.out.println("1. Show Databases");
                System.out.println("2. Create Database");
                System.out.println("3. Delete Database");
                System.out.println("4. Export Database to .CSV");
//                System.out.println("5. Work With DataBases");
                System.out.println("0. Exit");
                System.out.print("Select an option: ");

                if (scanner.hasNextInt()) {
                    option = scanner.nextInt();
                    scanner.nextLine();

                    try {
                        switch (option) {
                            case 1:
                                OperateDatabases.showDatabases(statement, EXCLUDED_DATABASES);
                                break;
                            case 2:
                                DatabaseManager.createDatabase(statement, scanner);
                                break;
                            case 3:
                                DatabaseManager.deleteDatabase(statement, scanner);
                                break;
                            case 4:
                                exportToCSV();
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
                } else {
                    System.out.println("Invalid input. Please enter a valid number.");
                    scanner.nextLine();
                }
            } while (option != 0);
        } catch (SQLException e) {
            System.out.println("Error initializing database connection: " + e.getMessage());
        }
    }
}














