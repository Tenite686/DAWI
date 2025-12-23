package pe.edu.cibertec.alquilape.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.alquilape.exception.BusinessException;
import pe.edu.cibertec.alquilape.exception.DuplicateResourceException;
import pe.edu.cibertec.alquilape.exception.ResourceNotFoundException;
import pe.edu.cibertec.alquilape.model.dto.VehiculoDto;
import pe.edu.cibertec.alquilape.model.entity.Vehiculo;
import pe.edu.cibertec.alquilape.model.enums.EstadoVehiculo;
import pe.edu.cibertec.alquilape.model.enums.TipoVehiculo;
import pe.edu.cibertec.alquilape.repository.VehiculoRepository;
import pe.edu.cibertec.alquilape.service.VehiculoService;
import pe.edu.cibertec.alquilape.specification.VehiculoEspecification;

import java.math.BigDecimal;
import java.util.List;

import static pe.edu.cibertec.alquilape.utils.MapperUtils.*;

@Service
@RequiredArgsConstructor
@Transactional
public class VehiculoServiceImpl implements VehiculoService {

    private static final Logger logger = LoggerFactory.getLogger(VehiculoServiceImpl.class);

    private final VehiculoRepository vehiculoRepository;
    private final ModelMapper mapper;

    @Override
    public VehiculoDto.VehiculoResponse crear(VehiculoDto.VehiculoRequest request) {
        logger.debug("Creando vehículo: {}", request.getPlaca());

        // Validar placa única
        if (vehiculoRepository.existsByPlaca(request.getPlaca())) {
            throw new DuplicateResourceException("Vehículo", "placa", request.getPlaca());
        }

        Vehiculo vehiculo = mapper.map(request, Vehiculo.class);

        // Si no se especifica estado, asignar DISPONIBLE
        if (vehiculo.getEstado() == null) {
            vehiculo.setEstado(EstadoVehiculo.DISPONIBLE);
        }

        Vehiculo saved = vehiculoRepository.save(vehiculo);

        logger.info("Vehículo creado exitosamente con ID: {}", saved.getId());

        return mapper.map(saved, VehiculoDto.VehiculoResponse.class);
    }

    @Override
    public VehiculoDto.VehiculoResponse actualizar(Long id, VehiculoDto.VehiculoRequest request) {
        logger.debug("Actualizando vehículo ID: {}", id);

        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo", "id", id));

        // Validar placa única si se está cambiando
        if (!request.getPlaca().equals(vehiculo.getPlaca()) &&
                vehiculoRepository.existsByPlaca(request.getPlaca())) {
            throw new DuplicateResourceException("Vehículo", "placa", request.getPlaca());
        }

        updateIfNotNull(request.getMarca(), vehiculo::setMarca);
        updateIfNotNull(request.getModelo(), vehiculo::setModelo);
        updateIfNotNull(request.getAnio(), vehiculo::setAnio);
        updateIfNotNull(request.getPlaca(), vehiculo::setPlaca);
        updateIfNotNull(request.getColor(), vehiculo::setColor);
        updateIfNotNull(request.getTipo(), vehiculo::setTipo);
        updateIfNotNull(request.getEstado(), vehiculo::setEstado);
        updateIfNotNull(request.getPrecioPorDia(), vehiculo::setPrecioPorDia);
        updateIfNotNull(request.getKilometraje(), vehiculo::setKilometraje);
        updateIfNotNull(request.getCapacidadPasajeros(), vehiculo::setCapacidadPasajeros);
        updateIfNotNull(request.getCaracteristicasAdicionales(), vehiculo::setCaracteristicasAdicionales);

        Vehiculo updated = vehiculoRepository.save(vehiculo);

        logger.info("Vehículo actualizado exitosamente ID: {}", id);

        return mapper.map(updated, VehiculoDto.VehiculoResponse.class);
    }

    @Override
    public void eliminar(Long id) {
        logger.debug("Eliminando vehículo ID: {}", id);

        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo", "id", id));

        // Validar que no esté alquilado
        if (vehiculo.getEstado() == EstadoVehiculo.ALQUILADO) {
            throw new BusinessException(
                    "No se puede eliminar el vehículo porque está actualmente alquilado");
        }

        // Soft delete
        vehiculo.setActivo(false);
        vehiculoRepository.save(vehiculo);
        vehiculo.setEstado(EstadoVehiculo.INACTIVO);

        logger.info("Vehículo eliminado (soft delete) ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculoDto.VehiculoResponse obtenerPorId(Long id) {
        logger.debug("Obteniendo vehículo por ID: {}", id);

        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo", "id", id));

        return mapper.map(vehiculo, VehiculoDto.VehiculoResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoDto.VehiculoResponse> obtenerTodos() {
        logger.debug("Obteniendo todos los vehículos");

        List<Vehiculo> vehiculos = vehiculoRepository.findAll();
        return vehiculos.stream()
                .map (vehiculo -> mapper.map(vehiculo, VehiculoDto.VehiculoResponse.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehiculoDto.VehiculoResponse> obtenerPaginado(Pageable pageable) {
        logger.debug("Obteniendo vehículos paginados");

        Page<Vehiculo> vehiculos = vehiculoRepository.findAll(pageable);
        return vehiculos.map(vehiculo -> mapper.map(vehiculo, VehiculoDto.VehiculoResponse.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehiculoDto.VehiculoResponse> buscarConFiltros(
            String marca, String modelo, TipoVehiculo tipo, EstadoVehiculo estado,
            BigDecimal precioMin, BigDecimal precioMax, Integer anio, Boolean activo,
            Pageable pageable) {

        logger.debug("Buscando vehículos con filtros");

        Page<Vehiculo> vehiculos = vehiculoRepository.findAll(
                VehiculoEspecification.conFiltros(
                        marca, modelo, tipo, estado, precioMin, precioMax, anio, activo),
                pageable
        );

        return vehiculos.map(vehiculo -> mapper.map(vehiculo, VehiculoDto.VehiculoResponse.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoDto.VehiculoResponse> obtenerDisponibles() {
        logger.debug("Obteniendo vehículos disponibles");

        List<Vehiculo> vehiculos = vehiculoRepository.findDisponibles(EstadoVehiculo.DISPONIBLE);
        return vehiculos.stream()
                .map (vehiculo -> mapper.map(vehiculo, VehiculoDto.VehiculoResponse.class))
                .toList();
    }
}