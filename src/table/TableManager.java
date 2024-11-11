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
            System.out.println("\n===== Menu =====");
            System.out.println("1. View existing columns");
            System.out.println("2. Create a new column");
            System.out.println("3. Delete an existing column");
            System.out.println("4. Create a new record");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea

            switch (option) {
                case 1:
                    // Mostrar columnas existentes en la tabla con sus tipos de datos
                    System.out.println("\n=============================================");
                    System.out.println("Columns in table " + selectedTable + ":");
                    ResultSet colRestSet = statement.executeQuery("SHOW COLUMNS FROM " + selectedTable);
                    while (colRestSet.next()) {
                        String columnName = colRestSet.getString("Field");
                        String columnType = colRestSet.getString("Type");
                        System.out.println(columnName + " (" + columnType + ")");
                    }
                    break;

                case 2:
                    // Verificar si la columna `id` ya existe en la tabla
                    ResultSet rs = statement.executeQuery("SHOW COLUMNS FROM " + selectedTable + " LIKE 'id'");
                    if (!rs.next()) {
                        // Crear la columna `id` si no existe
                        String addIdColumnQuery = "ALTER TABLE " + selectedTable + " ADD id INT PRIMARY KEY AUTO_INCREMENT";
                        statement.executeUpdate(addIdColumnQuery);
                        System.out.println("Column 'id' added to table '" + selectedTable + "' as PRIMARY KEY with AUTO_INCREMENT.");
                    }

                    // Bucle para crear columnas
                    boolean continueAddingColumns = true;
                    while (continueAddingColumns) {
                        boolean validInput = false;
                        String newColumnName = "";

                        // Solicitar el nombre de la nueva columna
                        while (!validInput) {
                            System.out.print("Enter the name of the new column (or enter 0 to cancel): ");
                            newColumnName = scanner.nextLine().trim();

                            // Cancelar la operación si el usuario ingresa 0
                            if (newColumnName.equals("0")) {
                                System.out.println("Operation cancelled.");
                                continueAddingColumns = false; // Salir del bucle principal
                                break; // Salir del bucle de validación de la columna
                            }

                            // Verificar si ya existe una columna con ese nombre
                            ResultSet columnCheck = statement.executeQuery("SHOW COLUMNS FROM " + selectedTable + " LIKE '" + newColumnName + "'");
                            if (columnCheck.next()) {
                                // Si la columna ya existe, informar al usuario y pedir un nuevo nombre
                                System.out.println("A column with the name '" + newColumnName + "' already exists. Please enter a different name.");
                            } else {
                                // Si no existe la columna, validar el nombre
                                if (newColumnName.matches("[a-zA-Z]+")) {
                                    validInput = true;
                                } else {
                                    System.out.println("Invalid column name. The name can only contain letters. Please try again.");
                                }
                            }
                        }

                        if (!newColumnName.equals("0")) {
                            validInput = false;
                            int dataTypeOption = -1;

                            // Solicitar el tipo de datos de la columna
                            while (!validInput) {
                                System.out.println("\n===== Select the data type for the column =====");
                                System.out.println("1. INT (Integer)");
                                System.out.println("2. VARCHAR(255) (Text up to 255 characters)");
                                System.out.println("3. DOUBLE (Decimal number)");
                                System.out.println("4. OTHER (Custom data type)");
                                System.out.print("Enter your choice (or enter 0 to cancel): ");

                                String input = scanner.nextLine().trim();

                                try {
                                    dataTypeOption = Integer.parseInt(input);

                                    // Cancelar la operación si el usuario ingresa 0
                                    if (dataTypeOption == 0) {
                                        System.out.println("Operation cancelled.");
                                        continueAddingColumns = false; // Salir del bucle principal
                                        break;
                                    }

                                    // Validar opción
                                    if (dataTypeOption >= 1 && dataTypeOption <= 4) {
                                        validInput = true;
                                    } else {
                                        System.out.println("Invalid choice. Please select a valid option.");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid input. Please enter a valid number.");
                                }
                            }

                            if (validInput) {
                                String newDataType = switch (dataTypeOption) {
                                    case 1 -> "INT";
                                    case 2 -> "VARCHAR(255)";
                                    case 3 -> "DOUBLE";
                                    default -> {
                                        System.out.print("Enter the custom data type: ");
                                        yield scanner.nextLine().trim();
                                    }
                                };

                                // Agregar la columna a la tabla
                                String addColumnQuery = "ALTER TABLE " + selectedTable + " ADD " + newColumnName + " " + newDataType;
                                try {
                                    statement.executeUpdate(addColumnQuery);
                                    System.out.println("Column '" + newColumnName + "' with data type '" + newDataType + "' added to table '" + selectedTable + "'.");
                                } catch (SQLException e) {
                                    System.out.println("Error adding column: " + e.getMessage());
                                }
                            }
                        }

                        // Preguntar si el usuario desea agregar otra columna
                        if (!newColumnName.equals("0") && continueAddingColumns) {
                            boolean validResponse = false;
                            while (!validResponse) {
                                System.out.print("\nDo you want to add another column? (y/n): ");
                                String response = scanner.nextLine().trim().toLowerCase();

                                if (response.equals("y")) {
                                    validResponse = true; // Continúa añadiendo columnas
                                } else if (response.equals("n")) {
                                    continueAddingColumns = false; // Finaliza la adición de columnas
                                    validResponse = true;
                                } else {
                                    System.out.println("Invalid response. Please type 'y' for yes or 'n' for no.");
                                }
                            }
                        }
                    }
                    break;

                case 3:
                    boolean validInp = false;
                    while (!validInp) {
                        // Mostrar columnas existentes en la tabla con sus tipos de datos
                        System.out.println("\n=============================================");
                        System.out.println("Columns in table " + selectedTable + ":");
                        ResultSet columnsResSet = statement.executeQuery("SHOW COLUMNS FROM " + selectedTable);
                        List<String> col = new ArrayList<>();
                        int colCont = 1;
                        while (columnsResSet.next()) {
                            String columnName = columnsResSet.getString("Field");
                            if (!columnName.equalsIgnoreCase("id")) { // Excluir la columna 'id'
                                String columnType = columnsResSet.getString("Type");
                                System.out.println(colCont + ". " + columnName + " (" + columnType + ")");
                                col.add(columnName);
                                colCont++;
                            }
                        }

                        // Si no hay columnas eliminables, salir del método
                        if (col.isEmpty()) {
                            System.out.println("No removable columns available.");
                            break;
                        }

                        // Solicitar al usuario el número de la columna a eliminar
                        System.out.print("\nEnter the number of the column to delete (or enter 0 to cancel): ");
                        String input = scanner.nextLine().trim();

                        // Cancelar la operación si el usuario ingresa 0
                        if (input.equals("0")) {
                            System.out.println("Operation cancelled.");
                            break;
                        }

                        int columnNumberToDelete;
                        try {
                            columnNumberToDelete = Integer.parseInt(input);
                        } catch (NumberFormatException e) {
                            System.out.println("\nInvalid input. Please enter a valid number.");
                            continue; // Volver a pedir la entrada del usuario
                        }

                        // Validar el número de la columna
                        if (columnNumberToDelete < 1 || columnNumberToDelete > col.size()) {
                            System.out.println("\nInvalid column number. Please try again.");
                            continue; // Volver a pedir la entrada del usuario
                        }

                        // Obtener el nombre de la columna a eliminar
                        String columnNameToDelete = col.get(columnNumberToDelete - 1);

                        // Confirmar la eliminación de la columna
                        System.out.print("Are you sure you want to delete the column '" + columnNameToDelete + "'? (y/n): ");
                        String confirmation = scanner.nextLine().trim().toLowerCase();
                        if (!confirmation.equals("y")) {
                            System.out.println("\nColumn deletion cancelled.");
                            break;
                        }

                        try {
                            String deleteColumnQuery = "ALTER TABLE " + selectedTable + " DROP COLUMN " + columnNameToDelete;
                            statement.executeUpdate(deleteColumnQuery);
                            System.out.println("\nColumn '" + columnNameToDelete + "' deleted from table '" + selectedTable + "'.");
                        } catch (SQLException e) {
                            System.out.println("Error deleting column: " + e.getMessage());
                        }

                        validInp = true; // Salir del bucle solo si la operación fue exitosa
                    }
                    break;
                case 4:
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

        while (true) {
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
            System.out.print("Enter the number of the table you want to delete (or enter 0 to cancel): ");
            String input = scanner.nextLine().trim();

            // Cancelar la operación si el usuario ingresa 0
            if (input.equals("0")) {
                System.out.println("Operation cancelled.");
                return;
            }

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                continue; // Volver a pedir la entrada del usuario
            }

            if (choice < 1 || choice > tables.size()) {
                System.out.println("Invalid selection. Please try again.");
                continue; // Volver a pedir la entrada del usuario
            }

            String selectedTable = tables.get(choice - 1);

            // Confirmar la eliminación de la tabla
            System.out.print("Are you sure you want to delete the table '" + selectedTable + "'? (y/n): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("y")) {
                // Ejecutar el comando SQL para eliminar la tabla
                statement.executeUpdate("DROP TABLE " + selectedTable);
                System.out.println("Table '" + selectedTable + "' deleted successfully.");
            } else {
                System.out.println("Operation canceled. The table was not deleted.");
            }

            break; // Salir del bucle después de procesar una entrada válida
        }
    }

    public static void createTable(Statement statement, String selectedDatabase) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Cambiar a la base de datos seleccionada
        statement.execute("USE " + selectedDatabase);

        String tableName = null;
        boolean validTableName = false;

        // Solicitar el nombre de la tabla y validar
        while (!validTableName) {
            System.out.print("Enter the name for the new table (or enter 0 to cancel): ");
            tableName = scanner.nextLine().trim();

            // Cancelar la operación si el usuario ingresa 0
            if (tableName.equals("0")) {
                System.out.println("Operation cancelled.");
                return;
            }

            // Verificar si el nombre de la tabla contiene solo letras
            if (tableName.matches("[a-zA-Z]+")) {
                validTableName = true;
            } else {
                System.out.println("Invalid table name. The name can only contain letters. Please try again.");
            }
        }
        // Solicitar el nombre del campo ID y validar
        String idFieldName = null;
        boolean validIdFieldName = false;
        while (!validIdFieldName) {
            System.out.print("Enter the name for the ID field (or enter 0 to use default 'id'): ");
            idFieldName = scanner.nextLine().trim();

            // Usar el nombre predeterminado 'id' si el usuario ingresa 0
            if (idFieldName.equals("0")) { idFieldName = "id";
                validIdFieldName = true;
            } else if (idFieldName.matches("[a-zA-Z]+")) { validIdFieldName = true;
            } else { System.out.println("Invalid ID field name. The name can only contain letters. Please try again.");
            }
        }

        // Iniciar la consulta para crear la tabla con el campo 'id' como clave primaria auto incremental
        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE " + tableName + " (" + idFieldName + " INT AUTO_INCREMENT PRIMARY KEY");

        // Bucle para agregar más campos (columnas)
        boolean addMoreFields = true;
        while (addMoreFields) {
            System.out.print("Do you want to add a new column (field)? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("n")) {
                addMoreFields = false; // Finaliza la adición de columnas
            } else if (response.equals("y")) {
                String fieldName = null;
                boolean validFieldName = false;

                // Solicitar el nombre de la nueva columna y validar
                while (!validFieldName) {
                    System.out.print("Enter the column (field) name (or enter 0 to cancel): ");
                    fieldName = scanner.nextLine().trim();

                    // Cancelar la operación si el usuario ingresa 0
                    if (fieldName.equals("0")) {
                        System.out.println("Operation cancelled.");
                        return;
                    }

                    // Verificar si el nombre de la columna contiene solo letras
                    if (fieldName.matches("[a-zA-Z]+")) {
                        validFieldName = true;
                    } else {
                        System.out.println("Invalid column name. The name can only contain letters. Please try again.");
                    }
                }

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

        // scanner.close(); // No cerrar el scanner si se va a reutilizar más tarde
    }

}
