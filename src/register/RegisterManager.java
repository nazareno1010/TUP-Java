package register;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class RegisterManager {

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

