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
import pe.edu.cibertec.alquilape.model.dto.VehiculoDto;
import pe.edu.cibertec.alquilape.model.enums.EstadoVehiculo;
import pe.edu.cibertec.alquilape.model.enums.TipoVehiculo;
import pe.edu.cibertec.alquilape.service.VehiculoService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehiculos")
@RequiredArgsConstructor
@Tag(name = "Vehículos", description = "Gestión de vehículos")
@SecurityRequirement(name = "bearerAuth")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear vehículo", description = "Solo ADMIN puede crear vehículos")
    public ResponseEntity<VehiculoDto.VehiculoResponse> crear(
            @Valid @RequestBody VehiculoDto.VehiculoRequest request) {
        return new ResponseEntity<>(vehiculoService.crear(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ASISTENTE')")
    @Operation(summary = "Listar vehículos con filtros",
            description = "Todos los roles pueden listar vehículos")
    public ResponseEntity<PageResponse<VehiculoDto.VehiculoResponse>> listar(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) TipoVehiculo tipo,
            @RequestParam(required = false) EstadoVehiculo estado,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<VehiculoDto.VehiculoResponse> pageResult = vehiculoService.buscarConFiltros(
                marca, modelo, tipo, estado, precioMin, precioMax, anio, activo, pageable);

        PageResponse<VehiculoDto.VehiculoResponse> response = PageResponse.<VehiculoDto.VehiculoResponse>builder()
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

    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ASISTENTE')")
    @Operation(summary = "Obtener vehículos disponibles")
    public ResponseEntity<List<VehiculoDto.VehiculoResponse>> obtenerDisponibles() {
        return ResponseEntity.ok(vehiculoService.obtenerDisponibles());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ASISTENTE')")
    @Operation(summary = "Obtener vehículo por ID")
    public ResponseEntity<VehiculoDto.VehiculoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar vehículo", description = "Solo ADMIN")
    public ResponseEntity<VehiculoDto.VehiculoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VehiculoDto.VehiculoRequest request) {
        return ResponseEntity.ok(vehiculoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar vehículo", description = "Solo ADMIN (soft delete)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        vehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}