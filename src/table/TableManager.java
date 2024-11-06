package table;
import register.OperateTables;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class TableManager {

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
