import { Component, OnInit } from '@angular/core';
import { UsuarioService } from '../../../../core/services/usuario.service';
import { Usuario } from '../../../../core/models/usuario.model';
import { AuthService } from '../../../../core/services/auth.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-usuario-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatInputModule,
    MatSelectModule
  ],
  templateUrl: './usuario-list.component.html',
  styleUrls: ['./usuario-list.component.css']
})
export class UsuarioListComponent implements OnInit {
  usuarios: Usuario[] = [];
  currentPage: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  isLoading: boolean = false;
  
  // Filtros
  searchTerm: string = '';
  rolFilter: string = '';
  
  roles: string[] = ['ADMIN', 'SUPERVISOR', 'ASISTENTE'];

  constructor(
    private usuarioService: UsuarioService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUsuarios();
  }

loadUsuarios(): void {
  this.isLoading = true;
  this.usuarioService.getUsuarios(
    this.currentPage,
    this.pageSize,
    this.rolFilter // <-- agrega este argumento
  ).subscribe({
    next: (response) => {
      this.usuarios = response.content;
      this.totalElements = response.totalElements;
      this.isLoading = false;
    },
    error: (error) => {
      console.error('Error loading usuarios:', error);
      this.isLoading = false;
    }
  });
}

  onPageChange(event: any): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadUsuarios();
  }

  cambiarRol(usuarioId: number, nuevoRol: string): void {
    if (confirm('¿Está seguro de cambiar el rol de este usuario?')) {
      this.usuarioService.cambiarRol(usuarioId, nuevoRol).subscribe({
        next: () => {
          this.loadUsuarios();
          alert('Rol cambiado exitosamente');
        },
        error: (error) => {
          console.error('Error cambiando rol:', error);
          alert('Error al cambiar el rol');
        }
      });
    }
  }

  cambiarEstado(usuarioId: number, activo: boolean): void {
    const action = activo ? 'activar' : 'desactivar';
    if (confirm(`¿Está seguro de ${action} este usuario?`)) {
      this.usuarioService.cambiarEstado(usuarioId, activo).subscribe({
        next: () => {
          this.loadUsuarios();
          alert(`Usuario ${action}do exitosamente`);
        },
        error: (error) => {
          console.error('Error cambiando estado:', error);
          alert('Error al cambiar estado');
        }
      });
    }
  }

  eliminarUsuario(usuarioId: number): void {
    if (confirm('¿Está seguro de eliminar este usuario?')) {
      this.usuarioService.eliminarUsuario(usuarioId).subscribe({
        next: () => {
          this.loadUsuarios();
          alert('Usuario eliminado exitosamente');
        },
        error: (error) => {
          console.error('Error eliminando usuario:', error);
          alert('Error al eliminar usuario');
        }
      });
    }
  }

  canEdit(): boolean {
    return this.authService.hasRole('ADMIN');
  }
}