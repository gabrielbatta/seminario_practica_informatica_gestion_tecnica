package ar.edu.gestiontecnica.dao;

import java.sql.*;

public class ActividadDAO {
    public void registrar(Connection conexion, int solicitudId, int usuarioId,
            String operacion, String detalle) throws SQLException {
        String sql = "INSERT INTO registro_actividad (id_solicitud, id_usuario, operacion, detalle) VALUES (?, ?, ?, ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, solicitudId);
            sentencia.setInt(2, usuarioId);
            sentencia.setString(3, operacion);
            sentencia.setString(4, detalle);
            sentencia.executeUpdate();
        }
    }

    public String consultarHistorialServicio(Connection conexion, int servicioId) throws SQLException {
        // Cuatro consultas separadas para que sea claro de dónde proviene cada dato.
        StringBuilder texto = new StringBuilder();
        texto.append("HISTORIAL DEL SERVICIO ").append(servicioId).append("\n\nSOLICITUDES\n");
        String sql = "SELECT id_solicitud, fecha_registro, descripcion, estado, solucion_cierre "
                + "FROM solicitud WHERE id_servicio = ? ORDER BY id_solicitud";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, servicioId);
            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    texto.append("#").append(filas.getInt("id_solicitud")).append(" | ")
                        .append(filas.getString("fecha_registro")).append(" | ").append(filas.getString("estado"))
                        .append("\n").append(filas.getString("descripcion")).append("\n");
                    String solucion = filas.getString("solucion_cierre");
                    if (solucion != null) { texto.append("Solución: ").append(solucion).append("\n"); }
                    texto.append("\n");
                }
            }
        }
        texto.append("ATENCIONES REMOTAS\n");
        sql = "SELECT r.*, u.nombre FROM atencion_remota r INNER JOIN solicitud s ON s.id_solicitud = r.id_solicitud "
                + "INNER JOIN usuario u ON u.id_usuario = r.id_usuario WHERE s.id_servicio = ? ORDER BY r.id_atencion";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, servicioId);
            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    texto.append("Solicitud #").append(filas.getInt("id_solicitud")).append(" | ")
                        .append(filas.getString("fecha")).append(" | ").append(filas.getString("nombre"))
                        .append("\nDiagnóstico: ").append(filas.getString("diagnostico"))
                        .append("\nAcciones: ").append(filas.getString("acciones"))
                        .append("\nResultado: ").append(filas.getString("resultado"));
                    String solucion = filas.getString("solucion");
                    if (solucion != null) { texto.append("\nSolución: ").append(solucion); }
                    texto.append("\n\n");
                }
            }
        }
        texto.append("ASISTENCIAS DOMICILIARIAS\n");
        sql = "SELECT a.*, u.nombre FROM asistencia a INNER JOIN solicitud s ON s.id_solicitud = a.id_solicitud "
                + "LEFT JOIN usuario u ON u.id_usuario = a.id_tecnico WHERE s.id_servicio = ? ORDER BY a.id_asistencia";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, servicioId);
            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    texto.append("Visita #").append(filas.getInt("id_asistencia"))
                        .append(" | Solicitud #").append(filas.getInt("id_solicitud"))
                        .append(" | ").append(filas.getString("estado"))
                        .append("\nTécnico: ").append(valor(filas.getString("nombre")))
                        .append(" | Fecha: ").append(valor(filas.getString("fecha_programada")))
                        .append("\nMotivo: ").append(filas.getString("motivo"))
                        .append("\nDiagnóstico: ").append(valor(filas.getString("diagnostico")))
                        .append("\nTareas: ").append(valor(filas.getString("tareas")))
                        .append("\nResultado: ").append(valor(filas.getString("resultado")))
                        .append("\nDetalle: ").append(valor(filas.getString("detalle_resultado"))).append("\n\n");
                }
            }
        }
        texto.append("REGISTRO DE ACTIVIDAD\n");
        sql = "SELECT r.*, u.login FROM registro_actividad r INNER JOIN solicitud s ON s.id_solicitud = r.id_solicitud "
                + "INNER JOIN usuario u ON u.id_usuario = r.id_usuario WHERE s.id_servicio = ? ORDER BY r.id_registro";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, servicioId);
            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    texto.append(filas.getString("fecha")).append(" | #").append(filas.getInt("id_solicitud"))
                        .append(" | ").append(filas.getString("login")).append(" | ").append(filas.getString("operacion"))
                        .append("\n").append(filas.getString("detalle")).append("\n");
                }
            }
        }
        return texto.toString();
    }

    private String valor(String texto) {
        if (texto == null) { return "Sin registrar"; }
        return texto;
    }
}
