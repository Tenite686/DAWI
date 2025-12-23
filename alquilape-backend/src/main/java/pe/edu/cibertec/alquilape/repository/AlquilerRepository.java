package pe.edu.cibertec.alquilape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.cibertec.alquilape.model.entity.Alquiler;
import pe.edu.cibertec.alquilape.model.enums.EstadoAlquiler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long>,
        JpaSpecificationExecutor<Alquiler> {

    List<Alquiler> findByEstado(EstadoAlquiler estado);

    List<Alquiler> findByClienteId(Long clienteId);

    List<Alquiler> findByVehiculoId(Long vehiculoId);

    @Query("SELECT a FROM Alquiler a " +
            "LEFT JOIN FETCH a.cliente " +
            "LEFT JOIN FETCH a.vehiculo " +
            "LEFT JOIN FETCH a.usuario " +
            "WHERE a.id = :id")
    Optional<Alquiler> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT COUNT(a) FROM Alquiler a " +
            "WHERE a.vehiculo.id = :vehiculoId " +
            "AND a.estado = 'ACTIVO' " +
            "AND a.fechaInicio <= :fechaFin " +
            "AND a.fechaFinEstimada >= :fechaInicio")
    Long countAlquileresActivosEnPeriodo(
            @Param("vehiculoId") Long vehiculoId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    @Query("SELECT a FROM Alquiler a WHERE a.estado = 'ACTIVO' " +
            "AND a.fechaFinEstimada < :fecha")
    List<Alquiler> findAlquileresVencidos(@Param("fecha") LocalDateTime fecha);
}
