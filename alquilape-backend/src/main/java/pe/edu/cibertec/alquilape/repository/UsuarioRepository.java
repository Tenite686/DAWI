package pe.edu.cibertec.alquilape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pe.edu.cibertec.alquilape.model.entity.Usuario;
import pe.edu.cibertec.alquilape.model.enums.Rol;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>,
        JpaSpecificationExecutor<Usuario> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    //Métodos para validar roles
    Boolean existsByRolAndActivo(Rol rol, Boolean activo);

    Boolean existsByRolAndActivoAndIdNot(Rol rol, Boolean activo, Long id);

    Long countByRolAndActivo(Rol rol, Boolean activo);
}