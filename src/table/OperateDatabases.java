package table;

import app.Main;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static table.TableManager.showTables;

public class OperateDatabases {
    // Ya no necesitamos la constante EXCLUDED_DATABASES aquí
    // private static final Set<String> EXCLUDED_DATABASES = Main.EXCLUDED_DATABASES;

    // Ahora el método showDatabases acepta dos parámetros
    public static void showDatabases(Statement statement, Set<String> excludedDatabases) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SHOW DATABASES");
        System.out.println("\n=============================================");
        System.out.println("Databases on the server:");

        int count = 1;
        List<String> databases = new ArrayList<>();

        // Recoger las bases de datos disponibles
        while (resultSet.next()) {
            String dbName = resultSet.getString(1);
            if (!excludedDatabases.contains(dbName)) {
                System.out.println(count + ". " + dbName);
                databases.add(dbName);
                count++;
            }
        }

        // Si no hay bases de datos disponibles, informamos y retornamos
        if (databases.isEmpty()) {
            System.out.println("No databases available.");
            return;
        }

        // Mostrar la opción para salir
        System.out.println("0. Back");

        // Solicitar al usuario una opción
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Select a database by number (or enter 0 to go back): ");
            if (scanner.hasNextInt()) {
                int selectedIndex = scanner.nextInt(); // Variable definida dentro del ciclo
                if (selectedIndex == 0) {
                    System.out.println("Returning to the main menu...");
                    return; // Salir de la función showDatabases y regresar al menú principal
                }
                if (selectedIndex >= 1 && selectedIndex <= databases.size()) {
                    // Opción válida, salimos del bucle
                    String selectedDatabase = databases.get(selectedIndex - 1);
                    System.out.println("\nYou selected: " + selectedDatabase);

                    // Cambiar a la base de datos seleccionada
                    statement.execute("USE " + selectedDatabase);

                    // Llamar a la función para gestionar las tablas
                    interfaceTable(statement, selectedDatabase);
                    break; // Salir después de la selección válida
                } else {
                    System.out.println("Invalid selection. Please select a valid number.");
                }
            } else {
                // Capturamos entradas no válidas
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next(); // Limpiar el buffer del scanner
            }
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
                    // Mostrar las tablas en la base de datos seleccionada
                    showTables(statement, selectedDatabase);
                    break;
                case 2:
                    // Crear una nueva tabla en la base de datos seleccionada
                    TableManager.createTable(statement, selectedDatabase);
                    break;
                case 3:
                    // Eliminar una tabla de la base de datos seleccionada
                    TableManager.deleteTable(statement, selectedDatabase, scanner);
                    break;  // Añadido el `break` para que no pase al siguiente caso
                case 0:
                    // Regresar al menú de bases de datos
                    System.out.println("Returning to database selection...");
                    showDatabases(statement, Main.EXCLUDED_DATABASES); // Ahora pasamos el conjunto de bases excluidas
                    return; // Salir del ciclo y regresar a la pantalla anterior
                default:
                    System.out.println("Invalid option. Please, try again.");
                    break;
            }

        } while (true);
    }
}
