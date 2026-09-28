package ar.edu.gestiontecnica.dao;

import ar.edu.gestiontecnica.modelo.Asistencia;
import ar.edu.gestiontecnica.modelo.Usuario;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAO {
    public boolean existeActiva(Connection conexion, int solicitudId) throws SQLException {
        String sql = "SELECT 1 FROM asistencia WHERE id_solicitud = ? AND estado <> 'FINALIZADA' LIMIT 1";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, solicitudId);
            try (ResultSet filas = sentencia.executeQuery()) { return filas.next(); }
        }
    }

    public int insertar(Connection conexion, int solicitudId, String motivo) throws SQLException {
        String sql = "INSERT INTO asistencia (id_solicitud, motivo) VALUES (?, ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setInt(1, solicitudId);
            sentencia.setString(2, motivo);
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) { return claves.getInt(1); }
                throw new SQLException("No se obtuvo el identificador de asistencia.");
            }
        }
    }

    public Asistencia buscar(Connection conexion, int id, boolean bloquear) throws SQLException {
        String sql = "SELECT * FROM asistencia WHERE id_asistencia = ?";
        if (bloquear) { sql += " FOR UPDATE"; }
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            try (ResultSet filas = sentencia.executeQuery()) {
                if (filas.next()) { return leer(filas); }
            }
        }
        return null;
    }

    public List<Asistencia> listar(Connection conexion, Usuario usuario) throws SQLException {
        String sql = """
            SELECT a.*, c.nombre AS cliente, v.codigo AS servicio, u.nombre AS tecnico
            FROM asistencia a
            INNER JOIN solicitud s ON s.id_solicitud = a.id_solicitud
            INNER JOIN servicio v ON v.id_servicio = s.id_servicio
            INNER JOIN domicilio d ON d.id_domicilio = v.id_domicilio
            INNER JOIN cliente c ON c.id_cliente = d.id_cliente
            LEFT JOIN usuario u ON u.id_usuario = a.id_tecnico
            """;
        boolean esTecnico = "TECNICO".equals(usuario.getRol());
        if (esTecnico) { sql += " WHERE a.id_tecnico = ?"; }
        sql += " ORDER BY a.id_asistencia DESC";
        List<Asistencia> asistencias = new ArrayList<>();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            if (esTecnico) { sentencia.setInt(1, usuario.getId()); }
            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    Asistencia asistencia = leer(filas);
                    asistencia.setCliente(filas.getString("cliente"));
                    asistencia.setServicio(filas.getString("servicio"));
                    asistencia.setNombreTecnico(filas.getString("tecnico"));
                    asistencias.add(asistencia);
                }
            }
        }
        return asistencias;
    }

    public void guardarProgramacion(Connection conexion, Asistencia asistencia) throws SQLException {
        String sql = "UPDATE asistencia SET id_tecnico = ?, fecha_programada = ?, estado = ? WHERE id_asistencia = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            if (asistencia.getTecnicoId() == null) { sentencia.setNull(1, Types.INTEGER); }
            else { sentencia.setInt(1, asistencia.getTecnicoId()); }
            if (asistencia.getFechaProgramada() == null) { sentencia.setNull(2, Types.TIMESTAMP); }
            else { sentencia.setObject(2, asistencia.getFechaProgramada()); }
            sentencia.setString(3, asistencia.getEstado());
            sentencia.setInt(4, asistencia.getId());
            sentencia.executeUpdate();
        }
    }

    public void guardarDiagnostico(Connection conexion, int id, String diagnostico) throws SQLException {
        String sql = "UPDATE asistencia SET diagnostico = ? WHERE id_asistencia = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, diagnostico); sentencia.setInt(2, id); sentencia.executeUpdate();
        }
    }

    public void guardarTareas(Connection conexion, int id, String tareas) throws SQLException {
        String sql = "UPDATE asistencia SET tareas = ? WHERE id_asistencia = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, tareas); sentencia.setInt(2, id); sentencia.executeUpdate();
        }
    }

    public void finalizar(Connection conexion, int id, String resultado, String detalle) throws SQLException {
        String sql = "UPDATE asistencia SET estado = 'FINALIZADA', resultado = ?, detalle_resultado = ?, "
                + "fecha_finalizacion = CURRENT_TIMESTAMP WHERE id_asistencia = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, resultado); sentencia.setString(2, detalle);
            sentencia.setInt(3, id); sentencia.executeUpdate();
        }
    }

    private Asistencia leer(ResultSet fila) throws SQLException {
        Asistencia asistencia = new Asistencia();
        asistencia.setId(fila.getInt("id_asistencia"));
        asistencia.setSolicitudId(fila.getInt("id_solicitud"));
        int tecnico = fila.getInt("id_tecnico");
        if (!fila.wasNull()) { asistencia.setTecnicoId(tecnico); }
        asistencia.setMotivo(fila.getString("motivo"));
        asistencia.setFechaProgramada(fila.getObject("fecha_programada", LocalDateTime.class));
        asistencia.setEstado(fila.getString("estado"));
        asistencia.setDiagnostico(fila.getString("diagnostico"));
        asistencia.setTareas(fila.getString("tareas"));
        asistencia.setResultado(fila.getString("resultado"));
        asistencia.setDetalleResultado(fila.getString("detalle_resultado"));
        asistencia.setFechaFinalizacion(fila.getObject("fecha_finalizacion", LocalDateTime.class));
        return asistencia;
    }
}
