package ar.edu.gestiontecnica;

import ar.edu.gestiontecnica.config.ConexionBD;
import ar.edu.gestiontecnica.dao.*;
import ar.edu.gestiontecnica.modelo.*;
import ar.edu.gestiontecnica.servicio.*;
import java.sql.*;
import java.time.LocalDateTime;

// Requiere MySQL real y 01, 02 y 03 ejecutados. No usa simulaciones de persistencia.
// Crea solicitudes identificadas como PRUEBA_TP2 y conserva sus historiales.
public class PruebasIntegracion {
    private static int aprobadas = 0;
    public static void main(String[] args) throws Exception {
        if (args.length != 1 || !"--confirmar-base-pruebas".equals(args[0])) {
            throw new IllegalArgumentException("Ejecutar solo sobre una base de práctica con --confirmar-base-pruebas.");
        }
        try (Connection conexion = ConexionBD.abrir()) {
            DatabaseMetaData meta = conexion.getMetaData();
            System.out.println("MOTOR | " + meta.getDatabaseProductName() + " | " + meta.getDatabaseProductVersion());
            if (!"MySQL".equals(meta.getDatabaseProductName())) {
                throw new IllegalArgumentException("Estas pruebas deben ejecutarse contra MySQL.");
            }
        }
        AutenticacionServicio login = new AutenticacionServicio();
        Usuario operador = login.ingresar("atencion1", "DemoTP2!2026".toCharArray());
        Usuario soporte = login.ingresar("soporte1", "DemoTP2!2026".toCharArray());
        Usuario coordinador = login.ingresar("coordinador1", "DemoTP2!2026".toCharArray());
        Usuario tecnico = login.ingresar("tecnico1", "DemoTP2!2026".toCharArray());
        Usuario otroTecnico = login.ingresar("tecnico2", "DemoTP2!2026".toCharArray());
        ok("I01", "Autenticación de los roles", operador.getId() == 1 && tecnico.getId() == 4);
        try {
            login.ingresar("' OR '1'='1", "cualquiera".toCharArray());
            throw new AssertionError("La consulta de autenticación no debe aceptar inyección.");
        } catch (IllegalArgumentException esperado) { ok("I02", "Login parametrizado rechaza entrada SQL", true); }

        SolicitudServicio s = new SolicitudServicio(); AsistenciaServicio a = new AsistenciaServicio();
        int remota = s.registrar(operador, 1, "PRUEBA_TP2 remota " + LocalDateTime.now());
        ok("I03", "Solicitud persistida", "REGISTRADA".equals(buscarSolicitud(remota).getEstado()));
        try {
            s.registrarAtencionRemota(soporte, remota, "", "Revisión", "RESUELTO", "Solución");
            throw new AssertionError("Se aceptó diagnóstico vacío.");
        } catch (IllegalArgumentException esperado) {
            ok("I04", "Datos inválidos no cambian estado", "REGISTRADA".equals(buscarSolicitud(remota).getEstado()));
        }
        s.registrarAtencionRemota(soporte, remota, "Configuración incorrecta", "Se corrigió la configuración", "RESUELTO", "Conectividad verificada");
        ok("I05", "Cierre remoto sin visita", "CERRADA".equals(buscarSolicitud(remota).getEstado()) && contarVisitas(remota) == 0);
        try {
            s.derivar(soporte, remota, "No debe permitirse");
            throw new AssertionError("Se modificó una solicitud cerrada.");
        } catch (IllegalArgumentException esperado) { ok("I06", "Inmutabilidad del cierre", true); }

        int solicitud = s.registrar(operador, 3, "PRUEBA_TP2 domicilio " + LocalDateTime.now());
        int visita = s.derivar(soporte, solicitud, "La evaluación detecta una falla física");
        ok("I07", "Derivación persistida", "DERIVADA".equals(buscarSolicitud(solicitud).getEstado()) && contarVisitas(solicitud) == 1);
        try {
            s.derivar(soporte, solicitud, "Duplicada");
            throw new AssertionError("Se duplicó una asistencia activa.");
        } catch (IllegalArgumentException esperado) { ok("I08", "Rechazo de visita activa duplicada", contarVisitas(solicitud) == 1); }
        a.programar(coordinador, visita, LocalDateTime.now().minusHours(1));
        ok("I09", "Fecha sola no completa programación", "PENDIENTE".equals(buscarAsistencia(visita).getEstado()));
        a.asignarTecnico(coordinador, visita, tecnico.getId());
        ok("I10", "Fecha y técnico programan", "PROGRAMADA".equals(buscarAsistencia(visita).getEstado()));
        try {
            a.registrarDiagnostico(otroTecnico, visita, "No autorizado");
            throw new AssertionError("Intervino otro técnico.");
        } catch (IllegalArgumentException esperado) {
            ok("I11", "Rechazo de técnico ajeno", buscarAsistencia(visita).getDiagnostico() == null);
        }
        try {
            a.finalizar(tecnico, visita, "PENDIENTE", "No debe finalizar sin datos");
            throw new AssertionError("Se finalizó una visita incompleta.");
        } catch (IllegalArgumentException esperado) {
            ok("I12", "Finalización incompleta rechazada", "PROGRAMADA".equals(buscarAsistencia(visita).getEstado()));
        }
        a.registrarDiagnostico(tecnico, visita, "Falla de señal en el tramo externo");
        a.registrarTareas(tecnico, visita, "Se revisó la conexión del domicilio");
        a.finalizar(tecnico, visita, "PENDIENTE", "Se requiere una segunda intervención");
        ok("I13", "RF17: visita finalizada y solicitud abierta",
                "FINALIZADA".equals(buscarAsistencia(visita).getEstado())
                && "PENDIENTE_CONTINUIDAD".equals(buscarSolicitud(solicitud).getEstado()));
        int segunda = s.derivar(soporte, solicitud, "Continuidad de atención");
        a.asignarTecnico(coordinador, segunda, tecnico.getId());
        a.programar(coordinador, segunda, LocalDateTime.now().minusMinutes(30));
        a.registrarDiagnostico(tecnico, segunda, "Conector externo dañado");
        a.registrarTareas(tecnico, segunda, "Se reemplazó el conector y se verificó señal");
        a.finalizar(tecnico, segunda, "RESUELTO", "Servicio normalizado y verificado");
        ok("I14", "Segunda visita cierra solicitud y conserva primera",
                "CERRADA".equals(buscarSolicitud(solicitud).getEstado()) && contarVisitas(solicitud) == 2);
        String historial = s.consultarHistorial(tecnico, solicitud);
        ok("I15", "Historial con antecedentes y responsables", historial.contains("FINALIZAR_ASISTENCIA")
                && historial.contains("tecnico1")
                && historial.contains("Se requiere una segunda intervención"));
        try {
            a.finalizar(tecnico, segunda, "RESUELTO", "Segundo clic");
            throw new AssertionError("Se volvió a finalizar una visita.");
        } catch (IllegalArgumentException esperado) { ok("I16", "Doble finalización rechazada", true); }
        boolean veVisitasAjenas = false;
        for (Asistencia visible : a.listar(otroTecnico)) {
            if (visible.getId() == visita || visible.getId() == segunda) { veVisitasAjenas = true; }
        }
        ok("I17", "Técnico ajeno no ve visitas", !veVisitasAjenas);
        System.out.println("RESUMEN | aprobadas=" + aprobadas + " | fallidas=0");
        System.out.println("DATOS CONSERVADOS | solicitud remota=" + remota + " | domiciliaria=" + solicitud);
    }
    private static Solicitud buscarSolicitud(int id) throws SQLException {
        try (Connection c = ConexionBD.abrir()) { return new SolicitudDAO().buscar(c, id, false); }
    }
    private static Asistencia buscarAsistencia(int id) throws SQLException {
        try (Connection c = ConexionBD.abrir()) { return new AsistenciaDAO().buscar(c, id, false); }
    }
    private static int contarVisitas(int solicitudId) throws SQLException {
        try (Connection c = ConexionBD.abrir(); PreparedStatement p = c.prepareStatement("SELECT COUNT(*) FROM asistencia WHERE id_solicitud = ?")) {
            p.setInt(1, solicitudId);
            try (ResultSet f = p.executeQuery()) { f.next(); return f.getInt(1); }
        }
    }
    private static void ok(String id, String nombre, boolean condicion) {
        if (!condicion) { System.out.println(id + " | FALLIDA | " + nombre); throw new AssertionError(nombre); }
        aprobadas++; System.out.println(id + " | APROBADA | " + nombre);
    }
}
