package ar.edu.gestiontecnica.dao;

import ar.edu.gestiontecnica.modelo.Opcion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogoDAO {
    public List<Opcion> listarServicios(Connection conexion) throws SQLException {
        String sql = """
            SELECT s.id_servicio, s.codigo, s.tipo, c.nombre, d.direccion
            FROM servicio s
            INNER JOIN domicilio d ON d.id_domicilio = s.id_domicilio
            INNER JOIN cliente c ON c.id_cliente = d.id_cliente
            WHERE s.activo = TRUE ORDER BY c.nombre, s.codigo
            """;
        List<Opcion> opciones = new ArrayList<>();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                String descripcion = filas.getString("nombre") + " | " + filas.getString("direccion")
                        + " | " + filas.getString("codigo") + " (" + filas.getString("tipo") + ")";
                opciones.add(new Opcion(filas.getInt("id_servicio"), descripcion));
            }
        }
        return opciones;
    }

    public boolean servicioValido(Connection conexion, int id) throws SQLException {
        String sql = """
            SELECT s.id_servicio FROM servicio s
            INNER JOIN domicilio d ON d.id_domicilio = s.id_domicilio
            INNER JOIN cliente c ON c.id_cliente = d.id_cliente
            WHERE s.id_servicio = ? AND s.activo = TRUE
              AND CHAR_LENGTH(TRIM(c.telefono)) > 0 AND CHAR_LENGTH(TRIM(d.direccion)) > 0
            """;
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            try (ResultSet filas = sentencia.executeQuery()) { return filas.next(); }
        }
    }

    public List<Opcion> listarTecnicos(Connection conexion) throws SQLException {
        List<Opcion> opciones = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre FROM usuario WHERE rol = 'TECNICO' AND activo = TRUE ORDER BY nombre";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                opciones.add(new Opcion(filas.getInt("id_usuario"), filas.getString("nombre")));
            }
        }
        return opciones;
    }
}
