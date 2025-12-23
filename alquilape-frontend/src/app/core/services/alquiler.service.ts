import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Alquiler } from '../models/alquiler.model';

@Injectable({
  providedIn: 'root'
})
export class AlquilerService {
  private apiUrl = `${environment.apiUrl}/alquileres`;

  constructor(private http: HttpClient) {}

  getAlquileres(page: number = 0, size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(this.apiUrl, { params });
  }

  buscarAlquileres(filtros: any): Observable<any> {
    let params = new HttpParams();
    
    Object.keys(filtros).forEach(key => {
      if (filtros[key] !== null && filtros[key] !== undefined && filtros[key] !== '') {
        params = params.set(key, filtros[key]);
      }
    });

    return this.http.get<any>(this.apiUrl, { params });
  }

  getAlquilerById(id: number): Observable<Alquiler> {
    return this.http.get<Alquiler>(`${this.apiUrl}/${id}`);
  }

  crearAlquiler(alquiler: Alquiler): Observable<Alquiler> {
    return this.http.post<Alquiler>(this.apiUrl, alquiler);
  }

  registrarDevolucion(id: number, datosDevolucion: any): Observable<Alquiler> {
    return this.http.put<Alquiler>(`${this.apiUrl}/${id}/devolver`, datosDevolucion);
  }

  cancelarAlquiler(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/cancelar`, {});
  }

  eliminarAlquiler(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}