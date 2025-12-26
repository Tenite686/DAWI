import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { VehiculoService } from '../../../../core/services/vehiculo.service';
import { AuthService } from '../../../../core/services/auth.service';
import { Vehiculo, TipoVehiculo, EstadoVehiculo } from '../../../../core/models/vehiculo.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

@Component({
  selector: 'app-vehiculo-list',
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
  templateUrl: './vehiculo-list.component.html',
  styleUrls: ['./vehiculo-list.component.css']
})
export class VehiculoListComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  
  displayedColumns: string[] = ['id', 'marca', 'modelo', 'placa', 'tipo', 'estado', 'precio', 'kilometraje', 'acciones'];
  dataSource = new MatTableDataSource<Vehiculo>([]);
  
  vehiculos: Vehiculo[] = [];
  vehiculosDisponibles: Vehiculo[] = [];
  
  currentPage: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  isLoading: boolean = false;
  
  // Filtros
  filtros = {
    marca: '',
    tipo: '',
    estado: '',
    precioMin: null as number | null,
    precioMax: null as number | null
  };
  
  tiposVehiculo = Object.values(TipoVehiculo);
  estadosVehiculo = Object.values(EstadoVehiculo);
  mostrarDisponibles: boolean = false;
  
  // Modal
  mostrarModalDetalles: boolean = false;
  vehiculoDetalles: Vehiculo | null = null;

  constructor(
    private vehiculoService: VehiculoService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadVehiculos();
    this.loadVehiculosDisponibles();
  }

  loadVehiculos(): void {
    this.isLoading = true;
    this.vehiculoService.getVehiculos(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.vehiculos = response.content || response;
        this.dataSource.data = this.vehiculos;
        this.totalElements = response.totalElements;
        this.isLoading = false;
        
      },
      error: (error) => {
        console.error('Error loading vehiculos:', error);
        this.isLoading = false;
      }
    });
  }
  //Metodo para manejar el cambio de pagina
  onPageChange(event: any): void {
    this.currentPage = event.pageIndex; 
    this.pageSize = event.pageSize;
    this.loadVehiculos();
}
cambiarPagina(e: any) {
  this.pageSize = e.pageSize;
  this.currentPage = e.pageIndex; 
  this.loadVehiculos(); 
}

  loadVehiculosDisponibles(): void {
    this.vehiculoService.getVehiculosDisponibles().subscribe({
      next: (vehiculos) => {
        this.vehiculosDisponibles = vehiculos;
      },
      error: (error) => {
        console.error('Error loading vehiculos disponibles:', error);
      }
    });
  }

  buscarVehiculos(): void {
    this.isLoading = true;
    const filtrosActivos: any = {};

    Object.keys(this.filtros).forEach(key => {
      let value = (this.filtros as any)[key];
      if ((key === 'precioMin' || key === 'precioMax') && value !== null && value !== undefined && value !== '') {
        value = Number(value);
        if (!isNaN(value)) {
          filtrosActivos[key] = value;
        }
      } else if (key !== 'precioMin' && key !== 'precioMax' && value !== null && value !== undefined && value !== '') {
        filtrosActivos[key] = value;
      }
    });

    this.vehiculoService.buscarVehiculos(filtrosActivos).subscribe({
      next: (response) => {
        this.vehiculos = response.content || response;
        this.dataSource.data = this.vehiculos;
        this.totalElements = response.totalElements || this.vehiculos.length;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error buscando vehiculos:', error);
        this.isLoading = false;
      }
    });
  }

  limpiarFiltros(): void {
    this.filtros = {
      marca: '',
      tipo: '',
      estado: '',
      precioMin: null,
      precioMax: null
    };
    this.loadVehiculos();
  }

  toggleDisponibles(): void {
    this.mostrarDisponibles = !this.mostrarDisponibles;
    if (this.mostrarDisponibles) {
      this.dataSource.data = this.vehiculosDisponibles;
    } else {
      this.dataSource.data = this.vehiculos;
    }
  }

  verDetalles(vehiculo: Vehiculo): void {
    this.vehiculoDetalles = vehiculo;
    this.mostrarModalDetalles = true;
  }

 eliminarVehiculo(vehiculoId: number): void {
  const vehiculo = this.vehiculos.find(v => v.id === vehiculoId);
  if (vehiculo && vehiculo.estado === 'INACTIVO') {
    alert('Este vehículo ya está inactivo.');
    return;
  }
  if (vehiculo && vehiculo.estado === 'ALQUILADO') {
    alert('Este vehículo está alquilado.');
    return;
  }
  if (confirm('¿Está seguro de eliminar este vehículo?')) {
    this.isLoading = true;
    this.vehiculoService.eliminarVehiculo(vehiculoId).subscribe({
      next: () => {
        this.loadVehiculos();
        this.loadVehiculosDisponibles();
        this.isLoading = false;
        alert('Vehículo eliminado exitosamente');
      },
      error: (error) => {
        console.error('Error eliminando vehículo:', error);
        this.isLoading = false;
        alert('Error al eliminar vehículo');
      }
    });
  }
}

  cambiarEstado(vehiculoId: number, nuevoEstado: string): void {
    const vehiculo = this.vehiculos.find(v => v.id === vehiculoId);
    if (vehiculo) {
      const vehiculoActualizado = { ...vehiculo, estado: nuevoEstado };
      this.vehiculoService.actualizarVehiculo(vehiculoId, vehiculoActualizado).subscribe({
        next: () => {
          this.loadVehiculos();
          this.loadVehiculosDisponibles();
          alert('Estado actualizado exitosamente');
        },
        error: (error) => {
          console.error('Error actualizando estado:', error);
          alert('Error al actualizar estado');
        }
      });
    }
  }

  cerrarModalDetalles(): void {
    this.mostrarModalDetalles = false;
    this.vehiculoDetalles = null;
  }

  getEstadoBadgeClass(estado: string): string {
    switch(estado) {
      case 'DISPONIBLE': return 'badge-success';
      case 'ALQUILADO': return 'badge-warning';
      case 'MANTENIMIENTO': return 'badge-danger';
      case 'INACTIVO': return 'badge-secondary';
      default: return 'badge-secondary';
    }
  }

  getTipoBadgeClass(tipo: string): string {
    switch(tipo) {
      case 'AUTO': return 'badge-auto';
      case 'CAMIONETA': return 'badge-camioneta';
      case 'SUV': return 'badge-suv';
      case 'MOTO': return 'badge-moto';
      default: return 'badge-secondary';
    }
  }

  canEdit(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  getVehiculosAMostrar(): Vehiculo[] {
    return this.mostrarDisponibles ? this.vehiculosDisponibles : this.vehiculos;
  }

  // Adicional
  getCaracteristicasKeys(caracteristicas: any): string[] {
    return caracteristicas ? Object.keys(caracteristicas) : [];
  }

  getVehiculoPrecio(): number {
    const vehiculoId = this.vehiculoDetalles?.id;
    const vehiculo = this.vehiculos.find(v => v.id === vehiculoId);
    return vehiculo?.precioPorDia || 0;
  }
}