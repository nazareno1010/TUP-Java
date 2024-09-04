# Sistema de Gestión de Sucursales

Este proyecto en Java define una estructura de clases para gestionar diferentes tipos de personas en una sucursal. La clase base `Persona` incluye atributos comunes como `nombre`, `edad`, y `dni`. Las clases derivadas `EmpleadoSucursal`, `ClienteSucursal`, y `SeguridadSucursal` extienden `Persona` para incluir características específicas.

- **`EmpleadoSucursal`**: Representa a un empleado con atributos adicionales `sueldo` y `cantidadHoras`, y un método para calcular el sueldo total.
- **`ClienteSucursal`**: Incluye atributos `mayorista` y `nroSocio`, con métodos para acceder y modificar estos valores.
- **`SeguridadSucursal`**: Añade un atributo `sector` para identificar el área de seguridad.

Para más detalles, consulta el código fuente de cada clase.

---