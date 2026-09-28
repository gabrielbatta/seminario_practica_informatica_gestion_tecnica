package ar.edu.gestiontecnica;

import ar.edu.gestiontecnica.modelo.*;
import ar.edu.gestiontecnica.servicio.*;
import java.time.LocalDateTime;

// Ejecutor sencillo: no necesita JUnit, MySQL ni conexiones de red.
public class PruebasUnitarias {
    private static int aprobadas = 0;
    private static int fallidas = 0;
    public static void main(String[] args) throws Exception {
        LocalDateTime ahora = LocalDateTime.of(2026, 9, 27, 12, 0);
        Usuario tecnico = usuario(4, "TECNICO");
        Usuario otroTecnico = usuario(5, "TECNICO");
        Usuario soporte = usuario(2, "SOPORTE");
        Solicitud abierta = new Solicitud(); abierta.setEstado("REGISTRADA");
        Solicitud cerrada = new Solicitud(); cerrada.setEstado("CERRADA");
        Asistencia visita = asistencia(ahora.minusHours(1));

        comprobar("U01", "Limpiar espacios de entrada", "Falla de señal".equals(
                ReglasAtencion.textoObligatorio(" Falla de señal ", "inconveniente", 1000)));
        rechaza("U02", "Campo obligatorio vacío", () -> ReglasAtencion.textoObligatorio("   ", "inconveniente", 1000));
        rechaza("U03", "Longitud máxima", () -> ReglasAtencion.textoObligatorio("abcd", "campo", 3));
        acepta("U04", "Rol autorizado", () -> ReglasAtencion.validarRol(soporte, "SOPORTE"));
        rechaza("U05", "Rol no autorizado", () -> ReglasAtencion.validarRol(soporte, "ATENCION"));
        rechaza("U06", "Sin sesión", () -> ReglasAtencion.validarRol(null, "ATENCION"));
        Usuario inactivo = usuario(7, "TECNICO"); inactivo.setActivo(false);
        rechaza("U07", "Usuario inactivo", () -> ReglasAtencion.validarRol(inactivo, "TECNICO"));
        rechaza("U08", "Solicitud cerrada", () -> ReglasAtencion.validarSolicitudAbierta(cerrada));
        rechaza("U09", "Derivación duplicada", () -> ReglasAtencion.validarDerivacion(abierta, true));
        acepta("U10", "Derivación sin visita activa", () -> ReglasAtencion.validarDerivacion(abierta, false));
        rechaza("U11", "Cierre remoto con visita activa", () -> ReglasAtencion.validarAtencionRemota(abierta, true));
        acepta("U12", "Atención remota permitida", () -> ReglasAtencion.validarAtencionRemota(abierta, false));
        comprobar("U13", "Sin técnico sigue pendiente", "PENDIENTE".equals(ReglasAtencion.calcularEstadoProgramacion(null, ahora)));
        comprobar("U14", "Sin fecha sigue pendiente", "PENDIENTE".equals(ReglasAtencion.calcularEstadoProgramacion(4, null)));
        comprobar("U15", "Fecha y técnico programan visita", "PROGRAMADA".equals(ReglasAtencion.calcularEstadoProgramacion(4, ahora)));
        rechaza("U16", "Otro técnico no puede intervenir", () -> ReglasAtencion.validarTecnicoAsignado(otroTecnico, visita));
        rechaza("U17", "Finalizar sin diagnóstico", () -> ReglasAtencion.validarFinalizacion(tecnico, visita, "RESUELTO", "Solución", ahora));
        visita.setDiagnostico("Conector dañado");
        rechaza("U18", "Finalizar sin tareas", () -> ReglasAtencion.validarFinalizacion(tecnico, visita, "RESUELTO", "Solución", ahora));
        visita.setTareas("Se reemplazó el conector");
        rechaza("U19", "Finalizar sin detalle", () -> ReglasAtencion.validarFinalizacion(tecnico, visita, "RESUELTO", "", ahora));
        rechaza("U20", "Resultado fuera del dominio", () -> ReglasAtencion.validarFinalizacion(tecnico, visita, "OTRO", "Detalle", ahora));
        acepta("U21", "Finalizar visita resuelta", () -> ReglasAtencion.validarFinalizacion(tecnico, visita, "RESUELTO", "Señal recuperada", ahora));
        acepta("U22", "Finalizar visita no resuelta", () -> ReglasAtencion.validarFinalizacion(tecnico, visita, "PENDIENTE", "Requiere otra intervención", ahora));
        visita.setFechaProgramada(ahora.plusDays(1));
        rechaza("U23", "No finalizar visita futura", () -> ReglasAtencion.validarFinalizacion(tecnico, visita, "RESUELTO", "Detalle", ahora));
        visita.setEstado("FINALIZADA");
        rechaza("U24", "No modificar visita finalizada", () -> ReglasAtencion.validarAsistenciaEditable(visita));
        String hash = SeguridadClave.generar("ClaveDePrueba!".toCharArray());
        comprobar("U25", "Verificar hash correcto", SeguridadClave.verificar("ClaveDePrueba!".toCharArray(), hash));
        comprobar("U26", "Rechazar otra contraseña", !SeguridadClave.verificar("OtraClave!".toCharArray(), hash));
        comprobar("U27", "Rechazar hash mal formado", !SeguridadClave.verificar("Clave".toCharArray(), "invalido"));
        String hash2 = SeguridadClave.generar("ClaveDePrueba!".toCharArray());
        comprobar("U28", "Sales distintas para la misma contraseña", !hash.equals(hash2));
        System.out.println("RESUMEN | aprobadas=" + aprobadas + " | fallidas=" + fallidas);
        if (fallidas > 0) { throw new AssertionError("Hay pruebas unitarias fallidas."); }
    }
    private static Usuario usuario(int id, String rol) {
        Usuario u = new Usuario(); u.setId(id); u.setRol(rol); u.setActivo(true); return u;
    }
    private static Asistencia asistencia(LocalDateTime fecha) {
        Asistencia a = new Asistencia(); a.setId(1); a.setSolicitudId(1); a.setTecnicoId(4);
        a.setFechaProgramada(fecha); a.setEstado("PROGRAMADA"); return a;
    }
    private static void comprobar(String id, String nombre, boolean condicion) {
        if (condicion) { aprobadas++; System.out.println(id + " | APROBADA | " + nombre); }
        else { fallidas++; System.out.println(id + " | FALLIDA | " + nombre); }
    }
    private static void rechaza(String id, String nombre, Runnable accion) {
        try { accion.run(); comprobar(id, nombre, false); }
        catch (IllegalArgumentException esperado) { comprobar(id, nombre, true); }
    }
    private static void acepta(String id, String nombre, Runnable accion) {
        try { accion.run(); comprobar(id, nombre, true); }
        catch (IllegalArgumentException inesperado) { comprobar(id, nombre, false); }
    }
}
