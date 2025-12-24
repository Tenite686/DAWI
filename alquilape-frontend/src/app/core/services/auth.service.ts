import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, Subscription, timer } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse } from '../models/auth.model';
import {jwtDecode} from 'jwt-decode'; // para alerta de expiración
import Swal from 'sweetalert2'; // Para la alerta 
import {Router} from '@angular/router'; // Para redirigir al login

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  private tokenKey = 'auth_token';
  private refreshKey = 'refresh_token'; // Si usas refresh tokens 
  private currentUserSubject = new BehaviorSubject<any>(null);
  private timeoutSubscription?: Subscription; // Limpiar el timer
  
  public currentUser$ = this.currentUserSubject.asObservable();

  public get currentUserValue(): any {
    return this.currentUserSubject.value;
  }

  constructor(private http: HttpClient, private router: Router) {
    this.loadUserFromStorage();
    //Inicar vigilancia si hay un usuario al recargar la pagina
    const token = this.getToken();
    if (token) {
      this.iniciarVigilanciaToken(token);
    }
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(
        tap(response => {
          this.saveToken(response.token);
          this.saveRefreshToken(response.refreshToken!); // Si usas refresh tokens
          this.saveUser(response);
          this.iniciarVigilanciaToken(response.token); // Iniciar vigilancia del token
        })
      );
  }

  // --- LÓGICA TIPO YAPE ---
  private iniciarVigilanciaToken(token: string): void {
    if (this.timeoutSubscription) this.timeoutSubscription.unsubscribe();

    try {
      const decoded: any = jwtDecode(token);
      const exp = decoded.exp * 1000;
      const tiempoRestante = exp - Date.now();
      const tiempoParaAviso = tiempoRestante - 60000; // 1 minuto antes

      if (tiempoParaAviso > 0) {
        this.timeoutSubscription = timer(tiempoParaAviso).subscribe(() => {
          this.mostrarAlertaExpiracion();
        });
      }
    } catch (error) {
      console.error("Error decodificando token", error);
    }
  }
  
  private mostrarAlertaExpiracion(): void {
    Swal.fire({
      title: '¿Sigues ahí?',
      text: 'Tu sesión está por expirar por seguridad.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Mantener sesión',
      cancelButtonText: 'Salir',
      timer: 30000,
      timerProgressBar: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.ejecutarRefresh();
      } else {
        this.logout();
      }
    });
  }

private ejecutarRefresh(): void {
    const refreshToken = localStorage.getItem(this.refreshKey);
    
    // Si no hay token guardado, no podemos refrescar, así que salimos
    if (!refreshToken) {
      this.logout();
      return;
    }
    this.http.post<LoginResponse>(`${this.apiUrl}/auth/refresh`, { refreshToken })
      .subscribe({
        next: (res) => {
          this.saveToken(res.token);      
          // Solución al error: Solo guardamos si res.refreshToken NO es undefined
          if (res.refreshToken) {
            this.saveRefreshToken(res.refreshToken);
          }   
          this.iniciarVigilanciaToken(res.token);
        },
        error: () => {
          console.error("No se pudo refrescar el token");
          this.logout();
        }
      });
  }

  // --- MÉTODOS DE APOYO ---
  private saveRefreshToken(token: string): void {
    localStorage.setItem(this.refreshKey, token);
  }

 logout(): void {
    if (this.timeoutSubscription) this.timeoutSubscription.unsubscribe();
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.refreshKey);
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    this.router.navigate(['/auth/login']);
  }


  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getUserRole(): string {
    const user = this.currentUserSubject.value;
    return user?.role || '';
  }

  hasRole(role: string): boolean {
    return this.getUserRole() === role;
  }

  hasAnyRole(roles: string[]): boolean {
    const userRole = this.getUserRole();
    return roles.includes(userRole);
  }

  private saveToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  private saveUser(userData: LoginResponse): void {
    const user = {
      username: userData.username,
      role: userData.rol,
      token: userData.token
    };

    console.log("save user: ", user)
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  private loadUserFromStorage(): void {
    const userStr = localStorage.getItem('currentUser');
    if (userStr) {
      this.currentUserSubject.next(JSON.parse(userStr));
    }
  }
  
}