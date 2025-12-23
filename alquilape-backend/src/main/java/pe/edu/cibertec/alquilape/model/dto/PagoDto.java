package pe.edu.cibertec.alquilape.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import pe.edu.cibertec.alquilape.model.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PagoRequest {
        @NotNull(message = "El alquiler es obligatorio")
        private Long alquilerId;

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
        private BigDecimal monto;

        @NotNull(message = "La fecha de pago es obligatoria")
        private LocalDateTime fechaPago;

        @NotNull(message = "El método de pago es obligatorio")
        private MetodoPago metodoPago;

        @Size(max = 100)
        private String numeroTransaccion;

        @Size(max = 500)
        private String observaciones;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PagoResponse {
        private Long id;
        private Long alquilerId;
        private BigDecimal monto;
        private LocalDateTime fechaPago;
        private String metodoPago;
        private String estado;
        private String numeroTransaccion;
        private String observaciones;
        private LocalDateTime createdAt;
    }
}