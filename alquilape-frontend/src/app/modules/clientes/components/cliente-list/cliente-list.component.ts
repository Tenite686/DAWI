// modules/clientes/components/cliente-list/cliente-list.component.ts
import { Component, OnInit } from '@angular/core';
import { ClienteService } from '../../../../core/services/cliente.service';
import { AlquilerService } from '../../../../core/services/alquiler.service';
import { Cliente, TipoCliente } from '../../../../core/models/cliente.model';
import { AuthService } from '../../../../core/services/auth.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

@Component({
  selector: 'app-cliente-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatInputModule,
    MatSelectModule,
    MatSlideToggleModule
  ],
  templateUrl: './cliente-list.component.html',
  styleUrls: ['./cliente-list.component.css']
})
export class ClienteListComponent implements OnInit {
  clientes: Cliente[] = [];
  currentPage: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  isLoading: boolean = false;
  
  // Filtros
  filtros = {
    nombre: '',
    tipo: '',
    activo: true
  };
  
  tiposCliente = Object.values(TipoCliente);
  // Dentro de la clase ClienteListComponent
displayedColumns: string[] = ['id', 'nombre', 'contacto', 'tipo', 'estado', 'licencia', 'acciones'];

// --- VARIABLES NUEVAS PARA EL HISTORIAL ---
  mostrarModalHistorial: boolean = false;
  historialAlquileres: any[] = [];
  clienteSeleccionado: any = null;
  isLoadingHistorial: boolean = false;
  
  constructor(
    private clienteService: ClienteService,
    private authService: AuthService,
    private alquilerService: AlquilerService
  ) {}

  ngOnInit(): void {
    this.loadClientes();
  }

  loadClientes(): void {
    this.isLoading = true;
    this.clienteService.getClientes(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.clientes = response.content;
        this.totalElements = response.totalElements;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading clientes:', error);
        this.isLoading = false;
      }
    });
  }

  buscarClientes(): void {
    this.isLoading = true;
    this.clienteService.buscarClientes(this.filtros).subscribe({
      next: (response) => {
        this.clientes = response.content || response;
        this.totalElements = response.totalElements || response.length;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error buscando clientes:', error);
        this.isLoading = false;
      }
    });
  }

  verHistorial(clienteId: number): void {
    this.isLoadingHistorial = true;
    this.mostrarModalHistorial = true; // Abre el modal
    
    // Buscamos el nombre del cliente para el título
    this.clienteSeleccionado = this.clientes.find(c => c.id === clienteId);

    // Usamos AlquilerService para buscar por clienteId
    this.alquilerService.buscarAlquileres({ clienteId: clienteId }).subscribe({
      next: (response) => {
        // Asignamos la respuesta (manejo seguro de paginación o lista)
        this.historialAlquileres = response.content || response;
        this.isLoadingHistorial = false;
        console.log('Historial cargado:', this.historialAlquileres);
      },
      error: (error) => {
        console.error('Error obteniendo historial:', error);
        this.isLoadingHistorial = false;
      }
    });
  }

  // --- FUNCIÓN PARA CERRAR EL MODAL ---
  cerrarModalHistorial(): void {
    this.mostrarModalHistorial = false;
    this.historialAlquileres = [];
    this.clienteSeleccionado = null;
  }

  eliminarCliente(clienteId: number): void {
    if (confirm('¿Está seguro de eliminar este cliente?')) {
      this.clienteService.eliminarCliente(clienteId).subscribe({
        next: () => {
          this.loadClientes();
          alert('Cliente eliminado exitosamente');
        },
        error: (error) => {
          console.error('Error eliminando cliente:', error);
          alert('Error al eliminar cliente');
        }
      });
    }
  }

  canDelete(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  canEdit(): boolean {
    return this.authService.hasAnyRole(['ADMIN', 'SUPERVISOR']);
  }


onPageChange(event: any): void {
  this.currentPage = event.pageIndex;
  this.pageSize = event.pageSize;
  this.loadClientes();
}
}
