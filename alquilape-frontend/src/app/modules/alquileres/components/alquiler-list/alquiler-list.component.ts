import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { AlquilerService } from '../../../../core/services/alquiler.service';
import { ClienteService } from '../../../../core/services/cliente.service';
import { VehiculoService } from '../../../../core/services/vehiculo.service';
import { AuthService } from '../../../../core/services/auth.service';
import { Alquiler, EstadoAlquiler } from '../../../../core/models/alquiler.model';
import { Cliente } from '../../../../core/models/cliente.model';
import { Vehiculo } from '../../../../core/models/vehiculo.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-alquiler-list',
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
  templateUrl: './alquiler-list.component.html',
  styleUrls: ['./alquiler-list.component.css']
})
export class AlquilerListComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  
  displayedColumns: string[] = ['id', 'cliente', 'vehiculo', 'fechaInicio', 'fechaFin', 'estado', 'precioTotal', 'acciones'];
  dataSource = new MatTableDataSource<Alquiler>([]);
  
  alquileres: Alquiler[] = [];
  clientes: Cliente[] = [];
  vehiculos: Vehiculo[] = [];
  
  currentPage: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  isLoading: boolean = false;
  
  // Filtros
  filtros = {
    estado: '',
    clienteId: null as number | null
  };
  
  estadosAlquiler = Object.values(EstadoAlquiler);
  
  // Modal
  mostrarModalDevolucion: boolean = false;
  alquilerSeleccionado: Alquiler | null = null;
  datosDevolucion = {
    fechaDevolucion: '',
    kilometrajeFin: 0,
    observaciones: ''
  };
  
  mostrarModalDetalles: boolean = false;
  alquilerDetalles: any = null;

  constructor(
    private alquilerService: AlquilerService,
    private clienteService: ClienteService,
    private vehiculoService: VehiculoService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadAlquileres();
    this.loadClientes();
    this.loadVehiculos();
  }

  loadAlquileres(): void {
  this.isLoading = true;
  this.alquilerService.getAlquileres(this.currentPage, this.pageSize).subscribe({
    next: (response) => {
      this.alquileres = response.content || response;
      this.dataSource.data = this.alquileres;
      this.totalElements = response.totalElements || this.alquileres.length;
      
      // AGREGAR ESTO: Sincroniza el índice del paginador con la variable del componente
      if (this.paginator) {
        this.paginator.pageIndex = this.currentPage;
      }

      this.isLoading = false;
    },
    error: (error) => {
      console.error('Error loading alquileres:', error);
      this.isLoading = false;
    }
  });
}

  loadClientes(): void {
    this.clienteService.getClientes(0, 100).subscribe({
      next: (response) => {
        this.clientes = response.content || response;
      },
      error: (error) => {
        console.error('Error loading clientes:', error);
      }
    });
  }

  loadVehiculos(): void {
    this.vehiculoService.getVehiculos(0, 100).subscribe({
      next: (response) => {
        this.vehiculos = response.content || response;
      },
      error: (error) => {
        console.error('Error loading vehiculos:', error);
      }
    });
  }

  buscarAlquileres(): void {
    this.isLoading = true;
    const filtrosActivos: any = {};
    
    if (this.filtros.estado) {
      filtrosActivos.estado = this.filtros.estado;
    }
    
    if (this.filtros.clienteId) {
      filtrosActivos.clienteId = this.filtros.clienteId;
    }
    
    this.alquilerService.buscarAlquileres(filtrosActivos).subscribe({
      next: (response) => {
        this.alquileres = response.content || response;
        this.dataSource.data = this.alquileres;
        this.totalElements = response.totalElements || this.alquileres.length;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error buscando alquileres:', error);
        this.isLoading = false;
      }
    });
  }

  limpiarFiltros(): void {
    this.filtros = {
      estado: '',
      clienteId: null
    };
    this.loadAlquileres();
  }

  verDetalles(alquiler: Alquiler): void {
    this.alquilerService.getAlquilerById(alquiler.id).subscribe({
      next: (detalles) => {
        this.alquilerDetalles = detalles;
        this.mostrarModalDetalles = true;
      },
      error: (error) => {
        console.error('Error obteniendo detalles:', error);
      }
    });
  }

  abrirModalDevolucion(alquiler: Alquiler): void {
  this.alquilerSeleccionado = alquiler;
  this.datosDevolucion = {
    fechaDevolucion: alquiler.fechaFinEstimada 
      ? this.formatDateToLocalInput(alquiler.fechaFinEstimada)
      : this.formatDateToLocalInput(new Date().toISOString()),
    kilometrajeFin: alquiler.kilometrajeInicio + 100,
    observaciones: 'Vehículo devuelto en buen estado'
  };
  this.mostrarModalDevolucion = true;
}

  registrarDevolucion(): void {
  if (this.alquilerSeleccionado) {
    this.isLoading = true;

    // Convertir fechaDevolucion a ISO string si es necesario
    const fechaLocal = this.datosDevolucion.fechaDevolucion;
    const fechaISO = new Date(fechaLocal).toISOString();

    const datos = {
      ...this.datosDevolucion,
      fechaDevolucion: fechaISO
    };

    this.alquilerService.registrarDevolucion(
      this.alquilerSeleccionado.id,
      datos
    ).subscribe({
      next: () => {
        this.loadAlquileres();
        this.cerrarModalDevolucion();
        this.isLoading = false;
        alert('Devolución registrada exitosamente');
      },
      error: (error) => {
        console.error('Error registrando devolución:', error);
        this.isLoading = false;
        alert('Error al registrar devolución');
      }
    });
  }
}

  cancelarAlquiler(alquilerId: number): void {
    if (confirm('¿Está seguro de cancelar este alquiler?')) {
      this.isLoading = true;
      this.alquilerService.cancelarAlquiler(alquilerId).subscribe({
        next: () => {
          this.loadAlquileres();
          this.isLoading = false;
          alert('Alquiler cancelado exitosamente');
        },
        error: (error) => {
          console.error('Error cancelando alquiler:', error);
          this.isLoading = false;
          alert('Error al cancelar alquiler');
        }
      });
    }
  }

  eliminarAlquiler(alquilerId: number): void {
    const alquiler = this.alquileres.find(a => a.id === alquilerId);
  if (alquiler && alquiler.estado === 'FINALIZADO') {
    alert('El alquiler ya está finalizado.');
    return;
  }
  if (confirm('¿Está seguro de eliminar este alquiler?')) {
    this.isLoading = true;
    this.alquilerService.eliminarAlquiler(alquilerId).subscribe({
      next: () => {
        this.loadAlquileres();
        this.isLoading = false;
        alert('Alquiler eliminado exitosamente');
      },
      error: (error) => {
        console.error('Error eliminando alquiler:', error);
        this.isLoading = false;
        alert('Error al eliminar alquiler');
        }
      });
    }
  }

  cerrarModalDevolucion(): void {
    this.mostrarModalDevolucion = false;
    this.alquilerSeleccionado = null;
    this.datosDevolucion = {
      fechaDevolucion: '',
      kilometrajeFin: 0,
      observaciones: ''
    };
  }

  cerrarModalDetalles(): void {
    this.mostrarModalDetalles = false;
    this.alquilerDetalles = null;
  }

  getClienteNombre(clienteId: number): string {
    const cliente = this.clientes.find(c => c.id === clienteId);
    return cliente ? `${cliente.nombre} ${cliente.apellido}` : 'N/A';
  }

  getVehiculoInfo(vehiculoId: number): string {
    const vehiculo = this.vehiculos.find(v => v.id === vehiculoId);
    return vehiculo ? `${vehiculo.marca} ${vehiculo.modelo} (${vehiculo.placa})` : 'N/A';
  }

  getEstadoBadgeClass(estado: string): string {
    switch(estado) {
      case 'ACTIVO': return 'badge-active';
      case 'FINALIZADO': return 'badge-success';
      case 'CANCELADO': return 'badge-danger';
      default: return 'badge-secondary';
    }
  }

  canDelete(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  canEdit(): boolean {
    return this.authService.hasAnyRole(['ADMIN', 'SUPERVISOR']);
  }

  private formatDateToLocalInput(dateString: string): string {
  const date = new Date(dateString);
  const pad = (n: number) => n.toString().padStart(2, '0');
  const yyyy = date.getFullYear();
  const MM = pad(date.getMonth() + 1);
  const dd = pad(date.getDate());
  const hh = pad(date.getHours());
  const mm = pad(date.getMinutes());
  return `${yyyy}-${MM}-${dd}T${hh}:${mm}`;
}
onPageChange(event: any): void {
    this.currentPage = event.pageIndex; // Cambia a la página seleccionada
    this.pageSize = event.pageSize;     // Cambia el tamaño de página si el usuario lo elige
    this.loadAlquileres();              // Recarga los datos llamando al backend
  }

}