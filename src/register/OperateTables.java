package register;

import table.TableManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OperateTables {


    public static void interfaceRegister(Statement statement, String selectedTable) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int option;
        do {
            System.out.println("\n===== DBSM - Database Management System =====");
            System.out.println("1. Create Register");
            System.out.println("2. Read Register");
            System.out.println("3. Search Register With ID");
            System.out.println("4. Update Register");
            System.out.println("5. Delete Register");
//            System.out.println("6. Convert to .CSV");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    RegisterManager.searchRegister(statement, selectedTable);
                    break;
                case 2:
                    RegisterManager.CreateRegister(statement, selectedTable);
                    break;
                case 3:
                    RegisterManager.readRegister(statement, selectedTable);
                    break;
                case 4:
                  RegisterManager.UpdateRegister(statement, selectedTable);
                    break;
                case 5:
                  RegisterManager.DeleteRegister(statement, selectedTable);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid option. Please, try again.");
                    break;
            }

        } while (option != 0);
    }

}
