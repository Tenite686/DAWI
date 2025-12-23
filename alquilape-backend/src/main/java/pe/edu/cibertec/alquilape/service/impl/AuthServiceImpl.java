package pe.edu.cibertec.alquilape.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.alquilape.exception.BusinessException;
import pe.edu.cibertec.alquilape.exception.ResourceNotFoundException;
import pe.edu.cibertec.alquilape.model.dto.AuthDto;
import pe.edu.cibertec.alquilape.model.entity.Usuario;
import pe.edu.cibertec.alquilape.repository.UsuarioRepository;
import pe.edu.cibertec.alquilape.security.JwtService;
import pe.edu.cibertec.alquilape.service.AuthService;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        logger.debug("Intentando autenticar usuario: {}", request.getUsername());

        // Autenticar
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Buscar usuario
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario", "username", request.getUsername()));

        if (!usuario.getActivo()) {
            throw new BusinessException("El usuario está inactivo");
        }

        // Generar tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        logger.info("Usuario autenticado exitosamente: {}", request.getUsername());

        return AuthDto.LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol().name())
                .build();
    }

    @Override
    public AuthDto.LoginResponse refreshToken(AuthDto.RefreshTokenRequest request) {
        logger.debug("Intentando refrescar token");

        String username = jwtService.extractUsername(request.getRefreshToken());

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));

        if (!usuario.getActivo()) {
            throw new BusinessException("El usuario está inactivo");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtService.isTokenValid(request.getRefreshToken(), userDetails)) {
            throw new BusinessException("Token de refresco inválido o expirado");
        }

        String newToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        logger.info("Token refrescado exitosamente para usuario: {}", username);

        return AuthDto.LoginResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol().name())
                .build();
    }
}
