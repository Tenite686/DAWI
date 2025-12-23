package pe.edu.cibertec.alquilape.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import pe.edu.cibertec.alquilape.model.enums.EstadoVehiculo;
import pe.edu.cibertec.alquilape.model.enums.TipoVehiculo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class VehiculoDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VehiculoRequest {
        @NotBlank(message = "La marca es obligatoria")
        @Size(max = 50)
        private String marca;

        @NotBlank(message = "El modelo es obligatorio")
        @Size(max = 50)
        private String modelo;

        @NotNull(message = "El año es obligatorio")
        @Min(value = 1990, message = "Año mínimo 1990")
        @Max(value = 2030, message = "Año máximo 2030")
        private Integer anio;

        @NotBlank(message = "La placa es obligatoria")
        @Pattern(regexp = "^[A-Z0-9]{6,7}$", message = "Placa inválida")
        private String placa;

        @Size(max = 30)
        private String color;

        @NotNull(message = "El tipo es obligatorio")
        private TipoVehiculo tipo;

        private EstadoVehiculo estado;

        @NotNull(message = "El precio por día es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        private BigDecimal precioPorDia;

        @Min(value = 0, message = "El kilometraje no puede ser negativo")
        private Integer kilometraje;

        @NotNull(message = "La capacidad de pasajeros es obligatoria")
        @Min(value = 1, message = "Mínimo 1 pasajero")
        @Max(value = 12, message = "Máximo 12 pasajeros")
        private Integer capacidadPasajeros;

        private Map<String, Object> caracteristicasAdicionales;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VehiculoResponse {
        private Long id;
        private String marca;
        private String modelo;
        private Integer anio;
        private String placa;
        private String color;
        private String tipo;
        private String estado;
        private BigDecimal precioPorDia;
        private Integer kilometraje;
        private Integer capacidadPasajeros;
        private Map<String, Object> caracteristicasAdicionales;
        private Boolean activo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}