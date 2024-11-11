package app;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Set;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.txt";
    private static String URL;
    private static final Map<String, String> userCredentials = new HashMap<>();
    private static String currentUsername;
    private static String currentPassword;
    private static boolean loggedIn = false;
    private static Set<String> authenticatedUsers = new HashSet<>();

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
            boolean validLogin = false;
            while (!validLogin) {
                int choice = getUserChoice(scanner);
                if (choice == 1) {
                    validLogin = loginExistingUser(scanner);
                } else if (choice == 2) {
                    validLogin = createNewUser(scanner);
                } else if (choice == 3) {
                    deleteUser(scanner);
                } else if (choice == 4) {
                    listUsers(scanner);
                }

                if (validLogin) {
                    break;
                }
            }
        }
    }

    private static boolean loginExistingUser(Scanner scanner) {
        int choice = -1;

        while (choice != 0) {
            System.out.println("\nRegistered Users:");
            int count = 1;
            for (String username : userCredentials.keySet()) {
                System.out.println(count + ". " + username);
                count++;
            }

            System.out.print("\nSelect a user by number to log in or press 0 to return: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 0) {
                    return false;
                }

                int index = 1;
                String selectedUser = null;
                for (String username : userCredentials.keySet()) {
                    if (index == choice) {
                        selectedUser = username;
                        break;
                    }
                    index++;
                }

                if (selectedUser != null) {
                    currentUsername = selectedUser;
                    currentPassword = userCredentials.get(currentUsername);

                    System.out.println("Welcome back, " + currentUsername + "!");
                    if (validateConnection()) {
                        System.out.println("Connection established successfully.");
                        loggedIn = true;
                        return true;
                    } else {
                        System.out.println("Connection failed.");
                        return false;
                    }
                } else {
                    System.out.println("Invalid selection. Please choose a valid user.");
                }
            } else {
                System.out.println("Invalid input. Please select a valid user.");
                scanner.nextLine();
            }
        }
        return false;
    }


    private static int getUserChoice(Scanner scanner) {
        int choice = 0;
        while (true) {
            System.out.println("\n=============================================");
            System.out.println("Would you like to:");
            System.out.println("1. List all users and select one to log in");
            System.out.println("2. Create a new user");
            System.out.println("3. Delete a user (you must be logged in)");
            System.out.print("Select an option: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
                if (choice >= 1 && choice <= 3) {
                    break;
                }
            } else {
                scanner.nextLine();
            }
            System.out.println("Invalid option. Please enter a number between 1 and 3.");
        }
        return choice;
    }

    private static void listUsers(Scanner scanner) {
        if (userCredentials.isEmpty()) {
            System.out.println("\nNo registered users found.");
            String response;
            while (true) {
                System.out.print("Would you like to create a new user? (y/n): ");
                response = scanner.nextLine().trim().toLowerCase();
                if (response.equals("y")) {
                    createNewUser(scanner);
                    return;
                } else if (response.equals("n")) {
                    System.out.println("Returning to the main menu.");
                    return;
                } else {
                    System.out.println("Invalid input. Please enter 'y' or 'n'.");
                }
            }
        }

        while (true) {
            System.out.println("\nRegistered Users:");
            int count = 1;
            for (String username : userCredentials.keySet()) {
                System.out.println(count + ". " + username);
                count++;
            }

            System.out.print("\nSelect a user by number to log in or press 0 to return: ");
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 0) {
                    return;
                }

                int index = 1;
                for (String username : userCredentials.keySet()) {
                    if (index == choice) {
                        currentUsername = username;
                        currentPassword = userCredentials.get(username);
                        if (validateConnection()) {
                            System.out.println("Login successful.");
                            return;
                        } else {
                            System.out.println("Failed to connect with selected user. Returning to menu.");
                            currentUsername = null;
                            currentPassword = null;
                        }
                        return;
                    }
                    index++;
                }
                System.out.println("Invalid selection. Please choose a valid user.");
            } else {
                scanner.nextLine();
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static boolean createNewUser(Scanner scanner) {
        while (true) {
            System.out.println("\n===== Create New User =====");
            System.out.println("Enter '0' to return to the main menu at any point.");
            System.out.print("Enter new username: ");
            String newUsername = scanner.nextLine().trim();

            if (newUsername.equals("0")) {
                return false;
            }

            System.out.print("Enter database URL: ");
            String newURL = scanner.nextLine().trim();

            if (newURL.equals("0")) {
                return false;
            }

            System.out.print("Enter password: ");
            String newPassword = scanner.nextLine().trim();

            if (newPassword.equals("0")) {
                return false;
            }

            for (Map.Entry<String, String> entry : userCredentials.entrySet()) {
                if (entry.getKey().equals(newUsername) && entry.getValue().equals(newPassword) && URL.equals(newURL)) {
                    System.out.println("This user already exists with the same credentials. Returning to the main menu.");
                    return false;
                }
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

    public static boolean deleteUser(Scanner scanner) {
        if (userCredentials.isEmpty()) {
            System.out.println("\nNo registered users found.");
            String response;
            while (true) {
                System.out.print("Would you like to create a new user instead? (y/n): ");
                response = scanner.nextLine().trim().toLowerCase();
                if (response.equals("y")) {
                    createNewUser(scanner);
                    return false;
                } else if (response.equals("n")) {
                    System.out.println("Returning to the main menu.");
                    return false;
                } else {
                    System.out.println("Invalid input. Please enter 'y' or 'n'.");
                }
            }
        }

        while (true) {
            System.out.println("\nRegistered Users:");
            int count = 1;
            for (String username : userCredentials.keySet()) {
                System.out.println(count + ". " + username);
                count++;
            }

            System.out.print("\nSelect a user by number to delete or press 0 to return: ");
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice == 0) return false;

                int index = 1;
                String selectedUser = null;
                for (String username : userCredentials.keySet()) {
                    if (index == choice) {
                        selectedUser = username;
                        break;
                    }
                    index++;
                }

                if (selectedUser != null) {
                    String confirmation;
                    while (true) {
                        System.out.print("Are you sure you want to delete the user '" + selectedUser + "'? (y/n): ");
                        confirmation = scanner.nextLine().trim().toLowerCase();
                        if (confirmation.equals("y")) {
                            userCredentials.remove(selectedUser);
                            authenticatedUsers.remove(selectedUser);
                            saveConfig();
                            System.out.println("User '" + selectedUser + "' deleted successfully.");
                            return true;
                        } else if (confirmation.equals("n")) {
                            System.out.println("User deletion canceled.");
                            return false;
                        } else {
                            System.out.println("Invalid input. Please enter 'y' or 'n'.");
                        }
                    }
                } else {
                    System.out.println("Invalid selection. Please choose a valid user.");
                }
            } else {
                scanner.nextLine();
                System.out.println("Invalid input. Please enter a valid number.");
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

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    private static boolean validateConnection() {
        boolean success = ConnectionManager.initialize(URL, currentUsername, currentPassword);
        if (success) {
            loggedIn = true;
        } else {
            System.out.println("Error connecting to the database.");
            loggedIn = false;
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































