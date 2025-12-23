package pe.edu.cibertec.alquilape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.cibertec.alquilape.model.entity.Pago;
import pe.edu.cibertec.alquilape.model.enums.EstadoPago;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long>,
        JpaSpecificationExecutor<Pago> {

    List<Pago> findByAlquilerId(Long alquilerId);

    List<Pago> findByEstado(EstadoPago estado);

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.alquiler.id = :alquilerId AND p.estado = 'PAGADO'")
    BigDecimal calcularTotalPagadoPorAlquiler(@Param("alquilerId") Long alquilerId);
}