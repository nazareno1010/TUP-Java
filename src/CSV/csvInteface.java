package CSV;

import app.ConnectionManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class csvInteface {

    public static Scanner scanner = new Scanner(System.in);

    public static void exportToCSV() {
        Connection conn = null;
        Statement statement = null;

        try {
            conn = ConnectionManager.getConnection();
            statement = conn.createStatement();

            // Crear carpeta 'exported' si no existe
            File exportFolder = new File("exported");
            if (!exportFolder.exists()) {
                exportFolder.mkdir();
            }

            System.out.println("Do you want to export a whole database or just a table?");
            System.out.println("1. Export entire database");
            System.out.println("2. Export a specific table");
            System.out.println("0. Return to database menu");
            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            if (choice == 0) {
                System.out.println("Returning to database menu...");
                return; // Salir del método para regresar al menú en Main
            } else if (choice == 1) {
                exportDatabaseToCSV(statement, conn);
            } else if (choice == 2) {
                exportTableToCSV(statement, conn);
            } else {
                System.out.println("Invalid choice.");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void exportDatabaseToCSV(Statement statement, Connection conn) throws SQLException {
        List<String> databases = getDatabases(statement);
        if (databases.isEmpty()) {
            System.out.println("No available databases to export.");
            return;
        }

        System.out.println("Select the database to export:");
        int dbIndex = selectFromList(databases);
        if (dbIndex == -1) return;
        String databaseName = databases.get(dbIndex);

        File dbFolder = new File("exported/" + databaseName);
        if (!dbFolder.exists()) {
            dbFolder.mkdir();
        }

        try (Statement dbStatement = conn.createStatement()) {
            ResultSet tables = dbStatement.executeQuery("SHOW TABLES FROM " + databaseName);
            while (tables.next()) {
                String tableName = tables.getString(1);
                exportTableToCSV(databaseName, tableName, dbFolder.getAbsolutePath(), conn);
            }
            System.out.println("Database exported successfully to folder: " + dbFolder.getPath());
        }
    }

    private static void exportTableToCSV(Statement statement, Connection conn) throws SQLException {
        List<String> databases = getDatabases(statement);
        if (databases.isEmpty()) {
            System.out.println("No available databases to export.");
            return;
        }

        System.out.println("Select the database containing the table to export:");
        int dbIndex = selectFromList(databases);
        if (dbIndex == -1) return;
        String databaseName = databases.get(dbIndex);

        List<String> tables = getTables(databaseName, conn);
        if (tables.isEmpty()) {
            System.out.println("No tables found in database: " + databaseName);
            return;
        }

        System.out.println("Select a table to export:");
        int tableIndex = selectFromList(tables);
        if (tableIndex == -1) return;
        String tableName = tables.get(tableIndex);

        File dbFolder = new File("exported/" + databaseName);
        if (!dbFolder.exists()) {
            dbFolder.mkdir();
        }

        exportTableToCSV(databaseName, tableName, dbFolder.getAbsolutePath(), conn);
        System.out.println("Table exported successfully to file: " + dbFolder.getPath() + "/" + tableName + ".csv");
    }

    private static void exportTableToCSV(String databaseName, String tableName, String folderPath, Connection conn) {
        String csvFilePath = folderPath + "/" + tableName + ".csv";
        String query = "SELECT * FROM " + databaseName + "." + tableName;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter csvWriter = new FileWriter(new File(csvFilePath))) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Escribir encabezados de columna
            for (int i = 1; i <= columnCount; i++) {
                csvWriter.append(metaData.getColumnName(i));
                if (i < columnCount) csvWriter.append(",");
            }
            csvWriter.append("\n");

            // Escribir filas de datos
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    csvWriter.append(rs.getString(i));
                    if (i < columnCount) csvWriter.append(",");
                }
                csvWriter.append("\n");
            }

            System.out.println("Table " + tableName + " exported to " + csvFilePath);
        } catch (SQLException | IOException e) {
            System.out.println("Error exporting table " + tableName + ": " + e.getMessage());
        }
    }

    private static List<String> getDatabases(Statement statement) throws SQLException {
        List<String> databases = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery("SHOW DATABASES");

        while (resultSet.next()) {
            String dbName = resultSet.getString(1);
            if (!dbName.equals("information_schema") && !dbName.equals("mysql") &&
                    !dbName.equals("performance_schema") && !dbName.equals("sys")) {
                databases.add(dbName);
            }
        }
        return databases;
    }

    private static List<String> getTables(String databaseName, Connection conn) throws SQLException {
        List<String> tables = new ArrayList<>();
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SHOW TABLES FROM " + databaseName);

        while (resultSet.next()) {
            tables.add(resultSet.getString(1));
        }
        return tables;
    }

    private static int selectFromList(List<String> items) {
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i));
        }
        System.out.print("Select an option (0 to cancel): ");
        int selection = scanner.nextInt() - 1;
        scanner.nextLine();  // Consume newline

        if (selection >= 0 && selection < items.size()) {
            return selection;
        } else {
            System.out.println("Cancelled.");
            return -1;
        }
    }
}
