class Persona {
    protected String nombre;
    protected int edad;
    protected String dni;

    public Persona(String nombre, int edad, String dni) {
        this.nombre = nombre;
        this.edad = edad;
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
}

class EmpleadoSucursal extends Persona {
    protected double sueldo;
    protected int cantidadHoras;

    public EmpleadoSucursal(String nombre, int edad, String dni, double sueldo, int cantidadHoras) {
        super(nombre, edad, dni);
        this.sueldo = sueldo;
        this.cantidadHoras = cantidadHoras;
    }

    public double calcularSueldo() {
        return sueldo * cantidadHoras;
    }

    public double getSueldo() {
        return sueldo;
    }

    public void setSueldo(double sueldo) {
        this.sueldo = sueldo;
    }

    public int getCantidadHoras() {
        return cantidadHoras;
    }

    public void setCantidadHoras(int cantidadHoras) {
        this.cantidadHoras = cantidadHoras;
    }
}
