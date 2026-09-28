package ar.edu.gestiontecnica.servicio;

import ar.edu.gestiontecnica.config.ConexionBD;
import ar.edu.gestiontecnica.dao.*;
import ar.edu.gestiontecnica.modelo.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SolicitudServicio {
    private final SolicitudDAO solicitudDAO = new SolicitudDAO();
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private final AtencionRemotaDAO remotaDAO = new AtencionRemotaDAO();
    private final ActividadDAO actividadDAO = new ActividadDAO();
    private final CatalogoDAO catalogoDAO = new CatalogoDAO();

    public int registrar(Usuario usuario, int servicioId, String descripcion) throws SQLException {
        // CU01: 1. Autorizar y validar datos antes de abrir la transacción.
        ReglasAtencion.validarRol(usuario, "ATENCION");
        String problema = ReglasAtencion.textoObligatorio(descripcion, "el inconveniente", 1000);
        try (Connection conexion = ConexionBD.abrir()) {
            conexion.setAutoCommit(false);
            try {
                // 2. Verificar que el servicio y sus datos de contacto existan.
                if (!catalogoDAO.servicioValido(conexion, servicioId)) {
                    throw new IllegalArgumentException("El servicio no existe, está inactivo o le faltan datos de contacto.");
                }
                // 3. Guardar solicitud y actividad como una sola operación.
                int id = solicitudDAO.insertar(conexion, servicioId, usuario.getId(), problema);
                actividadDAO.registrar(conexion, id, usuario.getId(), "REGISTRAR_SOLICITUD", "Estado inicial: REGISTRADA.");
                conexion.commit();
                return id;
            } catch (SQLException | RuntimeException error) {
                conexion.rollback();
                throw error;
            }
        }
    }

    public void registrarAtencionRemota(Usuario usuario, int solicitudId, String diagnostico,
            String acciones, String resultado, String solucion) throws SQLException {
        // CU03 y CU04 comparten el formulario, pero el resultado determina el cierre.
        ReglasAtencion.validarRol(usuario, "SOPORTE");
        String diagnosticoValidado = ReglasAtencion.textoObligatorio(diagnostico, "el diagnóstico", 2000);
        String accionesValidadas = ReglasAtencion.textoObligatorio(acciones, "las acciones", 2000);
        ReglasAtencion.validarResultado(resultado);
        String solucionValidada = null;
        if ("RESUELTO".equals(resultado)) {
            solucionValidada = ReglasAtencion.textoObligatorio(solucion, "la solución aplicada", 2000);
        }
        try (Connection conexion = ConexionBD.abrir()) {
            conexion.setAutoCommit(false);
            try {
                // Bloquear primero la solicitud: dos operaciones no deciden sobre el mismo estado viejo.
                Solicitud solicitud = solicitudDAO.buscar(conexion, solicitudId, true);
                boolean visitaActiva = asistenciaDAO.existeActiva(conexion, solicitudId);
                ReglasAtencion.validarAtencionRemota(solicitud, visitaActiva);
                remotaDAO.insertar(conexion, solicitudId, usuario.getId(), diagnosticoValidado,
                        accionesValidadas, resultado, solucionValidada);
                if ("RESUELTO".equals(resultado)) {
                    solicitudDAO.cerrar(conexion, solicitudId, "REMOTA", solucionValidada);
                    actividadDAO.registrar(conexion, solicitudId, usuario.getId(), "CERRAR_REMOTAMENTE",
                            solicitud.getEstado() + " -> CERRADA. Resolución remota documentada.");
                } else {
                    solicitudDAO.cambiarEstado(conexion, solicitudId, "EN_ATENCION_REMOTA");
                    actividadDAO.registrar(conexion, solicitudId, usuario.getId(), "REGISTRAR_ATENCION_REMOTA",
                            solicitud.getEstado() + " -> EN_ATENCION_REMOTA. El problema sigue pendiente.");
                }
                conexion.commit();
            } catch (SQLException | RuntimeException error) {
                conexion.rollback();
                throw error;
            }
        }
    }

    public int derivar(Usuario usuario, int solicitudId, String motivo) throws SQLException {
        // CU05: la derivación no exige que haya existido un intento remoto.
        ReglasAtencion.validarRol(usuario, "SOPORTE");
        String motivoValidado = ReglasAtencion.textoObligatorio(motivo, "el motivo de la derivación", 1000);
        try (Connection conexion = ConexionBD.abrir()) {
            conexion.setAutoCommit(false);
            try {
                Solicitud solicitud = solicitudDAO.buscar(conexion, solicitudId, true);
                boolean visitaActiva = asistenciaDAO.existeActiva(conexion, solicitudId);
                ReglasAtencion.validarDerivacion(solicitud, visitaActiva);
                if (!catalogoDAO.servicioValido(conexion, solicitud.getServicioId())) {
                    throw new IllegalArgumentException("Revisar el domicilio, el contacto y el servicio antes de derivar.");
                }
                int asistenciaId = asistenciaDAO.insertar(conexion, solicitudId, motivoValidado);
                solicitudDAO.cambiarEstado(conexion, solicitudId, "DERIVADA");
                actividadDAO.registrar(conexion, solicitudId, usuario.getId(), "DERIVAR_SOLICITUD",
                        solicitud.getEstado() + " -> DERIVADA. Asistencia #" + asistenciaId + ". Motivo: " + motivoValidado);
                conexion.commit();
                return asistenciaId;
            } catch (SQLException | RuntimeException error) {
                conexion.rollback();
                throw error;
            }
        }
    }

    public List<Solicitud> listar(Usuario usuario, String filtro) throws SQLException {
        ReglasAtencion.validarRol(usuario, "ATENCION", "SOPORTE", "COORDINADOR", "TECNICO", "ADMINISTRADOR");
        try (Connection conexion = ConexionBD.abrir()) {
            return solicitudDAO.listar(conexion, usuario, filtro);
        }
    }

    public List<Opcion> listarServicios(Usuario usuario) throws SQLException {
        ReglasAtencion.validarRol(usuario, "ATENCION");
        try (Connection conexion = ConexionBD.abrir()) { return catalogoDAO.listarServicios(conexion); }
    }

    public String consultarHistorial(Usuario usuario, int solicitudId) throws SQLException {
        ReglasAtencion.validarRol(usuario, "ATENCION", "SOPORTE", "COORDINADOR", "TECNICO", "ADMINISTRADOR");
        try (Connection conexion = ConexionBD.abrir()) {
            Solicitud solicitud = solicitudDAO.buscar(conexion, solicitudId, false);
            if (solicitud == null) { throw new IllegalArgumentException("La solicitud no existe."); }
            if ("TECNICO".equals(usuario.getRol())
                    && !solicitudDAO.tecnicoPuedeConsultar(conexion, solicitud.getServicioId(), usuario.getId())) {
                throw new IllegalArgumentException("No tiene asistencias asignadas sobre este servicio.");
            }
            return actividadDAO.consultarHistorialServicio(conexion, solicitud.getServicioId());
        }
    }
}
