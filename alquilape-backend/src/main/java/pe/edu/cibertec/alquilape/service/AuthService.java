package pe.edu.cibertec.alquilape.service;

import pe.edu.cibertec.alquilape.model.dto.AuthDto;

public interface AuthService {
    AuthDto.LoginResponse login(AuthDto.LoginRequest request);
    AuthDto.LoginResponse refreshToken(AuthDto.RefreshTokenRequest request);
}