export interface Usuario {
  id: number;
  username: string;
  email: string;
  nombreCompleto: string;
  rol: string;
  activo: boolean;
  fechaCreacion: Date;
}

export interface CrearUsuarioRequest {
  username: string;
  password: string;
  email: string;
  nombreCompleto: string;
  rol: string;
}

export interface CambiarRolRequest {
  nuevoRol: string;
}

export interface CambiarPasswordRequest {
  passwordActual: string;
  passwordNueva: string;
  passwordConfirmacion: string;
}