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
import pe.edu.cibertec.alquilape.model.dto.AlquilerDto;
import pe.edu.cibertec.alquilape.model.dto.ClienteDto;
import pe.edu.cibertec.alquilape.model.dto.PageResponse;
import pe.edu.cibertec.alquilape.model.enums.TipoCliente;
import pe.edu.cibertec.alquilape.service.ClienteService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestión de clientes")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Crear cliente")
    public ResponseEntity<ClienteDto.ClienteResponse> crear(
            @Valid @RequestBody ClienteDto.ClienteRequest request) {
        return new ResponseEntity<>(clienteService.crear(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Listar clientes con filtros")
    public ResponseEntity<PageResponse<ClienteDto.ClienteResponse>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String dniRuc,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) TipoCliente tipo,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ClienteDto.ClienteResponse> pageResult = clienteService.buscarConFiltros(
                nombre, apellido, dniRuc, email, tipo, activo, pageable);

        PageResponse<ClienteDto.ClienteResponse> response = PageResponse.<ClienteDto.ClienteResponse>builder()
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
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Obtener cliente por ID")
    public ResponseEntity<ClienteDto.ClienteResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    @GetMapping("/{id}/historial")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Obtener historial de alquileres del cliente")
    public ResponseEntity<List<AlquilerDto.AlquilerResponse>> obtenerHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerHistorialAlquileres(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Actualizar cliente")
    public ResponseEntity<ClienteDto.ClienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteDto.ClienteRequest request) {
        return ResponseEntity.ok(clienteService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar cliente (solo ADMIN)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}