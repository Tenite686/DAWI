package pe.edu.cibertec.alquilape.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import pe.edu.cibertec.alquilape.model.entity.Alquiler;
import pe.edu.cibertec.alquilape.model.enums.EstadoAlquiler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlquilerSpecification {

    public static Specification<Alquiler> conFiltros(
            EstadoAlquiler estado,
            Long clienteId,
            Long vehiculoId,
            Long usuarioId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Boolean activo
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (estado != null) {
                predicates.add(cb.equal(root.get("estado"), estado));
            }

            if (clienteId != null) {
                predicates.add(cb.equal(root.get("cliente").get("id"), clienteId));
            }

            if (vehiculoId != null) {
                predicates.add(cb.equal(root.get("vehiculo").get("id"), vehiculoId));
            }

            if (usuarioId != null) {
                predicates.add(cb.equal(root.get("usuario").get("id"), usuarioId));
            }

            if (fechaInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaInicio"), fechaInicio));
            }

            if (fechaFin != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaFinEstimada"), fechaFin));
            }

            if (activo != null) {
                predicates.add(cb.equal(root.get("activo"), activo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}