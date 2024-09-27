import java.util.ArrayList;

class ClienteTienda {
    private int Id;
    private String nombreClt, correoClt, numTelClt;

    public ClienteTienda() {
    }

    public ClienteTienda(int id, String nombreClt, String correoClt, String numTelClt) {
        Id = id;
        this.nombreClt = nombreClt;
        this.correoClt = correoClt;
        this.numTelClt = numTelClt;
    }

    public int getId() {
        return Id;
    }

    private void setId(int id) {
        Id = id;
    }

    public String getNombreClt() {
        return nombreClt;
    }

    public void setNombreClt(String nombreClt) {
        this.nombreClt = nombreClt;
    }

    public String getCorreoClt() {
        return correoClt;
    }

    public void setCorreoClt(String correoClt) {
        this.correoClt = correoClt;
    }

    public String getNumTelClt() {
        return numTelClt;
    }

    public void setNumTelClt(String numTelClt) {
        this.numTelClt = numTelClt;
    }

    @Override
    public String toString() {
        return "ClienteTienda{" +
                "Id=" + Id +
                ", nombre del cliente='" + nombreClt + '\'' +
                ", correo del cliente='" + correoClt + '\'' +
                ", numero de telefono del cliente='" + numTelClt + '\'' +
                '}';
    }
}

class Tienda {
    private ArrayList<ClienteTienda> clienteTiendas;

    public Tienda() {
        clienteTiendas = new ArrayList<>();
    }

    public void agregarCliente(ClienteTienda clienteTienda) {
        clienteTiendas.add(clienteTienda);
    }
}
class TiendaCRUD {
}

//No se lo que hace estos codigos :P
