package register;

import java.sql.*;
import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class RegisterManager {

    //Metodo para reducir la repetecion de codigo en searchRegister y showRegisters
    public static void printTableHeaders(List<String> columns, Map<String, Integer> columnWidths) {
        // Imprimir encabezados con la alineación
        for (String column : columns) {
            System.out.print(padRight(column, columnWidths.get(column)) + " | ");
        }
        System.out.println();
        System.out.println("-".repeat(columnWidths.values().stream().mapToInt(Integer::intValue).sum() + columns.size() * 3));
    }

    //Metodo para reducir la repetecion de codigo en searchRegister y showRegisters
    public static void printTableRows(List<Map<String, String>> rows, List<String> columns, Map<String, Integer> columnWidths) {
        // Imprimir cada fila con alineación
        for (Map<String, String> row : rows) {
            for (String column : columns) {
                String value = row.get(column);
                System.out.print(padRight(value != null ? value : "NULL", columnWidths.get(column)) + " | ");
            }
            System.out.println();
        }
    }

    public static void searchRegister(Statement statement, String selectedTable) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Solicitar el ID del registro a buscar
        System.out.print("Ingrese el ID del registro que desea buscar: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea

        // Crear la consulta SQL para buscar el registro por su ID
        String query = "SELECT * FROM " + selectedTable + " WHERE id = " + id;

        // Obtener las columnas antes de ejecutar la consulta del registro
        List<String> columns = new ArrayList<>();
        Map<String, Integer> columnWidths = new HashMap<>();
        ResultSet columnsResultSet = statement.executeQuery("SHOW COLUMNS FROM " + selectedTable);

        while (columnsResultSet.next()) {
            String columnName = columnsResultSet.getString("Field");
            columns.add(columnName);
            columnWidths.put(columnName, columnName.length()); // Inicializar ancho mínimo como el del nombre de la columna
        }

        // Ejecutar la consulta para obtener el registro
        ResultSet resultSet = statement.executeQuery(query);

        // Verificar si el registro existe
        if (resultSet.next()) {
            System.out.println("\nRegistro encontrado:");

            // Obtener los valores del registro
            Map<String, String> row = new HashMap<>();
            for (String column : columns) {
                String value = resultSet.getString(column);
                row.put(column, value);

                // Actualizar el ancho máximo de cada columna
                if (value != null && value.length() > columnWidths.get(column)) {
                    columnWidths.put(column, value.length());
                }
            }

            // Imprimir encabezados
            printTableHeaders(columns, columnWidths);

            // Imprimir el valor del registro encontrado
            printTableRows(Collections.singletonList(row), columns, columnWidths);

        } else {
            System.out.println("No se encontró ningún registro con el ID proporcionado.");
        }
    }


    // Método auxiliar para alinear el texto a la derecha
    public static String padRight(String text, int length) {
        return String.format("%-" + length + "s", text);
    }

    public static void readRegister(Statement statement, String selectedTable) throws SQLException {
        System.out.println("\n===== Viewing Records from Table: " + selectedTable + " =====");

        // Obtener columnas y calcular anchos máximos
        ResultSet columnsResultSet = statement.executeQuery("SHOW COLUMNS FROM " + selectedTable);
        List<String> columns = new ArrayList<>();
        Map<String, Integer> columnWidths = new HashMap<>();

        while (columnsResultSet.next()) {
            String columnName = columnsResultSet.getString("Field");
            columns.add(columnName);
            columnWidths.put(columnName, columnName.length()); // Inicializar ancho mínimo como el del nombre de la columna
        }

        // Obtener los registros para calcular el ancho máximo de cada columna
        String query = "SELECT * FROM " + selectedTable;
        ResultSet resultSet = statement.executeQuery(query);
        List<Map<String, String>> rows = new ArrayList<>();

        while (resultSet.next()) {
            Map<String, String> row = new HashMap<>();
            for (String column : columns) {
                String value = resultSet.getString(column);
                row.put(column, value);

                // Actualizar el ancho máximo de cada columna
                if (value != null && value.length() > columnWidths.get(column)) {
                    columnWidths.put(column, value.length());
                }
            }
            rows.add(row);
        }

        // Imprimir encabezados con la alineación usando el método printTableHeaders
        printTableHeaders(columns, columnWidths);

        // Imprimir cada fila con alineación usando el método printTableRows
        printTableRows(rows, columns, columnWidths);

        System.out.println("\n===== End of Records =====");
    }


    public static void CreateRegister(Statement statement, String selectedTable) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Obtener columnas y tipos de datos de la tabla seleccionada
        ResultSet columnsResultSet = statement.executeQuery("SHOW COLUMNS FROM " + selectedTable);
        Map<String, String> columnTypes = new LinkedHashMap<>();

        while (columnsResultSet.next()) {
            String columnName = columnsResultSet.getString("Field");
            String columnType = columnsResultSet.getString("Type");

            // Ignorar la columna `id` ya que es autoincremental y no debe ser modificada manualmente
            if (!columnName.equalsIgnoreCase("id")) {
                columnTypes.put(columnName, columnType);
            }
        }

        // Crear el registro pidiendo los valores al usuario solo para las columnas distintas de `id`
        Map<String, String> values = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : columnTypes.entrySet()) {
            String columnName = entry.getKey();
            String columnType = entry.getValue();

            System.out.print("Ingrese el valor para la columna '" + columnName + "' (" + columnType + "): ");
            String value = scanner.nextLine();

            // Validar el tipo de dato
            if (!isValidType(value, columnType)) {
                System.out.println("Error: tipo de dato no válido para la columna '" + columnName + "'. Solo admite " + columnType + ".");
                return; // Salir si hay un error de tipo de dato
            }

            values.put(columnName, value);
        }

        // Generar la consulta SQL para insertar el registro sin incluir la columna `id`
        StringBuilder query = new StringBuilder("INSERT INTO " + selectedTable + " (");
        StringBuilder valuesPart = new StringBuilder(" VALUES (");

        for (Map.Entry<String, String> entry : values.entrySet()) {
            query.append(entry.getKey()).append(", ");
            valuesPart.append("'").append(entry.getValue()).append("', ");
        }

        query.setLength(query.length() - 2); // Eliminar la última coma
        valuesPart.setLength(valuesPart.length() - 2); // Eliminar la última coma
        query.append(")").append(valuesPart).append(")");

        // Ejecutar la consulta
        statement.executeUpdate(query.toString());
        System.out.println("Registro creado exitosamente.");
    }

    public static void DeleteRegister(Statement statement, String selectedTable) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Verificar si existen registros en la tabla
        ResultSet countResultSet = statement.executeQuery("SELECT COUNT(*) AS count FROM " + selectedTable);
        if (countResultSet.next() && countResultSet.getInt("count") == 0) {
            System.out.println("No records found in the table. Cannot proceed with update.");
            return;
        }

        // Solicitar el ID del registro a eliminar
        System.out.print("Enter the ID of the record you want to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea

        // Confirmar eliminación
        System.out.print("Are you sure you want to delete the record with ID " + id + "? (y/n): ");
        String confirmation = scanner.nextLine();

        // Si la confirmación es "yes", proceder a eliminar
        if (confirmation.equalsIgnoreCase("y")) {
            String query = "DELETE FROM " + selectedTable + " WHERE id = " + id;

            int rowsAffected = statement.executeUpdate(query);

            if (rowsAffected > 0) {
                System.out.println("Record deleted successfully.");
            } else {
                System.out.println("No record found with the provided ID.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }


    public static void UpdateRegister(Statement statement, String selectedTable) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Verificar si existen registros en la tabla
        ResultSet countResultSet = statement.executeQuery("SELECT COUNT(*) AS count FROM " + selectedTable);
        if (countResultSet.next() && countResultSet.getInt("count") == 0) {
            System.out.println("No records found in the table. Cannot proceed with update.");
            return;
        }

        // Solicitar el ID del registro a actualizar
        System.out.print("Enter the ID of the record you want to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consumir la nueva línea

        // Obtener las columnas y tipos de datos de la tabla
        ResultSet columnsResultSet = statement.executeQuery("SHOW COLUMNS FROM " + selectedTable);
        Map<String, String> columnTypes = new LinkedHashMap<>();

        while (columnsResultSet.next()) {
            String columnName = columnsResultSet.getString("Field");
            String columnType = columnsResultSet.getString("Type");

            // Ignorar la columna `id` ya que no debe ser modificada
            if (!columnName.equalsIgnoreCase("id")) {
                columnTypes.put(columnName, columnType);
            }
        }

        // Crear la parte de la consulta para actualizar
        StringBuilder updateQuery = new StringBuilder("UPDATE " + selectedTable + " SET ");
        boolean hasChanges = false;

        // Pedir al usuario los nuevos valores para cada columna
        for (Map.Entry<String, String> entry : columnTypes.entrySet()) {
            String columnName = entry.getKey();
            String columnType = entry.getValue();

            System.out.print("Enter the new value for '" + columnName + "' (" + columnType + ") or press Enter to keep the current value: ");
            String newValue = scanner.nextLine();

            // Si el usuario ingresó un valor, agregarlo a la consulta
            if (!newValue.isEmpty()) {
                if (!isValidType(newValue, columnType)) {
                    System.out.println("Error: Invalid data type for column '" + columnName + "'. Expected type: " + columnType + ".");
                    return; // Salir si el tipo de dato es incorrecto
                }

                updateQuery.append(columnName).append(" = '").append(newValue).append("', ");
                hasChanges = true;
            }
        }

        // Verificar si se hicieron cambios
        if (hasChanges) {
            // Eliminar la última coma y espacio
            updateQuery.setLength(updateQuery.length() - 2);
            updateQuery.append(" WHERE id = ").append(id);

            // Ejecutar la consulta de actualización
            int rowsAffected = statement.executeUpdate(updateQuery.toString());

            if (rowsAffected > 0) {
                System.out.println("Record updated successfully.");
            } else {
                System.out.println("No record found with the provided ID.");
            }
        } else {
            System.out.println("No changes made. Update cancelled.");
        }
    }

    // Método para validar el tipo de dato ingresado
    private static boolean isValidType(String value, String columnType) {
        try {
            if (columnType.startsWith("int")) {
                Integer.parseInt(value);
            } else if (columnType.startsWith("double") || columnType.startsWith("float")) {
                Double.parseDouble(value);
            } else if (columnType.startsWith("varchar") || columnType.startsWith("text")) {
                return true; // Texto siempre es válido
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

