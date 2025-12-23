package pe.edu.cibertec.alquilape.model.enums;

public enum TipoVehiculo {
    AUTO("Automóvil"),
    CAMIONETA("Camioneta");

    private final String descripcion;

    TipoVehiculo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}