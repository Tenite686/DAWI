package pe.edu.cibertec.alquilape.model.enums;

public enum EstadoPago {
    PENDIENTE("Pendiente"),
    PAGADO("Pagado"),
    REEMBOLSADO("Reembolsado");

    private final String descripcion;

    EstadoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}