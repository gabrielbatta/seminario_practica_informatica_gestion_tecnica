package ar.edu.gestiontecnica.modelo;

import java.time.LocalDateTime;

// Objeto sencillo para transportar los datos entre las capas.
// Los nombres de cliente/servicio se consultan con JOIN; no se duplican en las tablas.
public class Asistencia extends RegistroAtencion {
    private int solicitudId;
    private Integer tecnicoId;
    private String motivo;
    private LocalDateTime fechaProgramada;
    private String diagnostico;
    private String tareas;
    private String resultado;
    private String detalleResultado;
    private LocalDateTime fechaFinalizacion;
    private String nombreTecnico;

    @Override
    public String getReferencia() {
        return "Asistencia #" + getId() + " · Solicitud #" + solicitudId;
    }

    public int getSolicitudId() {
        return solicitudId;
    }

    public void setSolicitudId(int solicitudId) {
        this.solicitudId = solicitudId;
    }

    public Integer getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(Integer tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTareas() {
        return tareas;
    }

    public void setTareas(String tareas) {
        this.tareas = tareas;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getDetalleResultado() {
        return detalleResultado;
    }

    public void setDetalleResultado(String detalleResultado) {
        this.detalleResultado = detalleResultado;
    }

    public LocalDateTime getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(LocalDateTime fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public String getNombreTecnico() {
        return nombreTecnico;
    }

    public void setNombreTecnico(String nombreTecnico) {
        this.nombreTecnico = nombreTecnico;
    }

}
