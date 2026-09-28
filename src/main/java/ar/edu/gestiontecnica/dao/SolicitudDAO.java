package ar.edu.gestiontecnica.dao;

import ar.edu.gestiontecnica.modelo.Solicitud;
import ar.edu.gestiontecnica.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SolicitudDAO {
    public int insertar(Connection conexion, int servicioId, int usuarioId, String descripcion)
            throws SQLException {
        String sql = "INSERT INTO solicitud (id_servicio, id_usuario_registro, descripcion) VALUES (?, ?, ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setInt(1, servicioId);
            sentencia.setInt(2, usuarioId);
            sentencia.setString(3, descripcion);
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) { return claves.getInt(1); }
                throw new SQLException("No se obtuvo el identificador de la solicitud.");
            }
        }
    }

    public Solicitud buscar(Connection conexion, int id, boolean bloquear) throws SQLException {
        String sql = "SELECT id_solicitud, id_servicio, id_usuario_registro, fecha_registro, descripcion, "
                + "estado, fecha_cierre, canal_cierre, solucion_cierre FROM solicitud WHERE id_solicitud = ?";
        if (bloquear) { sql = sql + " FOR UPDATE"; }
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            try (ResultSet filas = sentencia.executeQuery()) {
                if (filas.next()) { return leer(filas); }
            }
        }
        return null;
    }

    public void cambiarEstado(Connection conexion, int id, String estado) throws SQLException {
        String sql = "UPDATE solicitud SET estado = ? WHERE id_solicitud = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, estado);
            sentencia.setInt(2, id);
            sentencia.executeUpdate();
        }
    }

    public void cerrar(Connection conexion, int id, String canal, String solucion) throws SQLException {
        String sql = "UPDATE solicitud SET estado = 'CERRADA', fecha_cierre = CURRENT_TIMESTAMP, "
                + "canal_cierre = ?, solucion_cierre = ? WHERE id_solicitud = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, canal);
            sentencia.setString(2, solucion);
            sentencia.setInt(3, id);
            sentencia.executeUpdate();
        }
    }

    public List<Solicitud> listar(Connection conexion, Usuario usuario, String filtro) throws SQLException {
        String sql = """
            SELECT s.*, c.nombre AS cliente, d.direccion AS domicilio, v.codigo AS servicio
            FROM solicitud s
            INNER JOIN servicio v ON v.id_servicio = s.id_servicio
            INNER JOIN domicilio d ON d.id_domicilio = v.id_domicilio
            INNER JOIN cliente c ON c.id_cliente = d.id_cliente
            WHERE (c.nombre LIKE ? OR v.codigo LIKE ? OR CAST(s.id_solicitud AS CHAR) LIKE ?)
            """;
        boolean esTecnico = "TECNICO".equals(usuario.getRol());
        if (esTecnico) {
            sql += " AND EXISTS (SELECT 1 FROM asistencia a WHERE a.id_solicitud = s.id_solicitud AND a.id_tecnico = ?)";
        }
        sql += " ORDER BY s.id_solicitud DESC";
        List<Solicitud> solicitudes = new ArrayList<>();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            String texto = "%" + filtro.trim() + "%";
            sentencia.setString(1, texto);
            sentencia.setString(2, texto);
            sentencia.setString(3, texto);
            if (esTecnico) { sentencia.setInt(4, usuario.getId()); }
            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    Solicitud solicitud = leer(filas);
                    solicitud.setCliente(filas.getString("cliente"));
                    solicitud.setDomicilio(filas.getString("domicilio"));
                    solicitud.setServicio(filas.getString("servicio"));
                    solicitudes.add(solicitud);
                }
            }
        }
        return solicitudes;
    }

    public boolean tecnicoPuedeConsultar(Connection conexion, int servicioId, int usuarioId) throws SQLException {
        String sql = "SELECT 1 FROM asistencia a INNER JOIN solicitud s ON s.id_solicitud = a.id_solicitud "
                + "WHERE s.id_servicio = ? AND a.id_tecnico = ? LIMIT 1";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, servicioId);
            sentencia.setInt(2, usuarioId);
            try (ResultSet filas = sentencia.executeQuery()) { return filas.next(); }
        }
    }

    private Solicitud leer(ResultSet fila) throws SQLException {
        Solicitud solicitud = new Solicitud();
        solicitud.setId(fila.getInt("id_solicitud"));
        solicitud.setServicioId(fila.getInt("id_servicio"));
        solicitud.setUsuarioRegistroId(fila.getInt("id_usuario_registro"));
        solicitud.setFechaRegistro(fila.getObject("fecha_registro", java.time.LocalDateTime.class));
        solicitud.setDescripcion(fila.getString("descripcion"));
        solicitud.setEstado(fila.getString("estado"));
        solicitud.setFechaCierre(fila.getObject("fecha_cierre", java.time.LocalDateTime.class));
        solicitud.setCanalCierre(fila.getString("canal_cierre"));
        solicitud.setSolucionCierre(fila.getString("solucion_cierre"));
        return solicitud;
    }
}
