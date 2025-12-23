package pe.edu.cibertec.alquilape.model.enums;

public enum Rol {
    ADMIN("Administrador"),
    SUPERVISOR("Supervisor"),
    ASISTENTE("Asistente");

    private final String descripcion;

    Rol(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
