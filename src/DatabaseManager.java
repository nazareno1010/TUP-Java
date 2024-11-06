import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DatabaseManager {
    private static final Set<String> EXCLUDED_DATABASES = Main.EXCLUDED_DATABASES;

    public static void createDatabase(Statement statement, Scanner scanner) throws SQLException {
        System.out.print("Enter the name of the new database: ");
        String dbName = scanner.nextLine();
        statement.executeUpdate("CREATE DATABASE " + dbName);
        System.out.println("Database '" + dbName + "' created successfully.");
    }

    public static void deleteDatabase(Statement statement, Scanner scanner) throws SQLException {
        String sql = "SHOW DATABASES";
        ResultSet resultSet = statement.executeQuery(sql);
        List<String> databases = new ArrayList<>();

        System.out.println("\n=============================================");
        System.out.println("Databases on the server:");
        int count = 1;
        while (resultSet.next()) {
            String dbName = resultSet.getString(1);
            if (!EXCLUDED_DATABASES.contains(dbName)) {
                databases.add(dbName);
                System.out.println(count + ". " + dbName);
                count++;
            }
        }

        if (databases.isEmpty()) {
            System.out.println("No databases available for deletion.");
            return;
        }

        System.out.print("Enter the number of the database you want to delete: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > databases.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        String selectedDatabase = databases.get(choice - 1);

        System.out.print("Are you sure you want to delete the database '" + selectedDatabase + "'? (y/n): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("y")) {
            statement.executeUpdate("DROP DATABASE " + selectedDatabase);
            System.out.println("Database '" + selectedDatabase + "' deleted successfully.");
        } else {
            System.out.println("Operation canceled. The database was not deleted.");
        }
    }
}
