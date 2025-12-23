package pe.edu.cibertec.alquilape.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.cibertec.alquilape.model.dto.AlquilerDto;
import pe.edu.cibertec.alquilape.model.dto.ClienteDto;
import pe.edu.cibertec.alquilape.model.enums.TipoCliente;

import java.util.List;

public interface ClienteService {
    ClienteDto.ClienteResponse crear(ClienteDto.ClienteRequest request);
    ClienteDto.ClienteResponse actualizar(Long id, ClienteDto.ClienteRequest request);
    void eliminar(Long id);
    ClienteDto.ClienteResponse obtenerPorId(Long id);
    List<ClienteDto.ClienteResponse> obtenerTodos();
    Page<ClienteDto.ClienteResponse> obtenerPaginado(Pageable pageable);
    Page<ClienteDto.ClienteResponse> buscarConFiltros(
            String nombre, String apellido, String dniRuc,
            String email, TipoCliente tipo, Boolean activo, Pageable pageable);
    List<AlquilerDto.AlquilerResponse> obtenerHistorialAlquileres(Long id);
}