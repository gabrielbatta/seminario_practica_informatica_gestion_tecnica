package ar.edu.gestiontecnica.config;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {
    public static Connection abrir() throws SQLException {
        // 1. Leer la configuración externa, que no se publica en GitHub.
        String ruta = System.getProperty("gt.config", "config/db.properties");
        Properties datos = new Properties();
        try (Reader archivo = Files.newBufferedReader(Path.of(ruta), StandardCharsets.UTF_8)) {
            datos.load(archivo);
        } catch (IOException error) {
            throw new SQLException("No se pudo leer " + ruta + ". Copiar el archivo de ejemplo.", error);
        }

        // 2. Comprobar que se configuraron los tres datos necesarios.
        String url = datos.getProperty("db.url", "").trim();
        String usuario = datos.getProperty("db.usuario", "").trim();
        String clave = datos.getProperty("db.clave", "");
        if (url.isEmpty() || usuario.isEmpty() || clave.isEmpty() || clave.equals("CAMBIAR_CLAVE_LOCAL")) {
            throw new SQLException("Completar la conexión y cambiar la clave de config/db.properties.");
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException error) {
            throw new SQLException("Falta MySQL Connector/J. Abrir el proyecto Maven completo.", error);
        }

        // 3. Abrir una conexión. Quien la solicita la cierra con try-with-resources.
        Connection conexion = DriverManager.getConnection(url, usuario, clave);
        // El prototipo usa la hora civil de la oficina. Alinear la sesión de MySQL al equipo.
        String zona = java.time.OffsetDateTime.now().getOffset().getId();
        if (zona.equals("Z")) { zona = "+00:00"; }
        try (java.sql.PreparedStatement sentencia = conexion.prepareStatement("SET time_zone = ?")) {
            sentencia.setString(1, zona);
            sentencia.execute();
        } catch (SQLException error) {
            conexion.close();
            throw error;
        }
        return conexion;
    }
}
