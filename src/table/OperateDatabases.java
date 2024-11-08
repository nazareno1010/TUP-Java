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

    public static void exportDatabaseToCSV() {
        try (Connection conn = ConnectionManager.getConnection();
             Statement statement = conn.createStatement()) {

            // 1. Listar bases de datos y permitir que el usuario seleccione una
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

            // Solicitar al usuario la ruta de guardado
            System.out.print("Enter the file path to save the CSV (e.g., C:/exports/database.csv): ");
            String filePath = scanner.nextLine();

            // Convertir las barras inclinadas a barras invertidas
            filePath = convertSlashesToBackslashes(filePath);

            // Verificar el valor de filePath después de la conversión
            System.out.println("Converted file path: " + filePath);

            // Exportar la base de datos a la ruta modificada
            exportTablesToCSV(databaseName, filePath, conn);


            System.out.println("Database exported successfully to " + filePath);
        } catch (SQLException e) {
            System.out.println("Error retrieving databases: " + e.getMessage());
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
    public static void interfaceTable(Statement statement, String selectedDatabase) throws SQLException {
        Scanner scanner = new Scanner(System.in);
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
                    showTables(statement, selectedDatabase);
                    break;
                case 2:
                    TableManager.createTable(statement, selectedDatabase);
                    break;
                case 3:
                    TableManager.deleteTable(statement, selectedDatabase, scanner);
                    break;
                case 0:
                    System.out.println("Returning to database selection...");
                    return;
                default:
                    System.out.println("Invalid option. Please, try again.");
                    break;
            }

        } while (option != 0);
    }
}


