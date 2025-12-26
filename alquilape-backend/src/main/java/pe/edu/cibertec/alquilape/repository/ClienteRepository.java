package pe.edu.cibertec.alquilape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.cibertec.alquilape.model.entity.Cliente;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>,
        JpaSpecificationExecutor<Cliente> {

    Optional<Cliente> findByDniRuc(String dniRuc);

    Optional<Cliente> findByEmail(String email);

    Boolean existsByDniRuc(String dniRuc);

    Boolean existsByEmail(String email);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.alquileres WHERE c.id = :id")
    Optional<Cliente> findByIdWithAlquileres(@Param("id") Long id);

    boolean existsByTelefono(String telefono);

    Optional<Cliente> findByLicenciaNumero(String licenciaNumero);

    boolean existsByLicenciaNumero(String licenciaNumero);
}
