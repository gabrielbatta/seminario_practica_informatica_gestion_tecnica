package ar.edu.gestiontecnica.servicio;

import ar.edu.gestiontecnica.config.ConexionBD;
import ar.edu.gestiontecnica.dao.*;
import ar.edu.gestiontecnica.modelo.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class AsistenciaServicio {
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private final SolicitudDAO solicitudDAO = new SolicitudDAO();
    private final ActividadDAO actividadDAO = new ActividadDAO();

    public List<Asistencia> listar(Usuario usuario) throws SQLException {
        ReglasAtencion.validarRol(usuario, "COORDINADOR", "TECNICO", "ADMINISTRADOR");
        try (Connection conexion = ConexionBD.abrir()) { return asistenciaDAO.listar(conexion, usuario); }
    }

    public List<Opcion> listarTecnicos(Usuario usuario) throws SQLException {
        ReglasAtencion.validarRol(usuario, "COORDINADOR");
        try (Connection conexion = ConexionBD.abrir()) { return new CatalogoDAO().listarTecnicos(conexion); }
    }

    public void programar(Usuario usuario, int asistenciaId, LocalDateTime fecha) throws SQLException {
        // CU06: se permite cargar fechas pasadas para documentar visitas ya realizadas.
        ReglasAtencion.validarRol(usuario, "COORDINADOR");
        if (fecha == null) { throw new IllegalArgumentException("Completar la fecha y hora de la visita."); }
        try (Connection conexion = ConexionBD.abrir()) {
            conexion.setAutoCommit(false);
            try {
                Asistencia asistencia = cargarParaModificar(conexion, asistenciaId);
                validarSinIntervencion(asistencia);
                LocalDateTime fechaAnterior = asistencia.getFechaProgramada();
                asistencia.setFechaProgramada(fecha);
                asistencia.setEstado(ReglasAtencion.calcularEstadoProgramacion(asistencia.getTecnicoId(), fecha));
                asistenciaDAO.guardarProgramacion(conexion, asistencia);
                actividadDAO.registrar(conexion, asistencia.getSolicitudId(), usuario.getId(), "PROGRAMAR_ASISTENCIA",
                        "Asistencia #" + asistenciaId + ". Fecha: " + fechaAnterior + " -> " + fecha + ". Estado: " + asistencia.getEstado());
                conexion.commit();
            } catch (SQLException | RuntimeException error) {
                conexion.rollback();
                throw error;
            }
        }
    }

    public void asignarTecnico(Usuario usuario, int asistenciaId, int tecnicoId) throws SQLException {
        // CU07: no basta con que el ID exista; debe ser un técnico activo.
        ReglasAtencion.validarRol(usuario, "COORDINADOR");
        try (Connection conexion = ConexionBD.abrir()) {
            conexion.setAutoCommit(false);
            try {
                Asistencia asistencia = cargarParaModificar(conexion, asistenciaId);
                validarSinIntervencion(asistencia);
                Usuario tecnico = new UsuarioDAO().buscarPorId(conexion, tecnicoId);
                if (tecnico == null || !tecnico.getActivo() || !"TECNICO".equals(tecnico.getRol())) {
                    throw new IllegalArgumentException("Seleccionar un técnico activo.");
                }
                Integer anterior = asistencia.getTecnicoId();
                asistencia.setTecnicoId(tecnicoId);
                asistencia.setEstado(ReglasAtencion.calcularEstadoProgramacion(tecnicoId, asistencia.getFechaProgramada()));
                asistenciaDAO.guardarProgramacion(conexion, asistencia);
                actividadDAO.registrar(conexion, asistencia.getSolicitudId(), usuario.getId(), "ASIGNAR_TECNICO",
                        "Asistencia #" + asistenciaId + ". Técnico: " + anterior + " -> " + tecnicoId + ". Estado: " + asistencia.getEstado());
                conexion.commit();
            } catch (SQLException | RuntimeException error) {
                conexion.rollback();
                throw error;
            }
        }
    }

    public void registrarDiagnostico(Usuario usuario, int asistenciaId, String diagnostico) throws SQLException {
        // CU09: guardar el diagnóstico sin obligar a finalizar la visita en el mismo momento.
        ReglasAtencion.validarRol(usuario, "TECNICO");
        String texto = ReglasAtencion.textoObligatorio(diagnostico, "el diagnóstico", 2000);
        try (Connection conexion = ConexionBD.abrir()) {
            conexion.setAutoCommit(false);
            try {
                Asistencia asistencia = cargarParaModificar(conexion, asistenciaId);
                ReglasAtencion.validarTecnicoAsignado(usuario, asistencia);
                asistenciaDAO.guardarDiagnostico(conexion, asistenciaId, texto);
                actividadDAO.registrar(conexion, asistencia.getSolicitudId(), usuario.getId(), "REGISTRAR_DIAGNOSTICO",
                        "Se registró o actualizó el diagnóstico de la asistencia #" + asistenciaId + ".");
                conexion.commit();
            } catch (SQLException | RuntimeException error) {
                conexion.rollback();
                throw error;
            }
        }
    }

    public void registrarTareas(Usuario usuario, int asistenciaId, String tareas) throws SQLException {
        // CU10: las tareas se guardan como una operación explícita.
        ReglasAtencion.validarRol(usuario, "TECNICO");
        String texto = ReglasAtencion.textoObligatorio(tareas, "las tareas realizadas", 2000);
        try (Connection conexion = ConexionBD.abrir()) {
            conexion.setAutoCommit(false);
            try {
                Asistencia asistencia = cargarParaModificar(conexion, asistenciaId);
                ReglasAtencion.validarTecnicoAsignado(usuario, asistencia);
                asistenciaDAO.guardarTareas(conexion, asistenciaId, texto);
                actividadDAO.registrar(conexion, asistencia.getSolicitudId(), usuario.getId(), "REGISTRAR_TAREAS",
                        "Se registraron o actualizaron las tareas de la asistencia #" + asistenciaId + ".");
                conexion.commit();
            } catch (SQLException | RuntimeException error) {
                conexion.rollback();
                throw error;
            }
        }
    }

    public void finalizar(Usuario usuario, int asistenciaId, String resultado, String detalle) throws SQLException {
        // CU11 / RF17: terminar una visita no siempre cierra la solicitud.
        ReglasAtencion.validarRol(usuario, "TECNICO");
        String detalleValidado = ReglasAtencion.textoObligatorio(detalle, "el detalle del resultado", 2000);
        try (Connection conexion = ConexionBD.abrir()) {
            conexion.setAutoCommit(false);
            try {
                Asistencia asistencia = cargarParaModificar(conexion, asistenciaId);
                ReglasAtencion.validarFinalizacion(usuario, asistencia, resultado, detalleValidado, LocalDateTime.now());
                asistenciaDAO.finalizar(conexion, asistenciaId, resultado, detalleValidado);
                String nuevoEstado;
                if ("RESUELTO".equals(resultado)) {
                    solicitudDAO.cerrar(conexion, asistencia.getSolicitudId(), "DOMICILIARIA", detalleValidado);
                    nuevoEstado = "CERRADA";
                } else {
                    solicitudDAO.cambiarEstado(conexion, asistencia.getSolicitudId(), "PENDIENTE_CONTINUIDAD");
                    nuevoEstado = "PENDIENTE_CONTINUIDAD";
                }
                actividadDAO.registrar(conexion, asistencia.getSolicitudId(), usuario.getId(), "FINALIZAR_ASISTENCIA",
                        "Asistencia #" + asistenciaId + ": " + resultado + ". Solicitud -> " + nuevoEstado + ".");
                conexion.commit();
            } catch (SQLException | RuntimeException error) {
                // Si falla cualquier escritura, no queda una visita finalizada con una solicitud desactualizada.
                conexion.rollback();
                throw error;
            }
        }
    }

    private Asistencia cargarParaModificar(Connection conexion, int asistenciaId) throws SQLException {
        // Orden de bloqueo común: primero la solicitud y después la asistencia.
        Asistencia referencia = asistenciaDAO.buscar(conexion, asistenciaId, false);
        if (referencia == null) { throw new IllegalArgumentException("La asistencia no existe."); }
        Solicitud solicitud = solicitudDAO.buscar(conexion, referencia.getSolicitudId(), true);
        ReglasAtencion.validarSolicitudAbierta(solicitud);
        Asistencia asistencia = asistenciaDAO.buscar(conexion, asistenciaId, true);
        ReglasAtencion.validarAsistenciaEditable(asistencia);
        return asistencia;
    }

    private void validarSinIntervencion(Asistencia asistencia) {
        if (asistencia.getDiagnostico() != null || asistencia.getTareas() != null) {
            throw new IllegalArgumentException("No se puede reprogramar o reasignar una visita con intervención registrada.");
        }
    }
}
