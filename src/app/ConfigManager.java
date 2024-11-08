package app;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.txt";
    private static String URL;
    private static Map<String, String> userCredentials = new HashMap<>();
    private static String currentUsername;
    private static String currentPassword;

    public static void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                URL = reader.readLine().split("=")[1].trim();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("username=")) {
                        String[] parts = line.split("=");
                        if (parts.length == 4) {
                            String username = parts[1].trim();
                            String password = parts[3].trim();
                            userCredentials.put(username, password);
                        }
                    }
                }
                System.out.println("Configuration loaded successfully.");
            } catch (IOException e) {
                System.out.println("Error loading config: " + e.getMessage());
            }
        } else {
            System.out.println("Config file not found. Please enter new credentials.");
        }
    }

    public static void saveConfig() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
            writer.write("url=" + URL + "\n");
            for (Map.Entry<String, String> entry : userCredentials.entrySet()) {
                writer.write("username=" + entry.getKey() + "=password=" + entry.getValue() + "\n");
            }
            System.out.println("Configuration saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving config: " + e.getMessage());
        }
    }

    public static void loginUser(Scanner scanner) {
        loadConfig();

        if (URL == null) {
            // Solicitar y guardar nuevas credenciales hasta que sean correctas (primera configuración)
            boolean credentialsValid = false;
            while (!credentialsValid) {
                requestNewCredentials(scanner, false);
                credentialsValid = validateConnection();
                if (credentialsValid) {
                    userCredentials.put(currentUsername, currentPassword);
                    saveConfig();
                } else {
                    System.out.println("Connection failed. Please re-enter your credentials.");
                }
            }
        } else {
            while (true) {
                int choice = getUserChoice(scanner);
                if (choice == 1) {
                    if (loginExistingUser(scanner)) {
                        break;
                    }
                } else if (choice == 2) {
                    if (createNewUser(scanner)) {
                        break;
                    }
                }
            }
        }
    }

    private static int getUserChoice(Scanner scanner) {
        int choice = 0;
        while (true) {
            System.out.println("\n=============================================");
            System.out.println("Would you like to:");
            System.out.println("1. Login with an existing user");
            System.out.println("2. Create a new user");
            System.out.print("Select an option: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
                if (choice == 1 || choice == 2) {
                    break;
                }
            } else {
                scanner.nextLine();
            }
            System.out.println("Invalid option. Please enter 1 or 2.");
        }
        return choice;
    }

    private static boolean loginExistingUser(Scanner scanner) {
        System.out.print("Enter username: ");
        currentUsername = scanner.nextLine();

        if (!userCredentials.containsKey(currentUsername)) {
            System.out.println("Username not found. Returning to main menu.");
            return false;
        }

        while (true) {
            System.out.print("Enter password: ");
            String enteredPassword = scanner.nextLine();

            if (userCredentials.get(currentUsername).equals(enteredPassword)) {
                currentPassword = enteredPassword;
                System.out.println("Login successful.");
                return validateConnection();
            } else {
                System.out.println("Incorrect password. Please try again.");
            }
        }
    }

    private static boolean createNewUser(Scanner scanner) {
        while (true) {
            System.out.print("Enter new username: ");
            String newUsername = scanner.nextLine();

            System.out.print("Enter database URL: ");
            String newURL = scanner.nextLine();

            System.out.print("Enter password: ");
            String newPassword = scanner.nextLine();

            if (userCredentials.containsKey(newUsername) && userCredentials.get(newUsername).equals(newPassword) && URL.equals(newURL)) {
                System.out.println("This user already exists with the same credentials. Returning to the main menu.");
                return false;
            }

            if (userCredentials.containsKey(newUsername)) {
                System.out.println("Username already exists but with different credentials. Continuing with the creation of a new user.");
            }

            currentUsername = newUsername;
            URL = newURL;
            currentPassword = newPassword;

            if (validateConnection()) {
                userCredentials.put(currentUsername, currentPassword);
                saveConfig();
                System.out.println("New user created successfully.");
                return true;
            } else {
                System.out.println("Connection failed. Please re-enter all credentials.");
            }
        }
    }
    private static void requestNewCredentials(Scanner scanner, boolean skipUsername) {
        if (!skipUsername) {
            System.out.print("Enter username: ");
            currentUsername = scanner.nextLine();
        }

        System.out.print("Enter database URL: ");
        URL = scanner.nextLine();

        System.out.print("Enter password: ");
        currentPassword = scanner.nextLine();
    }

    private static boolean validateConnection() {
        boolean success = ConnectionManager.initialize(URL, currentUsername, currentPassword);
        if (!success) {
            System.out.println("Error connecting to the database.");
        }
        return success;
    }

    public static String getURL() {
        return URL;
    }

    public static String getUsername() {
        return currentUsername;
    }

    public static String getPassword() {
        return currentPassword;
    }
}





























