import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OperateDatabases {
    private static final Set<String> EXCLUDED_DATABASES = Main.EXCLUDED_DATABASES;

    public static void showDatabases(Statement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SHOW DATABASES");
        System.out.println("\n=============================================");
        System.out.println("Databases on the server:");
        
        int count = 1;
        List<String> databases = new ArrayList<>();

        while (resultSet.next()) {
            String dbName = resultSet.getString(1);
            if (!EXCLUDED_DATABASES.contains(dbName)) {
                System.out.println(count + ". " + dbName);
                databases.add(dbName);
                count++;
            }
        }

        if (databases.isEmpty()) {
            System.out.println("No databases available.");
            return;
        }

        // Solicitar selección de base de datos
        Scanner scanner = new Scanner(System.in);
        System.out.print("Select a database by number: ");
        int selectedIndex = scanner.nextInt();

        if (selectedIndex < 1 || selectedIndex > databases.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        String selectedDatabase = databases.get(selectedIndex - 1);
        System.out.println("You selected: " + selectedDatabase);

        // Cambiar a la base de datos seleccionada
        statement.execute("USE " + selectedDatabase);

        // Llamar a la función para crear una tabla
        createTable(statement, selectedDatabase);
    }

    public static void createTable(Statement statement, String databaseName) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Solicitar nombre de la tabla
        System.out.print("Enter the name of the table to create: ");
        String tableName = scanner.nextLine();

        // Solicitar definición de columnas
        System.out.print("Enter the column definitions (e.g., id INT, name VARCHAR(50)): ");
        String columnDefinitions = scanner.nextLine();

        // Construir la sentencia SQL para crear la tabla
        String sql = "CREATE TABLE " + tableName + " (" + columnDefinitions + ")";

        // Ejecutar la creación de la tabla
        try {
            statement.executeUpdate(sql);
            System.out.println("Table '" + tableName + "' created successfully in database '" + databaseName + "'.");
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }
}
