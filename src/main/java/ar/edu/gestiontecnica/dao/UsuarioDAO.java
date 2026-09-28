package ar.edu.gestiontecnica.dao;

import ar.edu.gestiontecnica.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    public Usuario buscarPorLogin(Connection conexion, String login) throws SQLException {
        String sql = "SELECT id_usuario, nombre, login, clave_hash, rol, activo FROM usuario WHERE login = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, login);
            try (ResultSet filas = sentencia.executeQuery()) {
                if (filas.next()) { return leer(filas); }
            }
        }
        return null;
    }

    public Usuario buscarPorId(Connection conexion, int id) throws SQLException {
        String sql = "SELECT id_usuario, nombre, login, clave_hash, rol, activo FROM usuario WHERE id_usuario = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            try (ResultSet filas = sentencia.executeQuery()) {
                if (filas.next()) { return leer(filas); }
            }
        }
        return null;
    }

    private Usuario leer(ResultSet fila) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(fila.getInt("id_usuario"));
        usuario.setNombre(fila.getString("nombre"));
        usuario.setLogin(fila.getString("login"));
        usuario.setClaveHash(fila.getString("clave_hash"));
        usuario.setRol(fila.getString("rol"));
        usuario.setActivo(fila.getBoolean("activo"));
        return usuario;
    }
}
