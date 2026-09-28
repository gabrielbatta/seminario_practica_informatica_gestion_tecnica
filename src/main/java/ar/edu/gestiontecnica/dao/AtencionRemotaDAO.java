package ar.edu.gestiontecnica.dao;

import java.sql.*;

public class AtencionRemotaDAO {
    public void insertar(Connection conexion, int solicitudId, int usuarioId, String diagnostico,
            String acciones, String resultado, String solucion) throws SQLException {
        String sql = "INSERT INTO atencion_remota "
                + "(id_solicitud, id_usuario, diagnostico, acciones, resultado, solucion) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, solicitudId);
            sentencia.setInt(2, usuarioId);
            sentencia.setString(3, diagnostico);
            sentencia.setString(4, acciones);
            sentencia.setString(5, resultado);
            if (solucion == null) { sentencia.setNull(6, Types.VARCHAR); }
            else { sentencia.setString(6, solucion); }
            sentencia.executeUpdate();
        }
    }
}
