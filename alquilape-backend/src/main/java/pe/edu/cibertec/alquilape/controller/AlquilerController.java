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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.alquilape.model.dto.AlquilerDto;
import pe.edu.cibertec.alquilape.model.dto.PageResponse;
import pe.edu.cibertec.alquilape.model.enums.EstadoAlquiler;
import pe.edu.cibertec.alquilape.service.AlquilerService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/alquileres")
@RequiredArgsConstructor
@Tag(name = "Alquileres", description = "Gestión de alquileres de vehículos")
@SecurityRequirement(name = "bearerAuth")
public class AlquilerController {

    private final AlquilerService alquilerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Crear nuevo alquiler",
            description = "ADMIN y SUPERVISOR pueden crear alquileres")
    public ResponseEntity<AlquilerDto.AlquilerResponse> crear(
            @Valid @RequestBody AlquilerDto.AlquilerRequest request) {
        return new ResponseEntity<>(alquilerService.crear(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Listar alquileres con filtros")
    public ResponseEntity<PageResponse<AlquilerDto.AlquilerResponse>> listar(
            @RequestParam(required = false) EstadoAlquiler estado,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long vehiculoId,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<AlquilerDto.AlquilerResponse> pageResult = alquilerService.buscarConFiltros(
                estado, clienteId, vehiculoId, usuarioId, fechaInicio, fechaFin, activo, pageable);

        PageResponse<AlquilerDto.AlquilerResponse> response = PageResponse.<AlquilerDto.AlquilerResponse>builder()
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
    @Operation(summary = "Obtener alquiler por ID")
    public ResponseEntity<AlquilerDto.AlquilerResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.obtenerPorId(id));
    }

    @PutMapping("/{id}/devolver")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Registrar devolución de vehículo",
            description = "ADMIN y SUPERVISOR pueden registrar devoluciones")
    public ResponseEntity<AlquilerDto.AlquilerResponse> registrarDevolucion(
            @PathVariable Long id,
            @Valid @RequestBody AlquilerDto.AlquilerDevolucionRequest request) {
        return ResponseEntity.ok(alquilerService.registrarDevolucion(id, request));
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Cancelar alquiler",
            description = "ADMIN y SUPERVISOR pueden cancelar alquileres")
    public ResponseEntity<AlquilerDto.AlquilerResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.cancelar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar alquiler", description = "Solo ADMIN (soft delete)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alquilerService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}