package pe.edu.cibertec.alquilape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.alquilape.model.dto.PageResponse;
import pe.edu.cibertec.alquilape.model.dto.UsuarioDto;
import pe.edu.cibertec.alquilape.service.UsuarioService;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario (solo ADMIN)")
    public ResponseEntity<UsuarioDto.UsuarioResponse> crear(
            @Valid @RequestBody UsuarioDto.UsuarioRequest request) {
        return new ResponseEntity<>(usuarioService.crear(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuarios", description = "Obtiene lista paginada de usuarios")
    public ResponseEntity<PageResponse<UsuarioDto.UsuarioResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) String rol) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<UsuarioDto.UsuarioResponse> pageResult = usuarioService.obtenerPaginado(pageable, rol);

        PageResponse<UsuarioDto.UsuarioResponse> response = PageResponse.<UsuarioDto.UsuarioResponse>builder()
                .content(pageResult.getContent())
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .first(pageResult.isFirst())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UsuarioDto.UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar usuario")
    public ResponseEntity<UsuarioDto.UsuarioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDto.UsuarioUpdateRequest request) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }

    @PatchMapping("/{id}/cambiar-rol")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar rol de usuario",
            description = "Permite al ADMIN cambiar el rol de un usuario")
    public ResponseEntity<UsuarioDto.UsuarioResponse> cambiarRol(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDto.CambiarRolRequest request) {
        return ResponseEntity.ok(usuarioService.cambiarRol(id, request.getNuevoRol()));
    }

    @PatchMapping("/{id}/cambiar-estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar/Desactivar usuario",
            description = "Permite al ADMIN activar o desactivar un usuario")
    public ResponseEntity<UsuarioDto.UsuarioResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean activo) {
        return ResponseEntity.ok(usuarioService.cambiarEstado(id, activo));
    }

    @PatchMapping("/{id}/cambiar-password")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar contraseña",
            description = "Permite cambiar la contraseña de un usuario")
    public ResponseEntity<Void> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDto.CambiarPasswordRequest request) {
        usuarioService.cambiarPassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario (soft delete)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
