package pe.edu.cibertec.alquilape.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import pe.edu.cibertec.alquilape.model.entity.Vehiculo;
import pe.edu.cibertec.alquilape.model.enums.EstadoVehiculo;
import pe.edu.cibertec.alquilape.model.enums.TipoVehiculo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VehiculoEspecification {

    public static Specification<Vehiculo> conFiltros(
            String marca,
            String modelo,
            TipoVehiculo tipo,
            EstadoVehiculo estado,
            BigDecimal precioMin,
            BigDecimal precioMax,
            Integer anio,
            Boolean activo
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (marca != null && !marca.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("marca")),
                        "%" + marca.toLowerCase() + "%"));
            }

            if (modelo != null && !modelo.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("modelo")),
                        "%" + modelo.toLowerCase() + "%"));
            }

            if (tipo != null) {
                predicates.add(cb.equal(root.get("tipo"), tipo));
            }

            if (estado != null) {
                predicates.add(cb.equal(root.get("estado"), estado));
            }

            if (precioMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("precioPorDia"), precioMin));
            }

            if (precioMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("precioPorDia"), precioMax));
            }

            if (anio != null) {
                predicates.add(cb.equal(root.get("anio"), anio));
            }

            if (activo != null) {
                predicates.add(cb.equal(root.get("activo"), activo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
