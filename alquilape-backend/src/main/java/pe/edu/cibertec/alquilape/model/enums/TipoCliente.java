package pe.edu.cibertec.alquilape.model.enums;

public enum TipoCliente {
    PERSONA("Persona Natural"),
    EMPRESA("Empresa");

    private final String descripcion;

    TipoCliente(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
