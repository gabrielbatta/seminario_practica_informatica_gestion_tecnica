package ar.edu.gestiontecnica;

import ar.edu.gestiontecnica.ui.VentanaLogin;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Toda creación de componentes Swing comienza en el hilo de eventos.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception error) {
                    System.err.println("Se utiliza la apariencia predeterminada de Swing.");
                }
                new VentanaLogin().setVisible(true);
            }
        });
    }
}
