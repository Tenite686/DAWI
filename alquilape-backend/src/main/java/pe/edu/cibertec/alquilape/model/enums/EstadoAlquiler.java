package pe.edu.cibertec.alquilape.model.enums;

public enum EstadoAlquiler {
    ACTIVO("Activo"),
    COMPLETADO("Completado"),
    CANCELADO("Cancelado"),
    FINALIZADO("Finalizado");

    private final String descripcion;

    EstadoAlquiler(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}