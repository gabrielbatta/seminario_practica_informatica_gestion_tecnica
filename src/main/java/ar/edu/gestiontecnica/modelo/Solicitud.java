package ar.edu.gestiontecnica.modelo;

import java.time.LocalDateTime;

// Objeto sencillo para transportar los datos entre las capas.
// Los nombres de cliente/servicio se consultan con JOIN; no se duplican en las tablas.
public class Solicitud {
    private int id;
    private int servicioId;
    private int usuarioRegistroId;
    private LocalDateTime fechaRegistro;
    private String descripcion;
    private String estado;
    private LocalDateTime fechaCierre;
    private String canalCierre;
    private String solucionCierre;
    private String cliente;
    private String domicilio;
    private String servicio;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServicioId() {
        return servicioId;
    }

    public void setServicioId(int servicioId) {
        this.servicioId = servicioId;
    }

    public int getUsuarioRegistroId() {
        return usuarioRegistroId;
    }

    public void setUsuarioRegistroId(int usuarioRegistroId) {
        this.usuarioRegistroId = usuarioRegistroId;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getCanalCierre() {
        return canalCierre;
    }

    public void setCanalCierre(String canalCierre) {
        this.canalCierre = canalCierre;
    }

    public String getSolucionCierre() {
        return solucionCierre;
    }

    public void setSolucionCierre(String solucionCierre) {
        this.solucionCierre = solucionCierre;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }
}
