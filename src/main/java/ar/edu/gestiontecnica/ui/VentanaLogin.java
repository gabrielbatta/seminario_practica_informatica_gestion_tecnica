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
        super("Gestión Técnica | Iniciar sesión");
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
        pie.add(ingresar, BorderLayout.SOUTH);
        contenido.add(pie, BorderLayout.SOUTH);
        setContentPane(contenido);
        usuario.setName("usuario"); clave.setName("clave"); ingresar.setName("ingresar");
        ingresar.addActionListener(evento -> ingresar());
        getRootPane().setDefaultButton(ingresar);
        pack(); setResizable(false); setLocationRelativeTo(null);
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
                VentanaPrincipal principal = new VentanaPrincipal(usuarioAutenticado);
                principal.setVisible(true);
                principal.recargar();
                dispose();
            }
        }.iniciar();
    }
}
