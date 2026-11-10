package ar.edu.gestiontecnica.servicio;

public class AccesoDenegadoException extends ReglaNegocioException {
    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
