package ar.edu.gestiontecnica.ui;

import java.awt.Window;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

// Las operaciones de base de datos no se ejecutan en el hilo que dibuja Swing.
// Se usa siempre el mismo esquema: realizar (segundo plano) y alFinalizar (pantalla).
public abstract class TareaInterfaz extends SwingWorker<Void, Void> {
    private final Window ventana;

    public TareaInterfaz(Window ventana) { this.ventana = ventana; }
    protected abstract void realizar() throws Exception;
    protected abstract void alFinalizar();
    protected void alFallar() { /* Cada pantalla puede informar que conserva datos anteriores. */ }

    public void iniciar() {
        ventana.setEnabled(false);
        execute();
    }

    @Override
    protected Void doInBackground() throws Exception {
        realizar();
        return null;
    }

    @Override
    protected void done() {
        ventana.setEnabled(true);
        try {
            get();
            alFinalizar();
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            alFallar();
            JOptionPane.showMessageDialog(ventana, "Se interrumpió la operación. Actualice la consulta.");
        } catch (ExecutionException error) {
            alFallar();
            Throwable causa = error.getCause();
            if (causa instanceof IllegalArgumentException) {
                JOptionPane.showMessageDialog(ventana, causa.getMessage(), "Revisar datos", JOptionPane.WARNING_MESSAGE);
            } else {
                causa.printStackTrace();
                JOptionPane.showMessageDialog(ventana,
                        "No se pudo confirmar la operación. Revise la conexión y actualice los datos.\n"
                        + "La consola muestra el detalle técnico; la configuración está en config/db.properties.",
                        "Error de operación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
