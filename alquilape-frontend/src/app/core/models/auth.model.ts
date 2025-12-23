export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  refreshToken?: string;
  username: string;
  rol: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}