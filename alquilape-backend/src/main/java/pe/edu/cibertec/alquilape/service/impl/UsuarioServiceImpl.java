package pe.edu.cibertec.alquilape.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.alquilape.exception.BusinessException;
import pe.edu.cibertec.alquilape.exception.DuplicateResourceException;
import pe.edu.cibertec.alquilape.exception.ResourceNotFoundException;
import pe.edu.cibertec.alquilape.model.dto.UsuarioDto;
import pe.edu.cibertec.alquilape.model.entity.Usuario;
import pe.edu.cibertec.alquilape.model.enums.Rol;
import pe.edu.cibertec.alquilape.repository.UsuarioRepository;
import pe.edu.cibertec.alquilape.service.UsuarioService;

import java.util.List;

import static pe.edu.cibertec.alquilape.utils.MapperUtils.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;

    @Override
    public UsuarioDto.UsuarioResponse crear(UsuarioDto.UsuarioRequest request) {
        logger.debug("Creando usuario: {}", request.getUsername());

        // Validar username único
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Usuario", "username", request.getUsername());
        }

        // Validar email único
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Usuario", "email", request.getEmail());
        }

        // Validar que no se cree más de un ADMIN
        if (request.getRol() == Rol.ADMIN) {
            long adminCount = usuarioRepository.count();
            if (adminCount > 0) {
                // Verificar si ya existe al menos un ADMIN activo
                boolean existeAdminActivo = usuarioRepository.existsByRolAndActivo(Rol.ADMIN, true);
                if (existeAdminActivo) {
                    throw new BusinessException(
                            "Ya existe un usuario con rol ADMIN. Solo puede haber un administrador en el sistema.");
                }
            }
        }

        Usuario usuario = mapper.map(request, Usuario.class);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        Usuario saved = usuarioRepository.save(usuario);

        logger.info("Usuario creado exitosamente con ID: {}", saved.getId());

        return mapper.map(saved, UsuarioDto.UsuarioResponse.class);
    }


    @Override
    public UsuarioDto.UsuarioResponse actualizar(Long id, UsuarioDto.UsuarioUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // Validación de email único (solo si cambia y no es null)
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Usuario", "email", request.getEmail());
            }
            usuario.setEmail(request.getEmail());
        }

        // Actualizar campos solo si no son null
        updateIfNotNull(request.getNombreCompleto(), usuario::setNombreCompleto);
        //updateIfNotNull(request.getRol(), usuario::setRol);
        updateIfNotNull(request.getActivo(), usuario::setActivo);

        Usuario updated = usuarioRepository.save(usuario);
        logger.info("Usuario actualizado ID: {}", id);
        return mapper.map(updated, UsuarioDto.UsuarioResponse.class);
    }

    @Override
    public UsuarioDto.UsuarioResponse cambiarRol(Long id, Rol nuevoRol) {
        logger.debug("Cambiando rol del usuario ID: {} a {}", id, nuevoRol);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        Rol rolAnterior = usuario.getRol();

        // Validación 1: No se puede cambiar el rol si es el mismo
        if (rolAnterior == nuevoRol) {
            throw new BusinessException(
                    String.format("El usuario ya tiene el rol %s", nuevoRol.getDescripcion()));
        }

        // Validación 2: No se puede cambiar a ADMIN si ya existe otro ADMIN activo
        if (nuevoRol == Rol.ADMIN) {
            boolean existeOtroAdmin = usuarioRepository.existsByRolAndActivoAndIdNot(
                    Rol.ADMIN, true, id);

            if (existeOtroAdmin) {
                throw new BusinessException(
                        "No se puede asignar el rol ADMIN. Ya existe otro administrador activo en el sistema.");
            }
        }

        // Validación 3: No se puede quitar el rol ADMIN si es el último ADMIN activo
        if (rolAnterior == Rol.ADMIN && nuevoRol != Rol.ADMIN) {
            long adminCount = usuarioRepository.countByRolAndActivo(Rol.ADMIN, true);

            if (adminCount <= 1) {
                throw new BusinessException(
                        "No se puede cambiar el rol del último administrador activo. " +
                                "Debe existir al menos un ADMIN en el sistema.");
            }
        }

        usuario.setRol(nuevoRol);
        Usuario updated = usuarioRepository.save(usuario);

        logger.info("Rol del usuario ID: {} cambiado de {} a {}", id,
                rolAnterior.getDescripcion(), nuevoRol.getDescripcion());

        return mapper.map(updated, UsuarioDto.UsuarioResponse.class);
    }

    @Override
    public UsuarioDto.UsuarioResponse cambiarEstado(Long id, Boolean activo) {
        logger.debug("Cambiando estado del usuario ID: {} a {}", id, activo);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // Validación: No se puede desactivar el último ADMIN activo
        if (usuario.getRol() == Rol.ADMIN && !activo && usuario.getActivo()) {
            long adminActivoCount = usuarioRepository.countByRolAndActivo(Rol.ADMIN, true);

            if (adminActivoCount <= 1) {
                throw new BusinessException(
                        "No se puede desactivar el último administrador activo. " +
                                "Debe existir al menos un ADMIN activo en el sistema.");
            }
        }

        usuario.setActivo(activo);
        Usuario updated = usuarioRepository.save(usuario);

        logger.info("Estado del usuario ID: {} cambiado a {}", id, activo ? "ACTIVO" : "INACTIVO");

        return mapper.map(updated, UsuarioDto.UsuarioResponse.class);
    }

    @Override
    public void cambiarPassword(Long id, UsuarioDto.CambiarPasswordRequest request) {
        logger.debug("Cambiando contraseña del usuario ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }

        // Verificar que la nueva contraseña coincida con la confirmación
        if (!request.getPasswordNueva().equals(request.getPasswordConfirmacion())) {
            throw new BusinessException("La nueva contraseña y su confirmación no coinciden");
        }

        // Verificar que la nueva contraseña sea diferente a la actual
        if (passwordEncoder.matches(request.getPasswordNueva(), usuario.getPassword())) {
            throw new BusinessException("La nueva contraseña debe ser diferente a la actual");
        }

        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);

        logger.info("Contraseña cambiada exitosamente para usuario ID: {}", id);
    }

    @Override
    public void eliminar(Long id) {
        logger.debug("Eliminando usuario ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // Soft delete
        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        logger.info("Usuario eliminado (soft delete) ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDto.UsuarioResponse obtenerPorId(Long id) {
        logger.debug("Obteniendo usuario por ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        return mapper.map(usuario, UsuarioDto.UsuarioResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDto.UsuarioResponse> obtenerTodos() {
        logger.debug("Obteniendo todos los usuarios");

        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(usuario -> mapper.map(usuarios, UsuarioDto.UsuarioResponse.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioDto.UsuarioResponse> obtenerPaginado(Pageable pageable, String rol) {
        logger.debug("Obteniendo usuarios paginados");
        Page<Usuario> usuarios;

        // Si el rol no es nulo ni está vacío, filtramos usando el repositorio
        if (rol != null && !rol.trim().isEmpty()) {
            try {
                // Convertimos el String (ej: "ADMIN") al Enum Rol
                Rol rolEnum = Rol.valueOf(rol.toUpperCase());
                usuarios = usuarioRepository.findByRol(rolEnum, pageable);
            } catch (IllegalArgumentException e) {
                // Si el rol enviado no coincide con el Enum, traemos todos por defecto
                logger.warn("Rol inválido recibido: {}, se ignora el filtro", rol);
                usuarios = usuarioRepository.findAll(pageable);
            }
        } else {
            // Si no hay filtro (caso "Todos los roles"), traemos todos
            usuarios = usuarioRepository.findAll(pageable);
        }

        return usuarios.map(usuario -> mapper.map(usuario, UsuarioDto.UsuarioResponse.class));
    }
}