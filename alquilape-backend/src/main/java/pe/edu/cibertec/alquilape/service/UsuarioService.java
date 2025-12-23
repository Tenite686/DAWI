package pe.edu.cibertec.alquilape.service;

import pe.edu.cibertec.alquilape.model.dto.UsuarioDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.cibertec.alquilape.model.enums.Rol;

import java.util.List;

public interface UsuarioService {
    UsuarioDto.UsuarioResponse crear(UsuarioDto.UsuarioRequest request);
    UsuarioDto.UsuarioResponse actualizar(Long id, UsuarioDto.UsuarioUpdateRequest request);
    void eliminar(Long id);
    UsuarioDto.UsuarioResponse obtenerPorId(Long id);
    List<UsuarioDto.UsuarioResponse> obtenerTodos();
    Page<UsuarioDto.UsuarioResponse> obtenerPaginado(Pageable pageable);
    UsuarioDto.UsuarioResponse cambiarRol(Long id, Rol nuevoRol);
    UsuarioDto.UsuarioResponse cambiarEstado(Long id, Boolean activo);
    void cambiarPassword(Long id, UsuarioDto.CambiarPasswordRequest request);
}
