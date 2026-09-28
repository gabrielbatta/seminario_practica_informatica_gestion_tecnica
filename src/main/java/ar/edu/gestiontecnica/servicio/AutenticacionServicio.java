package ar.edu.gestiontecnica.servicio;

import ar.edu.gestiontecnica.config.ConexionBD;
import ar.edu.gestiontecnica.dao.UsuarioDAO;
import ar.edu.gestiontecnica.modelo.Usuario;
import java.sql.Connection;
import java.sql.SQLException;
import java.security.GeneralSecurityException;

public class AutenticacionServicio {
    public Usuario ingresar(String login, char[] clave) throws SQLException, GeneralSecurityException {
        String nombre = ReglasAtencion.textoObligatorio(login, "el usuario", 40);
        if (clave == null || clave.length == 0) { throw new IllegalArgumentException("Completar la contraseña."); }
        try (Connection conexion = ConexionBD.abrir()) {
            Usuario usuario = new UsuarioDAO().buscarPorLogin(conexion, nombre);
            if (usuario == null || !usuario.getActivo()
                    || !SeguridadClave.verificar(clave, usuario.getClaveHash())) {
                throw new IllegalArgumentException("Usuario o contraseña incorrectos.");
            }
            // No conservar el hash en la sesión de la pantalla.
            usuario.setClaveHash(null);
            return usuario;
        }
    }
}
