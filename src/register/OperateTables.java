package register;

import table.TableManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OperateTables {

    public static void showTables(Statement statement, String Database) throws SQLException {
        // Cambiar a la base de datos seleccionada
        statement.execute("USE " + Database);

        // Ejecutar el comando para mostrar las tablas
        ResultSet resultSet = statement.executeQuery("SHOW TABLES");
        System.out.println("\n=============================================");
        System.out.println("Tables in the database: " + Database);

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

        if (selectedIndex == 0) {
            System.out.println("Returning to the previous menu...");
            return;  // Finaliza la ejecución de showTables
        }

        if (selectedIndex < 1 || selectedIndex > tables.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        String selectedTable = tables.get(selectedIndex - 1);
        System.out.println("You selected: " + selectedTable);

        // Aquí puedes agregar la lógica para realizar operaciones en la tabla seleccionada
        // Por ejemplo, llamar a una función específica de la tabla
        TableManager.createTable(statement, selectedTable);
    }

    public static void interfaceRegister(Statement statement, String selectedTable) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int option;
        do {
            System.out.println("\n===== DBSM - Database Management System =====");
            System.out.println("1. Create Register");
            System.out.println("2. Read Register");
            System.out.println("3. Update Register");
            System.out.println("4. Delete Register");
            System.out.println("5. Convert to .CSV");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
//                case 1:
//                    RegisterManager.CreateRegister(statement, selectedTable);
//                    break;
//                case 2:
//                  RegisterManager.ReadRegister(statement, selectedTable);
//                    break;
//                case 3:
//                  RegisterManager.UpdateRegister(statement, selectedTable);
//                    break;
//                case 4:
//                  RegisterManager.DeleteRegister(statement, selectedTable);
//                    break;
//                case 5:
//                  RegisterManager.TransportCsv(statement, selectedTable);
//                    break;
                case 1:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option. Please, try again.");
                    break;
            }

        } while (option != 1);
    }

}
