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

class ClienteSucursal extends Persona {
    private boolean mayorista;
    private int nroSocio;

    public ClienteSucursal(String nombre, int edad, String dni, boolean mayorista, int nroSocio) {
        super(nombre, edad, dni);
        this.mayorista = mayorista;
        this.nroSocio = nroSocio;
    }

    public int getNroSocio() {
        return nroSocio;
    }

    public void setMayorista(boolean mayorista) {
        this.mayorista = mayorista;
    }

    public void setNroSocio(int nroSocio) {
        this.nroSocio = nroSocio;
    }

    public boolean isMayorista() {
        return mayorista;
    }

    public String toString() {
        return "\n" + super.toString() + "\nMayorista: " + this.mayorista;
    }
}

class SeguridadSucursal extends Persona {
    private String sector;

    public SeguridadSucursal(String nombre, int edad, String dni, String sector) {
        super(nombre, edad, dni);
        this.sector = sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getSector() {
        return sector;
    }

}

