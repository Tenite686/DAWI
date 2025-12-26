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
import pe.edu.cibertec.alquilape.exception.ValidationException;
import pe.edu.cibertec.alquilape.model.dto.AlquilerDto;
import pe.edu.cibertec.alquilape.model.dto.ClienteDto;
import pe.edu.cibertec.alquilape.model.entity.Cliente;
import pe.edu.cibertec.alquilape.model.enums.TipoCliente;
import pe.edu.cibertec.alquilape.repository.ClienteRepository;
import pe.edu.cibertec.alquilape.service.ClienteService;
import pe.edu.cibertec.alquilape.specification.ClienteSpecification;

import java.time.LocalDate;
import java.util.List;

import static pe.edu.cibertec.alquilape.utils.MapperUtils.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteServiceImpl.class);

    private final ClienteRepository clienteRepository;
    private final ModelMapper mapper;

    @Override
    public ClienteDto.ClienteResponse crear(ClienteDto.ClienteRequest request) {
        logger.debug("Creando cliente: {}", request.getDniRuc());

        // Validar DNI/RUC único
        if (clienteRepository.existsByDniRuc(request.getDniRuc())) {
            throw new DuplicateResourceException("Cliente", "DNI/RUC", request.getDniRuc());
        }

        // Validar email único
        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Cliente", "email", request.getEmail());
        }
        // Validar teléfono único
        if (request.getTelefono() != null && clienteRepository.existsByTelefono((request.getTelefono()))) {
            throw new DuplicateResourceException("Cliente", "teléfono", request.getTelefono());
        }

        // Validar licencia vigente
        validarLicencia(request.getLicenciaNumero(), request.getLicenciaVencimiento(), null);

        Cliente cliente = mapper.map(request, Cliente.class);
        Cliente saved = clienteRepository.save(cliente);

        logger.info("Cliente creado exitosamente con ID: {}", saved.getId());

        return mapper.map(saved, ClienteDto.ClienteResponse.class);
    }

    @Override
    public ClienteDto.ClienteResponse actualizar(Long id, ClienteDto.ClienteRequest request) {
        logger.debug("Actualizando cliente ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        // Validar DNI/RUC único si se está cambiando
        if (!request.getDniRuc().equals(cliente.getDniRuc()) &&
                clienteRepository.existsByDniRuc(request.getDniRuc())) {
            throw new DuplicateResourceException("Cliente", "DNI/RUC", request.getDniRuc());
        }

        // Validar email único si se está cambiando
        if (!request.getEmail().equals(cliente.getEmail()) &&
                clienteRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Cliente", "email", request.getEmail());
        }

        // Validar licencia vigente
        validarLicencia(request.getLicenciaNumero(), request.getLicenciaVencimiento(), id);

        updateIfNotNull(request.getNombre(), cliente::setNombre);
        updateIfNotNull(request.getApellido(), cliente::setApellido);
        updateIfNotNull(request.getDniRuc(), cliente::setDniRuc);
        updateIfNotNull(request.getEmail(), cliente::setEmail);
        updateIfNotNull(request.getTelefono(), cliente::setTelefono);
        updateIfNotNull(request.getDireccion(), cliente::setDireccion);
        updateIfNotNull(request.getTipo(), cliente::setTipo);
        updateIfNotNull(request.getLicenciaNumero(), cliente::setLicenciaNumero);
        updateIfNotNull(request.getLicenciaVencimiento(), cliente::setLicenciaVencimiento);
        updateIfNotNull(request.getActivo(), cliente::setActivo);

        //mapper.map(request, Cliente.class);
        Cliente updated = clienteRepository.save(cliente);

        logger.info("Cliente actualizado exitosamente ID: {}", id);

        return mapper.map(updated, ClienteDto.ClienteResponse.class);
    }

    @Override
    public void eliminar(Long id) {
        logger.debug("Eliminando cliente ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        // Validar que no tenga alquileres activos
        boolean tieneAlquileresActivos = cliente.getAlquileres().stream()
                .anyMatch(a -> a.getEstado().name().equals("ACTIVO"));

        if (tieneAlquileresActivos) {
            throw new BusinessException(
                    "No se puede eliminar el cliente porque tiene alquileres activos");
        }

        // Soft delete
        cliente.setActivo(false);
        clienteRepository.save(cliente);

        logger.info("Cliente eliminado (soft delete) ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDto.ClienteResponse obtenerPorId(Long id) {
        logger.debug("Obteniendo cliente por ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        return mapper.map(cliente, ClienteDto.ClienteResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDto.ClienteResponse> obtenerTodos() {
        logger.debug("Obteniendo todos los clientes");

        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(cliente -> mapper.map(clientes, ClienteDto.ClienteResponse.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteDto.ClienteResponse> obtenerPaginado(Pageable pageable) {
        logger.debug("Obteniendo clientes paginados");

        Page<Cliente> clientes = clienteRepository.findAll(pageable);
        return clientes.map(cliente -> mapper.map(cliente, ClienteDto.ClienteResponse.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteDto.ClienteResponse> buscarConFiltros(
            String nombre, String apellido, String dniRuc,
            String email, TipoCliente tipo, Boolean activo, Pageable pageable) {

        logger.debug("Buscando clientes con filtros");

        Page<Cliente> clientes = clienteRepository.findAll(
                ClienteSpecification.conFiltros(nombre, apellido, dniRuc, email, tipo, activo),
                pageable
        );

        return clientes.map(cliente -> mapper.map(cliente, ClienteDto.ClienteResponse.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlquilerDto.AlquilerResponse> obtenerHistorialAlquileres(Long id) {
        logger.debug("Obteniendo historial de alquileres del cliente ID: {}", id);

        Cliente cliente = clienteRepository.findByIdWithAlquileres(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        return cliente.getAlquileres().stream()
                .map(alquiler -> mapper.map(alquiler, AlquilerDto.AlquilerResponse.class))
                .toList();

    }

    private void validarLicencia(String numero, LocalDate vencimiento, Long idActual) {
        if (numero != null && !numero.isBlank()) {
            // Validar Duplicidad en BD
            clienteRepository.findByLicenciaNumero(numero).ifPresent(existente -> {
                // Si el cliente encontrado tiene un ID diferente al que estamos procesando, es duplicado
                if (idActual == null || !existente.getId().equals(idActual)) {
                    throw new DuplicateResourceException("Cliente", "número de licencia", numero);
                }
            });
            //Validar Vencimiento (tu lógica original)
            if (vencimiento == null) {
                throw new ValidationException("Si proporciona número de licencia, debe indicar fecha de vencimiento");
            }
            if (vencimiento.isBefore(LocalDate.now())) {
                throw new ValidationException("La licencia de conducir está vencida");
            }
        }
    }
}