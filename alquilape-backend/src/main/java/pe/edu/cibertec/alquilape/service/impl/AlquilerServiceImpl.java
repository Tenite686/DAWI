package pe.edu.cibertec.alquilape.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.alquilape.exception.BusinessException;
import pe.edu.cibertec.alquilape.exception.ResourceNotFoundException;
import pe.edu.cibertec.alquilape.exception.ValidationException;
import pe.edu.cibertec.alquilape.model.dto.AlquilerDto;
import pe.edu.cibertec.alquilape.model.entity.Alquiler;
import pe.edu.cibertec.alquilape.model.entity.Cliente;
import pe.edu.cibertec.alquilape.model.entity.Usuario;
import pe.edu.cibertec.alquilape.model.entity.Vehiculo;
import pe.edu.cibertec.alquilape.model.enums.EstadoAlquiler;
import pe.edu.cibertec.alquilape.model.enums.EstadoVehiculo;
import pe.edu.cibertec.alquilape.repository.AlquilerRepository;
import pe.edu.cibertec.alquilape.repository.ClienteRepository;
import pe.edu.cibertec.alquilape.repository.UsuarioRepository;
import pe.edu.cibertec.alquilape.repository.VehiculoRepository;
import pe.edu.cibertec.alquilape.service.AlquilerService;
import pe.edu.cibertec.alquilape.specification.AlquilerSpecification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class AlquilerServiceImpl implements AlquilerService {

    private static final Logger logger = LoggerFactory.getLogger(AlquilerServiceImpl.class);

    private final AlquilerRepository alquilerRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper mapper;

    @Override
    public AlquilerDto.AlquilerResponse crear(AlquilerDto.AlquilerRequest request) {
        logger.debug("Creando nuevo alquiler");

        // Validar fechas
        validarFechas(request.getFechaInicio(), request.getFechaFinEstimada());

        // Obtener cliente y validar
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.getClienteId()));

        if (!cliente.getActivo()) {
            throw new BusinessException("El cliente está inactivo");
        }

        // Validar licencia vigente
        if (cliente.getLicenciaVencimiento() == null ||
                cliente.getLicenciaVencimiento().isBefore(LocalDate.now())) {
            throw new ValidationException("El cliente no tiene licencia de conducir vigente");
        }

        // Obtener vehículo y validar disponibilidad
        Vehiculo vehiculo = vehiculoRepository.findById(request.getVehiculoId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo", "id", request.getVehiculoId()));

        if (!vehiculo.getActivo()) {
            throw new BusinessException("El vehículo está inactivo");
        }

        if (vehiculo.getEstado() != EstadoVehiculo.DISPONIBLE) {
            throw new BusinessException("El vehículo no está disponible");
        }

        // Validar que no haya solapamiento con otros alquileres
        Long alquileresEnPeriodo = alquilerRepository.countAlquileresActivosEnPeriodo(
                vehiculo.getId(),
                request.getFechaInicio(),
                request.getFechaFinEstimada()
        );

        if (alquileresEnPeriodo > 0) {
            throw new BusinessException(
                    "El vehículo ya está alquilado en el período solicitado");
        }

        // Validar kilometraje
        if (request.getKilometrajeInicio() < vehiculo.getKilometraje()) {
            throw new ValidationException(
                    "El kilometraje inicial no puede ser menor al kilometraje actual del vehículo");
        }

        // Obtener usuario actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", auth.getName()));

        // Crear alquiler
        Alquiler alquiler = mapper.map(request, Alquiler.class);
        alquiler.setCliente(cliente);
        alquiler.setVehiculo(vehiculo);
        alquiler.setUsuario(usuario);

        // Calcular precio total
        BigDecimal precioTotal = calcularPrecioTotal(
                vehiculo.getPrecioPorDia(),
                request.getFechaInicio(),
                request.getFechaFinEstimada()
        );
        alquiler.setPrecioTotal(precioTotal);

        // Cambiar estado del vehículo a ALQUILADO
        vehiculo.setEstado(EstadoVehiculo.ALQUILADO);
        vehiculoRepository.save(vehiculo);

        Alquiler saved = alquilerRepository.save(alquiler);

        logger.info("Alquiler creado exitosamente con ID: {}", saved.getId());

        // Recuperar con todos los detalles para la respuesta
        Alquiler complete = alquilerRepository.findByIdWithDetails(saved.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Alquiler", "id", saved.getId()));

        return mapper.map(complete, AlquilerDto.AlquilerResponse.class);
    }

    @Override
    public AlquilerDto.AlquilerResponse registrarDevolucion(
            Long id, AlquilerDto.AlquilerDevolucionRequest request) {

        logger.debug("Registrando devolución de alquiler ID: {}", id);

        Alquiler alquiler = alquilerRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alquiler", "id", id));

        // Validar estado
        if (alquiler.getEstado() != EstadoAlquiler.ACTIVO) {
            throw new BusinessException("El alquiler no está activo");
        }

        // Validar fecha de devolución
        if (request.getFechaDevolucion().isBefore(alquiler.getFechaInicio())) {
            throw new ValidationException(
                    "La fecha de devolución no puede ser anterior a la fecha de inicio");
        }

        // Validar kilometraje
        if (request.getKilometrajeFin() < alquiler.getKilometrajeInicio()) {
            throw new ValidationException(
                    "El kilometraje final no puede ser menor al kilometraje inicial");
        }

        // Registrar devolución
        alquiler.setFechaDevolucionReal(request.getFechaDevolucion());
        alquiler.setKilometrajeFin(request.getKilometrajeFin());
        alquiler.setEstado(EstadoAlquiler.COMPLETADO);

        if (request.getObservaciones() != null) {
            String observacionesActuales = alquiler.getObservaciones() != null
                    ? alquiler.getObservaciones() + "\n"
                    : "";
            alquiler.setObservaciones(observacionesActuales + request.getObservaciones());
        }

        // Actualizar vehículo
        Vehiculo vehiculo = alquiler.getVehiculo();
        vehiculo.setEstado(EstadoVehiculo.DISPONIBLE);
        vehiculo.setKilometraje(request.getKilometrajeFin());
        vehiculoRepository.save(vehiculo);

        // Recalcular precio si hay retraso
        if (request.getFechaDevolucion().isAfter(alquiler.getFechaFinEstimada())) {
            BigDecimal precioAdicional = calcularPrecioTotal(
                    vehiculo.getPrecioPorDia(),
                    alquiler.getFechaFinEstimada(),
                    request.getFechaDevolucion()
            );
            alquiler.setPrecioTotal(alquiler.getPrecioTotal().add(precioAdicional));

            logger.info("Cargo adicional por retraso: {}", precioAdicional);
        }

        Alquiler updated = alquilerRepository.save(alquiler);

        logger.info("Devolución registrada exitosamente para alquiler ID: {}", id);

        return mapper.map(updated, AlquilerDto.AlquilerResponse.class);
    }

    @Override
    public AlquilerDto.AlquilerResponse cancelar(Long id) {
        logger.debug("Cancelando alquiler ID: {}", id);

        Alquiler alquiler = alquilerRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alquiler", "id", id));

        // Validar estado
        if (alquiler.getEstado() != EstadoAlquiler.ACTIVO) {
            throw new BusinessException("Solo se pueden cancelar alquileres activos");
        }

        // Cambiar estado
        alquiler.setEstado(EstadoAlquiler.CANCELADO);

        // Liberar vehículo
        Vehiculo vehiculo = alquiler.getVehiculo();
        vehiculo.setEstado(EstadoVehiculo.DISPONIBLE);
        vehiculoRepository.save(vehiculo);

        Alquiler updated = alquilerRepository.save(alquiler);

        logger.info("Alquiler cancelado exitosamente ID: {}", id);

        return mapper.map(updated, AlquilerDto.AlquilerResponse.class);
    }

    @Override
    public void eliminar(Long id) {
        logger.debug("Eliminando alquiler ID: {}", id);

        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alquiler", "id", id));

        // Solo se pueden eliminar alquileres cancelados
        if (alquiler.getEstado() == EstadoAlquiler.ACTIVO) {
            throw new BusinessException(
                    "No se puede eliminar un alquiler activo. Debe cancelarlo primero");
        }

        // Soft delete
        alquiler.setActivo(false);
        alquilerRepository.save(alquiler);

        logger.info("Alquiler eliminado (soft delete) ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public AlquilerDto.AlquilerResponse obtenerPorId(Long id) {
        logger.debug("Obteniendo alquiler por ID: {}", id);

        Alquiler alquiler = alquilerRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alquiler", "id", id));

        return mapper.map(alquiler, AlquilerDto.AlquilerResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlquilerDto.AlquilerResponse> obtenerTodos() {
        logger.debug("Obteniendo todos los alquileres");

        List<Alquiler> alquileres = alquilerRepository.findAll();
        return alquileres.stream()
                .map(alquiler -> mapper.map(alquiler, AlquilerDto.AlquilerResponse.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlquilerDto.AlquilerResponse> obtenerPaginado(Pageable pageable) {
        logger.debug("Obteniendo alquileres paginados");

        Page<Alquiler> alquileres = alquilerRepository.findAll(pageable);
        return alquileres.map(alquiler -> mapper.map(alquiler, AlquilerDto.AlquilerResponse.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlquilerDto.AlquilerResponse> buscarConFiltros(
            EstadoAlquiler estado, Long clienteId, Long vehiculoId, Long usuarioId,
            LocalDateTime fechaInicio, LocalDateTime fechaFin, Boolean activo,
            Pageable pageable) {

        logger.debug("Buscando alquileres con filtros");

        Page<Alquiler> alquileres = alquilerRepository.findAll(
                AlquilerSpecification.conFiltros(
                        estado, clienteId, vehiculoId, usuarioId, fechaInicio, fechaFin, activo),
                pageable
        );

        return alquileres.map(alquiler -> mapper.map(alquiler, AlquilerDto.AlquilerResponse.class));
    }

    // Métodos auxiliares

    private void validarFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        LocalDateTime ahora = LocalDateTime.now();

        if (fechaInicio.isBefore(ahora)) {
            throw new ValidationException("La fecha de inicio no puede ser en el pasado");
        }

        if (fechaFin.isBefore(fechaInicio)) {
            throw new ValidationException(
                    "La fecha fin no puede ser anterior a la fecha de inicio");
        }

        long dias = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        if (dias < 1) {
            throw new ValidationException("El alquiler debe ser de al menos 1 día");
        }
    }

    private BigDecimal calcularPrecioTotal(
            BigDecimal precioPorDia,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        long dias = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        if (dias < 1) {
            dias = 1; // Mínimo 1 día
        }

        return precioPorDia.multiply(BigDecimal.valueOf(dias));
    }
}

