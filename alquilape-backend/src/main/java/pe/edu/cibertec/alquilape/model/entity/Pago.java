package pe.edu.cibertec.alquilape.model.entity;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.cibertec.alquilape.model.enums.EstadoPago;
import pe.edu.cibertec.alquilape.model.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos", indexes = {
        @Index(name = "idx_alquiler", columnList = "alquiler_id"),
        @Index(name = "idx_estado", columnList = "estado"),
        @Index(name = "idx_fecha", columnList = "fechaPago")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alquiler_id", nullable = false)
    private Alquiler alquiler;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDateTime fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Column(length = 100)
    private String numeroTransaccion;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}