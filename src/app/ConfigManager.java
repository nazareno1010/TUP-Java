package app;

import java.io.*;
import java.util.Scanner;

public class ConfigManager {

    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static boolean connectionValidated = false;

    public static void loadConfig() {
        File configFile = new File("config.txt");
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                URL = reader.readLine().split("=")[1].trim();
                USER = reader.readLine().split("=")[1].trim();
                PASSWORD = reader.readLine().split("=")[1].trim();
            } catch (IOException e) {
                System.out.println("Error loading config: " + e.getMessage());
            }
        }
    }

    public static void saveConfig() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("config.txt"))) {
            writer.write("url=" + URL + "\n");
            writer.write("username=" + USER + "\n");
            writer.write("password=" + PASSWORD + "\n");
        } catch (IOException e) {
            System.out.println("Error saving config: " + e.getMessage());
        }
    }

    public static void getUserInput(Scanner scanner) {
        boolean connectionValid = false;

        while (!connectionValid) {
            System.out.print("Enter database URL: ");
            URL = scanner.nextLine();

            System.out.print("Enter database username: ");
            USER = scanner.nextLine();

            System.out.print("Enter database password: ");
            PASSWORD = scanner.nextLine();

            connectionValid = validateConnection();


            if (connectionValid) {
                System.out.println("Connection successful! Saving credentials...");
                System.out.println("Configuration saved successfully.");
                saveConfig();
                break;
            } else {
                System.out.println("Invalid credentials. Please check your details and try again.");
            }
        }
    }

    public static String getURL() {
        return URL;
    }

    public static String getUser() {
        return USER;
    }

    public static String getPassword() {
        return PASSWORD;
    }

    public static boolean validateConnection() {
        if (connectionValidated) {
            return true;
        }

        System.out.println("\n=============================================");
        System.out.println("Attempting to validate connection...");

        connectionValidated = ConnectionManager.initialize(URL, USER, PASSWORD);

        if (!connectionValidated) {
            System.out.println("Connection failed. Please verify your credentials.");
        }
        return connectionValidated;
    }
}










