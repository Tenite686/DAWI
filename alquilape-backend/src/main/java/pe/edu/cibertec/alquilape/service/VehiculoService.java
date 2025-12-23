package pe.edu.cibertec.alquilape.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.cibertec.alquilape.model.dto.VehiculoDto;
import pe.edu.cibertec.alquilape.model.enums.EstadoVehiculo;
import pe.edu.cibertec.alquilape.model.enums.TipoVehiculo;

import java.math.BigDecimal;
import java.util.List;
public interface VehiculoService {
    VehiculoDto.VehiculoResponse crear(VehiculoDto.VehiculoRequest request);
    VehiculoDto.VehiculoResponse actualizar(Long id, VehiculoDto.VehiculoRequest request);
    void eliminar(Long id);
    VehiculoDto.VehiculoResponse obtenerPorId(Long id);
    List<VehiculoDto.VehiculoResponse> obtenerTodos();
    Page<VehiculoDto.VehiculoResponse> obtenerPaginado(Pageable pageable);
    Page<VehiculoDto.VehiculoResponse> buscarConFiltros(
            String marca, String modelo, TipoVehiculo tipo, EstadoVehiculo estado,
            BigDecimal precioMin, BigDecimal precioMax, Integer anio, Boolean activo, Pageable pageable);
    List<VehiculoDto.VehiculoResponse> obtenerDisponibles();
}