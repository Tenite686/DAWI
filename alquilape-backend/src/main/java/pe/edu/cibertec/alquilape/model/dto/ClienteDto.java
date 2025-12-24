package pe.edu.cibertec.alquilape.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import pe.edu.cibertec.alquilape.model.enums.TipoCliente;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClienteDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClienteRequest {
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        private String nombre;

        @Size(max = 100)
        private String apellido;

        @NotBlank(message = "El DNI/RUC es obligatorio")
        @Pattern(regexp = "^[0-9]{8}|[0-9]{11}$", message = "DNI (8 dígitos) o RUC (11 dígitos) inválido")
        private String dniRuc;

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Email inválido")
        private String email;

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^[0-9]{9}$", message = "Teléfono inválido (9 dígitos)")
        private String telefono;

        @Size(max = 200)
        private String direccion;

        @NotNull(message = "El tipo de cliente es obligatorio")
        private TipoCliente tipo;

        @Size(max = 20)
        private String licenciaNumero;

        @Future(message = "La licencia debe estar vigente")
        private LocalDate licenciaVencimiento;

        private Boolean activo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClienteResponse {
        private Long id;
        private String nombre;
        private String apellido;
        private String nombreCompleto;
        private String dniRuc;
        private String email;
        private String telefono;
        private String direccion;
        private String tipo;
        private String licenciaNumero;
        private LocalDate licenciaVencimiento;
        private Boolean licenciaVigente;
        private Boolean activo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}