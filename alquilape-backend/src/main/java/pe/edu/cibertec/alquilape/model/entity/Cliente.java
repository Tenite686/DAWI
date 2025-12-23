package pe.edu.cibertec.alquilape.model.entity;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.cibertec.alquilape.model.enums.TipoCliente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes", indexes = {
        @Index(name = "idx_dni_ruc", columnList = "dniRuc"),
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_nombre_apellido", columnList = "nombre, apellido")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 20)
    private String dniRuc;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(length = 200)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCliente tipo;

    @Column(length = 20)
    private String licenciaNumero;

    @Column
    private LocalDate licenciaVencimiento;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Alquiler> alquileres = new ArrayList<>();

    public String getNombreCompleto() {
        return apellido != null ? nombre + " " + apellido : nombre;
    }
}
