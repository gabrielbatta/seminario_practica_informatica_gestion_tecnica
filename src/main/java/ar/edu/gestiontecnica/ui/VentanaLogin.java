package ar.edu.gestiontecnica.ui;

import ar.edu.gestiontecnica.modelo.Usuario;
import ar.edu.gestiontecnica.servicio.AutenticacionServicio;
import java.awt.*;
import java.util.Arrays;
import javax.swing.*;

public class VentanaLogin extends JFrame {
    private final JTextField usuario = new JTextField(22);
    private final JPasswordField clave = new JPasswordField(22);
    private final JButton ingresar = new JButton("Ingresar");

    public VentanaLogin() {
        this(pantallaDelCursor());
    }

    public VentanaLogin(GraphicsConfiguration pantalla) {
        super("Gestión Técnica | Iniciar sesión", pantalla);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel contenido = new JPanel(new BorderLayout(16, 16));
        contenido.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        JLabel titulo = new JLabel("Gestión de asistencias técnicas");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        contenido.add(titulo, BorderLayout.NORTH);
        JPanel campos = new JPanel(new GridLayout(4, 1, 4, 6));
        campos.add(new JLabel("Usuario")); campos.add(usuario);
        campos.add(new JLabel("Contraseña")); campos.add(clave);
        contenido.add(campos, BorderLayout.CENTER);
        JPanel pie = new JPanel(new BorderLayout(10, 12));
        pie.add(new JLabel("Prototipo académico · Acceso interno por roles"), BorderLayout.NORTH);
        JTextArea cuentasDemo = new JTextArea("""
                Rol               Usuario        Contraseña
                Atención          atencion1      1234
                Soporte           soporte1       1234
                Coordinación      coordinador1   1234
                Técnico de campo  tecnico1       1234
                Técnico de campo  tecnico2       1234
                Administrador     admin1         1234
                """);
        cuentasDemo.setName("cuentasDemo");
        cuentasDemo.setEditable(false);
        cuentasDemo.setOpaque(false);
        cuentasDemo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        cuentasDemo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Debug · Cuentas de demostración"),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        pie.add(cuentasDemo, BorderLayout.CENTER);
        pie.add(ingresar, BorderLayout.SOUTH);
        contenido.add(pie, BorderLayout.SOUTH);
        setContentPane(contenido);
        usuario.setName("usuario"); clave.setName("clave"); ingresar.setName("ingresar");
        ingresar.addActionListener(evento -> ingresar());
        getRootPane().setDefaultButton(ingresar);
        pack(); setResizable(false);
        Rectangle limites = getGraphicsConfiguration().getBounds();
        Insets margenes = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        setLocation(limites.x + margenes.left + Math.max(0, (limites.width - margenes.left - margenes.right - getWidth()) / 2),
                limites.y + margenes.top + Math.max(0, (limites.height - margenes.top - margenes.bottom - getHeight()) / 2));
    }

    private static GraphicsConfiguration pantallaDelCursor() {
        PointerInfo puntero = MouseInfo.getPointerInfo();
        return puntero == null ? null : puntero.getDevice().getDefaultConfiguration();
    }

    private void ingresar() {
        final String loginIngresado = usuario.getText();
        final char[] claveIngresada = clave.getPassword();
        clave.setText("");
        new TareaInterfaz(this) {
            private Usuario usuarioAutenticado;
            @Override
            protected void realizar() throws Exception {
                try {
                    usuarioAutenticado = new AutenticacionServicio().ingresar(loginIngresado, claveIngresada);
                } finally {
                    Arrays.fill(claveIngresada, '\0');
                }
            }
            @Override
            protected void alFinalizar() {
                VentanaPrincipal principal = new VentanaPrincipal(usuarioAutenticado, getGraphicsConfiguration());
                principal.setVisible(true);
                principal.recargar();
                dispose();
            }
        }.iniciar();
    }
}
