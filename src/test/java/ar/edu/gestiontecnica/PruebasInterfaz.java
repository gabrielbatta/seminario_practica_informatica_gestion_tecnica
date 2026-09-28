package ar.edu.gestiontecnica;

import ar.edu.gestiontecnica.modelo.Usuario;
import ar.edu.gestiontecnica.ui.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

// Prueba de construcción visual y habilitación de botones. No simula MySQL.
// Las capturas tienen tablas vacías y NO acreditan operaciones persistidas.
public class PruebasInterfaz {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    VentanaLogin login = new VentanaLogin(); login.setVisible(true);
                    comprobar("UI01", buscar(login, "usuario") != null && buscar(login, "clave") != null);
                    capturar(login, "evidencias/01_login_swing.png"); login.dispose();
                    Usuario operador = usuario("ATENCION");
                    VentanaPrincipal atencion = new VentanaPrincipal(operador); atencion.setVisible(true);
                    comprobar("UI02", buscar(atencion, "nuevaSolicitud").isEnabled() && !buscar(atencion, "atencionRemota").isEnabled());
                    capturar(atencion, "evidencias/02_solicitudes_swing.png"); atencion.dispose();
                    Usuario tecnico = usuario("TECNICO");
                    VentanaPrincipal campo = new VentanaPrincipal(tecnico); campo.setVisible(true);
                    comprobar("UI03", !buscar(campo, "programar").isEnabled() && buscar(campo, "finalizar").isEnabled());
                    seleccionarSegundaPestana(campo);
                    capturar(campo, "evidencias/03_asistencias_swing.png"); campo.dispose();
                    System.out.println("RESUMEN | aprobadas=3 | fallidas=0 | sin conexión a MySQL");
                } catch (Exception error) { throw new RuntimeException(error); }
            }
        });
    }
    private static Usuario usuario(String rol) {
        Usuario u = new Usuario(); u.setId(1); u.setNombre("Usuario de prueba visual"); u.setRol(rol); u.setActivo(true); return u;
    }
    private static Component buscar(Container padre, String nombre) {
        for (Component componente : padre.getComponents()) {
            if (nombre.equals(componente.getName())) { return componente; }
            if (componente instanceof Container) {
                Component encontrado = buscar((Container) componente, nombre);
                if (encontrado != null) { return encontrado; }
            }
        }
        return null;
    }
    private static void seleccionarSegundaPestana(Container padre) {
        for (Component c : padre.getComponents()) {
            if (c instanceof JTabbedPane) { ((JTabbedPane) c).setSelectedIndex(1); return; }
            if (c instanceof Container) { seleccionarSegundaPestana((Container) c); }
        }
    }
    private static void comprobar(String id, boolean condicion) {
        if (!condicion) { throw new AssertionError(id + " FALLIDA"); }
        System.out.println(id + " | APROBADA | construcción y permisos visuales");
    }
    private static void capturar(JFrame ventana, String ruta) throws Exception {
        BufferedImage imagen = new BufferedImage(ventana.getWidth(), ventana.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D grafico = imagen.createGraphics(); ventana.paint(grafico); grafico.dispose();
        File salida = new File(ruta); salida.getParentFile().mkdirs(); ImageIO.write(imagen, "png", salida);
    }
}
