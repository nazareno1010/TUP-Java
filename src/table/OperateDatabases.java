package table;

import app.ConnectionManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import static app.Main.EXCLUDED_DATABASES;
import static app.Main.scanner;
import static table.TableManager.showTables;

public class OperateDatabases {

    public static String convertSlashesToBackslashes(String filePath) {
        // Reemplaza las barras inclinadas (/) por barras invertidas (\\)
        return filePath.replace("/", "\\");
    }


    public static List<String> showDatabases(Statement statement, Set<String> excludedDatabases) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SHOW DATABASES");
        List<String> databases = new ArrayList<>();

        System.out.println("\n=============================================");
        System.out.println("Databases on the server:");

        int count = 1;
        while (resultSet.next()) {
            String dbName = resultSet.getString(1);
            if (!excludedDatabases.contains(dbName)) {
                System.out.println(count + ". " + dbName);
                databases.add(dbName);
                count++;
            }
        }

        if (databases.isEmpty()) {
            System.out.println("No databases available.");
        }
        return databases;
    }

    // Modificación en exportDatabaseToCSV
    public static void exportDatabaseToCSV(Statement statement) {
        try {
            List<String> databases = showDatabases(statement, EXCLUDED_DATABASES);

            System.out.println("Select the database to export:");
            if (databases.isEmpty()) {
                System.out.println("No available databases to export.");
                return;
            }

            int dbIndex;
            while (true) {
                System.out.print("Enter the number of the database: ");
                if (scanner.hasNextInt()) {
                    dbIndex = scanner.nextInt() - 1;
                    scanner.nextLine();  // Consume newline
                    if (dbIndex >= 0 && dbIndex < databases.size()) break;
                } else {
                    scanner.nextLine();  // Consume invalid input
                }
                System.out.println("Invalid choice. Try again.");
            }

            String databaseName = databases.get(dbIndex);
            System.out.print("Enter the file path to save the CSV (e.g., C:/exports/database.csv): ");
            String filePath = scanner.nextLine();
            filePath = convertSlashesToBackslashes(filePath);

            // Exporta cada tabla de la base de datos seleccionada
            exportTablesToCSV(databaseName, filePath, statement.getConnection());
            System.out.println("Database exported successfully to " + filePath);

        } catch (SQLException e) {
            System.out.println("Error retrieving databases: " + e.getMessage());
        }
    }


    // Método para cerrar recursos
    private static void closeResources(Statement statement, Connection conn) {
        try {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }


    private static void exportTablesToCSV(String databaseName, String filePath, Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Obtener nombres de tablas
            ResultSet tables = stmt.executeQuery("SHOW TABLES FROM " + databaseName);

            while (tables.next()) {
                String tableName = tables.getString(1);
                exportTableToCSV(databaseName, tableName, filePath, conn);
            }
        } catch (SQLException e) {
            System.out.println("Error exporting tables: " + e.getMessage());
        }
    }

    private static void exportTableToCSV(String databaseName, String tableName, String filePath, Connection conn) {
        String csvFilePath = filePath + "_" + tableName + ".csv";
        String query = "SELECT * FROM " + databaseName + "." + tableName;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter csvWriter = new FileWriter(new File(csvFilePath))) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Escribir nombres de columna
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

    // Método para gestionar las operaciones sobre las tablas de la base de datos seleccionada
    public static void interfaceTable(Statement statement) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Mostrar bases de datos disponibles
        List<String> databases = showDatabases(statement, EXCLUDED_DATABASES);

        if (databases.isEmpty()) {
            System.out.println("No databases available to select.");
            return;
        }

        // Permitir al usuario seleccionar una base de datos
        System.out.println("\nSelect the database to work with:");
        int dbIndex;
        while (true) {
            System.out.print("Enter the number of the database: ");
            if (scanner.hasNextInt()) {
                dbIndex = scanner.nextInt() - 1;
                scanner.nextLine();  // Consumir el salto de línea
                if (dbIndex >= 0 && dbIndex < databases.size()) break;
            } else {
                scanner.nextLine();  // Consumir la entrada inválida
            }
            System.out.println("Invalid choice. Try again.");
        }

        String selectedDatabase = databases.get(dbIndex);
        System.out.println("You selected: " + selectedDatabase);

        // Ahora que se seleccionó la base de datos, podemos entrar en la interfaz para gestionar las tablas de esa base
        int option;
        do {
            System.out.println("\n===== DBSM - Database Management System =====");
            System.out.println("1. Show Tables");
            System.out.println("2. Create Tables");
            System.out.println("3. Delete Tables");
            System.out.println("0. Back");
            System.out.print("Select an option: ");

            option = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer del scanner

            switch (option) {
                case 1:
                    showTables(statement, selectedDatabase); // Mostrar las tablas de la base de datos seleccionada
                    break;
                case 2:
                    TableManager.createTable(statement, selectedDatabase); // Crear una tabla en la base seleccionada
                    break;
                case 3:
                    TableManager.deleteTable(statement, selectedDatabase, scanner); // Eliminar una tabla en la base seleccionada
                    break;
                case 0:
                    System.out.println("Returning to database selection...");
                    return; // Salir de la interfaz de tablas y regresar a la selección de base de datos
                default:
                    System.out.println("Invalid option. Please, try again.");
                    break;
            }

        } while (option != 0);
    }
}



