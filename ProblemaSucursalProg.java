class Persona {

    private final String nombre;
    private final String apellido;
    private final int dni;

    public Persona(String nombre, String apellido, int dni) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
    }

    @Override
    public String toString() {
        return "Nombre: " + this.nombre + "\nApellido: " + this.apellido + "\nDNI: " + this.dni;
    }
}


class Empleado extends Persona {

    private final double sueldo;
    private final double cantHoras;

    public Empleado(String nombre, String apellido, int dni, double sueldo, double cantHoras) {
        super(nombre, apellido, dni);
        this.sueldo = sueldo;
        this.cantHoras = cantHoras;
    }

    @Override
    public String toString() {
        return super.toString() + "\nSueldo: " + this.sueldo;
    }
}
