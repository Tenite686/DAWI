package pe.edu.cibertec.alquilape.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pe.edu.cibertec.alquilape.model.enums.EstadoVehiculo;
import pe.edu.cibertec.alquilape.model.enums.TipoVehiculo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "vehiculos", indexes = {
        @Index(name = "idx_placa", columnList = "placa"),
        @Index(name = "idx_marca_modelo", columnList = "marca, modelo"),
        @Index(name = "idx_tipo_estado", columnList = "tipo, estado")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehiculo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 50)
    private String modelo;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    @Column(length = 30)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoVehiculo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoVehiculo estado = EstadoVehiculo.DISPONIBLE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioPorDia;

    @Column(nullable = false)
    @Builder.Default
    private Integer kilometraje = 0;

    @Column(nullable = false)
    private Integer capacidadPasajeros;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> caracteristicasAdicionales;

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Alquiler> alquileres = new ArrayList<>();
}