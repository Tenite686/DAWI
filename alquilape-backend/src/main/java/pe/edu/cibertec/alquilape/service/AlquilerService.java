package pe.edu.cibertec.alquilape.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.cibertec.alquilape.model.dto.AlquilerDto;
import pe.edu.cibertec.alquilape.model.enums.EstadoAlquiler;

import java.time.LocalDateTime;
import java.util.List;

public interface AlquilerService {
    AlquilerDto.AlquilerResponse crear(AlquilerDto.AlquilerRequest request);
    AlquilerDto.AlquilerResponse registrarDevolucion(Long id, AlquilerDto.AlquilerDevolucionRequest request);
    AlquilerDto.AlquilerResponse cancelar(Long id);
    void eliminar(Long id);
    AlquilerDto.AlquilerResponse obtenerPorId(Long id);
    List<AlquilerDto.AlquilerResponse> obtenerTodos();
    Page<AlquilerDto.AlquilerResponse> obtenerPaginado(Pageable pageable);
    Page<AlquilerDto.AlquilerResponse> buscarConFiltros(
            EstadoAlquiler estado, Long clienteId, Long vehiculoId, Long usuarioId,
            LocalDateTime fechaInicio, LocalDateTime fechaFin, Boolean activo, Pageable pageable);
}
