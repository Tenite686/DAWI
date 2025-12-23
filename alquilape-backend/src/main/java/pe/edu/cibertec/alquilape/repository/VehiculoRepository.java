package pe.edu.cibertec.alquilape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.cibertec.alquilape.model.entity.Vehiculo;
import pe.edu.cibertec.alquilape.model.enums.EstadoVehiculo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long>,
        JpaSpecificationExecutor<Vehiculo> {

    Optional<Vehiculo> findByPlaca(String placa);

    Boolean existsByPlaca(String placa);

    List<Vehiculo> findByEstado(EstadoVehiculo estado);

    @Query("SELECT v FROM Vehiculo v WHERE v.estado = :estado AND v.activo = true")
    List<Vehiculo> findDisponibles(@Param("estado") EstadoVehiculo estado);

    @Query("""
        SELECT v FROM Vehiculo v 
        WHERE v.id = :vehiculoId 
        AND v.estado = 'DISPONIBLE' 
        AND NOT EXISTS (
            SELECT a FROM Alquiler a 
            WHERE a.vehiculo.id = :vehiculoId 
            AND a.estado = 'ACTIVO' 
            AND (
                (a.fechaInicio <= :fechaFin AND a.fechaFinEstimada >= :fechaInicio)
            )
        )
    """)
    Optional<Vehiculo> findDisponibleEnPeriodo(
            @Param("vehiculoId") Long vehiculoId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
}