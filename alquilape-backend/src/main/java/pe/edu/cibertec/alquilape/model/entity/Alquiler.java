package pe.edu.cibertec.alquilape.model.entity;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.cibertec.alquilape.model.enums.EstadoAlquiler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alquileres", indexes = {
        @Index(name = "idx_cliente", columnList = "cliente_id"),
        @Index(name = "idx_vehiculo", columnList = "vehiculo_id"),
        @Index(name = "idx_estado", columnList = "estado"),
        @Index(name = "idx_fechas", columnList = "fechaInicio, fechaFinEstimada")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alquiler extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaFinEstimada;

    @Column
    private LocalDateTime fechaDevolucionReal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoAlquiler estado = EstadoAlquiler.ACTIVO;

    @Column(nullable = false)
    private Integer kilometrajeInicio;

    @Column
    private Integer kilometrajeFin;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @OneToMany(mappedBy = "alquiler", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Pago> pagos = new ArrayList<>();
}