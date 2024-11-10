package table;

import register.OperateTables;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TableManager {

    public static void showTables(Statement statement, String database) throws SQLException {
        // Cambiar a la base de datos seleccionada
        statement.execute("USE " + database);

        // Ejecutar el comando para mostrar las tablas
        ResultSet resultSet = statement.executeQuery("SHOW TABLES");
        System.out.println("\n=============================================");
        System.out.println("Tables in the database: " + database);

        int count = 1;
        List<String> tables = new ArrayList<>();

        // Almacenar y mostrar las tablas encontradas
        while (resultSet.next()) {
            String tableName = resultSet.getString(1);
            System.out.println(count + ". " + tableName);
            tables.add(tableName);
            count++;
        }

        if (tables.isEmpty()) {
            System.out.println("No tables available in this database.");
            return;
        }

        // Solicitar selección de una tabla
        Scanner scanner = new Scanner(System.in);
        System.out.print("Select a table by number (or enter 0 to go back): ");
        int selectedIndex = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea

        if (selectedIndex == 0) {
            System.out.println("Returning to the previous menu...");
            return;  // Finaliza la ejecución de showTables
        }

        if (selectedIndex < 1 || selectedIndex > tables.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        String selectedTable = tables.get(selectedIndex - 1);
        System.out.println("\nYou selected: " + selectedTable);

        // Mostrar el contenido de la tabla seleccionada
        System.out.println("\n=============================================");
        System.out.println("Contents of table: " + selectedTable);
        ResultSet tableContent = statement.executeQuery("SELECT * FROM " + selectedTable);
        ResultSetMetaData metaData = tableContent.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Mostrar nombres y tipo de dato de columnas
        ResultSet columnsResultSet = statement.executeQuery("SHOW COLUMNS FROM " + selectedTable);
        List<String> columns = new ArrayList<>();
        int columnCont = 1;
        while (columnsResultSet.next()) {
            String columnName = columnsResultSet.getString("Field");
            String columnType = columnsResultSet.getString("Type");
            System.out.println(columnCont + ". " + columnName + " (" + columnType + ")");
            columns.add(columnName);
            columnCont++;
        }

        boolean editfield = true;

        do {
            // Proporcionar opciones para agregar, eliminar columnas y/o trabajar con registros
            System.out.println("\n=============================================");
            System.out.println("Options:");
            System.out.println("1. Add a new column");
            System.out.println("2. Delete a column");
            System.out.println("3. Work with registers");
            System.out.println("0. Return to previous menu");
            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea

            switch (option) {
                case 1:
                    // Verificar si la columna `id` ya existe en la tabla
                    ResultSet rs = statement.executeQuery("SHOW COLUMNS FROM " + selectedTable + " LIKE 'id'");
                    if (!rs.next()) {
                        // Crear la columna `id` si no existe
                        String addIdColumnQuery = "ALTER TABLE " + selectedTable + " ADD id INT PRIMARY KEY AUTO_INCREMENT";
                        statement.executeUpdate(addIdColumnQuery);
                        System.out.println("Column 'id' added to table '" + selectedTable + "' as PRIMARY KEY with AUTO_INCREMENT.");
                    }

                    // Agregar una nueva columna personalizada
                    System.out.print("Enter the name of the new column: ");
                    String newColumnName = scanner.nextLine().trim();
                    System.out.println("\n===== Select the data type for the column =====");
                    System.out.println("1. INT (Integer)");
                    System.out.println("2. VARCHAR(255) (Text up to 255 characters)");
                    System.out.println("3. DOUBLE (Decimal number)");
                    System.out.println("4. OTHER (Custom data type)");
                    int dataTypeOption = scanner.nextInt();
                    scanner.nextLine(); // Consumir la nueva línea
                    String newDataType = switch (dataTypeOption) {
                        case 1 -> "INT";
                        case 2 -> "VARCHAR(255)";
                        case 3 -> "DOUBLE";
                        default -> {
                            System.out.print("Enter the custom data type: ");
                            yield scanner.nextLine().trim();
                        }
                    };

                    String addColumnQuery = "ALTER TABLE " + selectedTable + " ADD " + newColumnName + " " + newDataType;
                    statement.executeUpdate(addColumnQuery);
                    System.out.println("Column '" + newColumnName + "' with data type '" + newDataType + "' added to table '" + selectedTable + "'.");
                    break;

                case 2:
                    // Mostrar columnas existentes en la tabla con sus tipos de datos
                    System.out.println("\n=============================================");
                    System.out.println("Columns in table " + selectedTable + ":");
                    ResultSet columnsResSet = statement.executeQuery("SHOW COLUMNS FROM " + selectedTable);
                    List<String> col = new ArrayList<>();
                    int colCont = 1;
                    while (columnsResSet.next()) {
                        String columnName = columnsResSet.getString("Field");
                        String columnType = columnsResSet.getString("Type");
                        System.out.println(colCont + ". " + columnName + " (" + columnType + ")");
                        col.add(columnName);
                        colCont++;
                    }

                    // Eliminar una columna
                    System.out.print("Enter the name of the column to delete: ");
                    String columnNameToDelete = scanner.nextLine().trim();
                    if (!col.contains(columnNameToDelete)) {
                        System.out.println("Invalid column name.");
                        break;
                    }

                    // Confirmar la eliminación de la columna
                    System.out.print("Are you sure you want to delete the column '" + columnNameToDelete + "'? (y/n): ");
                    String confirmation = scanner.nextLine().trim().toLowerCase();
                    if (!confirmation.equals("y")) {
                        System.out.println("Column deletion cancelled.");
                        break;
                    }

                    try {
                        String deleteColumnQuery = "ALTER TABLE " + selectedTable + " DROP COLUMN " + columnNameToDelete;
                        statement.executeUpdate(deleteColumnQuery);
                        System.out.println("Column '" + columnNameToDelete + "' deleted from table '" + selectedTable + "'.");
                    } catch (SQLException e) {
                        System.out.println("Error deleting column: " + e.getMessage());
                    }
                    break;

                case 3:
                    OperateTables.interfaceRegister(statement, selectedTable);
                    break;

                case 0:
                    System.out.println("Returning to the previous menu...");
                    editfield = false;
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        } while (editfield);

//        scanner.close(); // Cerrar Scanner para liberar recursos
    }


    public static void deleteTable(Statement statement, String selectedDatabase, Scanner scanner) throws SQLException {
        // Cambiar a la base de datos seleccionada
        statement.execute("USE " + selectedDatabase);

        // Mostrar las tablas para que el usuario elija cuál borrar
        ResultSet resultSet = statement.executeQuery("SHOW TABLES");
        List<String> tables = new ArrayList<>();
        int count = 1;

        System.out.println("Tables in database '" + selectedDatabase + "':");
        while (resultSet.next()) {
            String tableName = resultSet.getString(1);
            tables.add(tableName);
            System.out.println(count + ". " + tableName);
            count++;
        }

        if (tables.isEmpty()) {
            System.out.println("No tables available for deletion.");
            return;
        }

        // Pedir al usuario que seleccione una tabla para borrar
        System.out.print("Enter the number of the table you want to delete: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (choice < 1 || choice > tables.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        String selectedTable = tables.get(choice - 1);

        // Confirmar la eliminación de la tabla
        System.out.print("Are you sure you want to delete the table '" + selectedTable + "'? (y/n): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("y")) {
            // Ejecutar el comando SQL para eliminar la tabla
            statement.executeUpdate("DROP TABLE " + selectedTable);
            System.out.println("Table '" + selectedTable + "' deleted successfully.");
        } else {
            System.out.println("Operation canceled. The table was not deleted.");
        }
    }


    public static void createTable(Statement statement, String selectedDatabase) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Cambiar a la base de datos seleccionada
        statement.execute("USE " + selectedDatabase);

        // Solicitar el nombre de la tabla
        System.out.print("Enter the name for the new table: ");
        String tableName = scanner.nextLine().trim();

        // Iniciar la consulta para crear la tabla con el campo 'id' como clave primaria auto incremental
        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE " + tableName + " (id INT AUTO_INCREMENT PRIMARY KEY");

        // Bucle para agregar más campos (columnas)
        boolean addMoreFields = true;
        while (addMoreFields) {
            System.out.print("Do you want to add a new column (field)? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("n")) {
                addMoreFields = false; // Finaliza la adición de columnas
            } else if (response.equals("y")) {
                // Solicitar el nombre de la nueva columna
                System.out.print("Enter the column (field) name: ");
                String fieldName = scanner.nextLine().trim();

                // Solicitar el tipo de datos de la columna usando un switch
                System.out.println("\n=====Select the data type for the column '" + fieldName + "'=====");
                System.out.println("1. INT (Integer)");
                System.out.println("2. VARCHAR(255) (Text up to 255 characters)");
                System.out.println("3. DOUBLE (Decimal number)");
                System.out.println("4. OTHER (Custom data type)");

                int dataTypeOption = Integer.parseInt(scanner.nextLine().trim());
                String dataType = switch (dataTypeOption) {
                    case 1 -> "INT";
                    case 2 -> "VARCHAR(255)";
                    case 3 -> "DOUBLE";
                    default -> {
                        System.out.print("Enter the custom data type for column '" + fieldName + "': ");
                        yield scanner.nextLine().trim();
                    }
                };

                // Agregar la nueva columna a la consulta
                createTableQuery.append(", ").append(fieldName).append(" ").append(dataType);
                System.out.println("Column '" + fieldName + "' with data type '" + dataType + "' added.");
            } else {
                System.out.println("Invalid response. Please type 'y' (yes) or 'n' (no).");
            }
        }


        // Completar la consulta de creación de tabla
        createTableQuery.append(");");

        // Imprimir la consulta para fines de depuración
        System.out.println("SQL Query: " + createTableQuery.toString());

        // Ejecutar la consulta para crear la tabla
        statement.executeUpdate(createTableQuery.toString());
        System.out.println("Table " + tableName + " created successfully in database " + selectedDatabase + ".");

        // Mostrar las tablas existentes después de crear la nueva tabla
        showTables(statement, selectedDatabase);

//        scanner.close();
    }

}
