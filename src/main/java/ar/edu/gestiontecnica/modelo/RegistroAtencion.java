package ar.edu.gestiontecnica.modelo;

public abstract class RegistroAtencion {
    private int id;
    private String estado;
    private String cliente;
    private String servicio;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }

    public abstract String getReferencia();
}
