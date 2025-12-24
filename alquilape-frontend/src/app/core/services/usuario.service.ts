import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { 
  Usuario, 
  CrearUsuarioRequest, 
  CambiarRolRequest, 
  CambiarPasswordRequest 
} from '../models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private apiUrl = `${environment.apiUrl}/usuarios`;

  constructor(private http: HttpClient) {}

  getUsuarios(
  page: number = 0,
  size: number = 10,
  rolFilter: string = '',
  sortBy: string = 'id',
  direction: string = 'ASC'
): Observable<any> {
  let params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString())
    .set('sortBy', sortBy)
    .set('direction', direction);

  if (rolFilter && rolFilter.trim() !== '') {
    params = params.set('rol', rolFilter.trim());
  }

    return this.http.get<any>(this.apiUrl, { params });
  }

  getUsuarioById(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/${id}`);
  }

  crearUsuario(usuario: CrearUsuarioRequest): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiUrl, usuario);
  }

  actualizarUsuario(id: number, usuario: Partial<Usuario>): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id}`, usuario);
  }

  cambiarRol(id: number, nuevoRol: string): Observable<any> {
    const request: CambiarRolRequest = { nuevoRol };
    return this.http.patch(`${this.apiUrl}/${id}/cambiar-rol`, request);
  }

  cambiarEstado(id: number, activo: boolean): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/cambiar-estado?activo=${activo}`, {});
  }

  cambiarPassword(id: number, passwordData: CambiarPasswordRequest): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/cambiar-password`, passwordData);
  }

  eliminarUsuario(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}