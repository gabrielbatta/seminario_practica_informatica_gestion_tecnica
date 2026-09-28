package ar.edu.gestiontecnica.servicio;

import ar.edu.gestiontecnica.modelo.Asistencia;
import ar.edu.gestiontecnica.modelo.Solicitud;
import ar.edu.gestiontecnica.modelo.Usuario;
import java.time.LocalDateTime;

// Validaciones sin SQL ni pantallas: se pueden probar de manera independiente.
public class ReglasAtencion {
    public static String textoObligatorio(String texto, String campo, int longitudMaxima) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Completar " + campo + ".");
        }
        String limpio = texto.trim();
        if (limpio.length() > longitudMaxima) {
            throw new IllegalArgumentException(campo + ": máximo " + longitudMaxima + " caracteres.");
        }
        return limpio;
    }

    public static void validarRol(Usuario usuario, String... rolesPermitidos) {
        if (usuario == null || !usuario.getActivo()) {
            throw new IllegalArgumentException("Se requiere una sesión activa.");
        }
        for (String rol : rolesPermitidos) {
            if (rol.equals(usuario.getRol())) { return; }
        }
        throw new IllegalArgumentException("Su rol no permite realizar esta operación.");
    }

    public static void validarSolicitudAbierta(Solicitud solicitud) {
        if (solicitud == null) { throw new IllegalArgumentException("La solicitud no existe."); }
        if ("CERRADA".equals(solicitud.getEstado())) {
            throw new IllegalArgumentException("La solicitud está cerrada; se conserva solo para consulta.");
        }
    }

    public static void validarAtencionRemota(Solicitud solicitud, boolean tieneVisitaActiva) {
        validarSolicitudAbierta(solicitud);
        if (tieneVisitaActiva || "DERIVADA".equals(solicitud.getEstado())) {
            throw new IllegalArgumentException("Debe finalizar la visita activa antes de continuar remotamente.");
        }
    }

    public static void validarDerivacion(Solicitud solicitud, boolean tieneVisitaActiva) {
        validarSolicitudAbierta(solicitud);
        if (tieneVisitaActiva) {
            throw new IllegalArgumentException("La solicitud ya tiene una asistencia sin finalizar.");
        }
    }

    public static void validarResultado(String resultado) {
        if (!"RESUELTO".equals(resultado) && !"PENDIENTE".equals(resultado)) {
            throw new IllegalArgumentException("Seleccionar RESUELTO o PENDIENTE.");
        }
    }

    public static void validarAsistenciaEditable(Asistencia asistencia) {
        if (asistencia == null) { throw new IllegalArgumentException("La asistencia no existe."); }
        if ("FINALIZADA".equals(asistencia.getEstado())) {
            throw new IllegalArgumentException("La asistencia ya fue finalizada y no puede modificarse.");
        }
    }

    public static String calcularEstadoProgramacion(Integer tecnicoId, LocalDateTime fecha) {
        if (tecnicoId != null && fecha != null) { return "PROGRAMADA"; }
        return "PENDIENTE";
    }

    public static void validarTecnicoAsignado(Usuario usuario, Asistencia asistencia) {
        validarRol(usuario, "TECNICO");
        validarAsistenciaEditable(asistencia);
        if (!"PROGRAMADA".equals(asistencia.getEstado())) {
            throw new IllegalArgumentException("La asistencia debe tener fecha y técnico asignados.");
        }
        if (asistencia.getTecnicoId() == null || asistencia.getTecnicoId() != usuario.getId()) {
            throw new IllegalArgumentException("Solo el técnico asignado puede registrar esta intervención.");
        }
    }

    public static void validarFinalizacion(Usuario usuario, Asistencia asistencia,
            String resultado, String detalle, LocalDateTime ahora) {
        validarTecnicoAsignado(usuario, asistencia);
        textoObligatorio(asistencia.getDiagnostico(), "el diagnóstico", 2000);
        textoObligatorio(asistencia.getTareas(), "las tareas realizadas", 2000);
        textoObligatorio(detalle, "el detalle del resultado", 2000);
        validarResultado(resultado);
        if (asistencia.getFechaProgramada().isAfter(ahora)) {
            throw new IllegalArgumentException("No se puede finalizar una visita programada para el futuro.");
        }
    }
}
