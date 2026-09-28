package ar.edu.gestiontecnica.modelo;

// Elemento de un desplegable: conserva el ID y muestra un texto comprensible.
public class Opcion {
    private final int id;
    private final String descripcion;

    public Opcion(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }
    public int getId() { return id; }
    @Override
    public String toString() { return descripcion; }
}
