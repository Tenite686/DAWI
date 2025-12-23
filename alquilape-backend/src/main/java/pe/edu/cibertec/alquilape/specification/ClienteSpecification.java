package pe.edu.cibertec.alquilape.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import pe.edu.cibertec.alquilape.model.entity.Cliente;
import pe.edu.cibertec.alquilape.model.enums.TipoCliente;

import java.util.ArrayList;
import java.util.List;

public class ClienteSpecification {

    public static Specification<Cliente> conFiltros(
            String nombre,
            String apellido,
            String dniRuc,
            String email,
            TipoCliente tipo,
            Boolean activo
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nombre != null && !nombre.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nombre")),
                        "%" + nombre.toLowerCase() + "%"));
            }

            if (apellido != null && !apellido.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("apellido")),
                        "%" + apellido.toLowerCase() + "%"));
            }

            if (dniRuc != null && !dniRuc.isBlank()) {
                predicates.add(cb.like(root.get("dniRuc"), "%" + dniRuc + "%"));
            }

            if (email != null && !email.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("email")),
                        "%" + email.toLowerCase() + "%"));
            }

            if (tipo != null) {
                predicates.add(cb.equal(root.get("tipo"), tipo));
            }

            if (activo != null) {
                predicates.add(cb.equal(root.get("activo"), activo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}