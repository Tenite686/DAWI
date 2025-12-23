package pe.edu.cibertec.alquilape.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AlquilerDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlquilerRequest {
        @NotNull(message = "El cliente es obligatorio")
        private Long clienteId;

        @NotNull(message = "El vehículo es obligatorio")
        private Long vehiculoId;

        @NotNull(message = "La fecha de inicio es obligatoria")
        @Future(message = "La fecha de inicio debe ser futura")
        private LocalDateTime fechaInicio;

        @NotNull(message = "La fecha fin estimada es obligatoria")
        @Future(message = "La fecha fin estimada debe ser futura")
        private LocalDateTime fechaFinEstimada;

        @NotNull(message = "El kilometraje inicial es obligatorio")
        @Min(value = 0, message = "El kilometraje no puede ser negativo")
        private Integer kilometrajeInicio;

        @Size(max = 500)
        private String observaciones;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlquilerDevolucionRequest {
        @NotNull(message = "La fecha de devolución es obligatoria")
        private LocalDateTime fechaDevolucion;

        @NotNull(message = "El kilometraje final es obligatorio")
        @Min(value = 0, message = "El kilometraje no puede ser negativo")
        private Integer kilometrajeFin;

        @Size(max = 500)
        private String observaciones;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlquilerResponse {
        private Long id;
        private ClienteInfo cliente;
        private VehiculoInfo vehiculo;
        private UsuarioInfo usuario;
        private LocalDateTime fechaInicio;
        private LocalDateTime fechaFinEstimada;
        private LocalDateTime fechaDevolucionReal;
        private BigDecimal precioTotal;
        private String estado;
        private Integer kilometrajeInicio;
        private Integer kilometrajeFin;
        private String observaciones;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ClienteInfo {
            private Long id;
            private String nombreCompleto;
            private String dniRuc;
            private String telefono;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class VehiculoInfo {
            private Long id;
            private String marca;
            private String modelo;
            private String placa;
            private BigDecimal precioPorDia;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class UsuarioInfo {
            private Long id;
            private String nombreCompleto;
        }
    }
}