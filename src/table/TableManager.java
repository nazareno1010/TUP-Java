package table;
import register.OperateTables;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TableManager {

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

        // Bucle para agregar más campos
        boolean addMoreFields = true;
        while (addMoreFields) {
            System.out.print("Do you want to add a new field? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("no")) {
                addMoreFields = false; // Finaliza la adición de campos
                OperateTables.showTables(statement, selectedDatabase);
            } else if (response.equals("yes")) {
                // Solicitar el nombre del nuevo campo
                System.out.print("Enter the field name: ");
                String fieldName = scanner.nextLine().trim();

                // Solicitar el tipo de datos del campo
                System.out.print("Select the data type (INT, VARCHAR(255), DOUBLE, etc.): ");
                String dataType = scanner.nextLine().trim();

                // Agregar el nuevo campo a la consulta
                createTableQuery.append(", ").append(fieldName).append(" ").append(dataType);
            } else {
                System.out.println("Invalid response. Please type 'yes' or 'no'.");
            }
        }

        // Completar la consulta de creación de tabla
        createTableQuery.append(");");

        // Ejecutar la consulta para crear la tabla
        statement.executeUpdate(createTableQuery.toString());
        System.out.println("Table " + tableName + " created successfully in database " + selectedDatabase + ".");

        scanner.close(); // Cerrar Scanner para liberar recursos

    }
}
