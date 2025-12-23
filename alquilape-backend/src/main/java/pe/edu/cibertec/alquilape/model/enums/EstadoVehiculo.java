package pe.edu.cibertec.alquilape.model.enums;

public enum EstadoVehiculo {
    DISPONIBLE("Disponible"),
    ALQUILADO("Alquilado"),
    MANTENIMIENTO("En Mantenimiento"),
    INACTIVO("Inactivo");

    private final String descripcion;

    EstadoVehiculo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
