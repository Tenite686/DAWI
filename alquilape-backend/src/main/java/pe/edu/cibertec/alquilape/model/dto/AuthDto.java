package pe.edu.cibertec.alquilape.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

public class AuthDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "El username es obligatorio")
        private String username;

        @NotBlank(message = "El password es obligatorio")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginResponse {
        private String token;
        private String refreshToken;
        private String tipo = "Bearer";
        private Long id;
        private String username;
        private String email;
        private String nombreCompleto;
        private String rol;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenRequest {
        @NotBlank(message = "El refresh token es obligatorio")
        private String refreshToken;
    }
}