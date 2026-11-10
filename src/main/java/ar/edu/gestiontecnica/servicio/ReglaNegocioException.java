package ar.edu.gestiontecnica.servicio;

public class ReglaNegocioException extends IllegalArgumentException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
