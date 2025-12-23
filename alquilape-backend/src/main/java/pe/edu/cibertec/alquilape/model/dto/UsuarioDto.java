package pe.edu.cibertec.alquilape.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import pe.edu.cibertec.alquilape.model.enums.Rol;

import java.time.LocalDateTime;

public class UsuarioDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsuarioRequest {
        @NotBlank(message = "El username es obligatorio")
        @Size(min = 4, max = 50, message = "El username debe tener entre 4 y 50 caracteres")
        private String username;

        @NotBlank(message = "El password es obligatorio")
        @Size(min = 6, message = "El password debe tener al menos 6 caracteres")
        private String password;

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Email inválido")
        private String email;

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 100)
        private String nombreCompleto;

        @NotNull(message = "El rol es obligatorio")
        private Rol rol;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsuarioUpdateRequest {
        @Email(message = "Email inválido")
        private String email;

        @Size(max = 100)
        private String nombreCompleto;

        private Rol rol;

        private Boolean activo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CambiarRolRequest {
        @NotNull(message = "El rol es obligatorio")
        private Rol nuevoRol;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CambiarPasswordRequest {
        @NotBlank(message = "La contraseña actual es obligatoria")
        private String passwordActual;

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
        private String passwordNueva;

        @NotBlank(message = "La confirmación de contraseña es obligatoria")
        private String passwordConfirmacion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsuarioResponse {
        private Long id;
        private String username;
        private String email;
        private String nombreCompleto;
        private String rol;
        private Boolean activo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}