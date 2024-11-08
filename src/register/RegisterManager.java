package register;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class RegisterManager {

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

        // Imprimir encabezados con la alineación
        for (String column : columns) {
            System.out.print(padRight(column, columnWidths.get(column)) + " | ");
        }
        System.out.println();
        System.out.println("-".repeat(columnWidths.values().stream().mapToInt(Integer::intValue).sum() + columns.size() * 3));

        // Imprimir cada fila con alineación
        for (Map<String, String> row : rows) {
            for (String column : columns) {
                String value = row.get(column);
                System.out.print(padRight(value != null ? value : "NULL", columnWidths.get(column)) + " | ");
            }
            System.out.println();
        }

        System.out.println("===== End of Records =====");
    }

    // Método auxiliar para alinear texto a la derecha con un ancho específico
    private static String padRight(String text, int length) {
        return String.format("%-" + length + "s", text);
    }




    public static void CreateRegister(Statement statement, String selectedTable) throws SQLException {
            Scanner scanner = new Scanner(System.in);

            // Obtener columnas y tipos de datos de la tabla seleccionada
            ResultSet columnsResultSet = statement.executeQuery("SHOW COLUMNS FROM " + selectedTable);
            Map<String, String> columnTypes = new LinkedHashMap<>();

            while (columnsResultSet.next()) {
                String columnName = columnsResultSet.getString("Field");
                String columnType = columnsResultSet.getString("Type");
                columnTypes.put(columnName, columnType);
            }

            // Crear el registro pidiendo los valores al usuario
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

            // Generar la consulta SQL para insertar el registro
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

