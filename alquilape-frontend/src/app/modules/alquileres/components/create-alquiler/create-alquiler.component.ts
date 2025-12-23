import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AlquilerService } from '../../../../core/services/alquiler.service';
import { VehiculoService } from '../../../../core/services/vehiculo.service';
import { ClienteService } from '../../../../core/services/cliente.service';
import { Vehiculo } from '../../../../core/models/vehiculo.model';
import { Cliente } from '../../../../core/models/cliente.model';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-create-alquiler',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule
  ],
  templateUrl: './create-alquiler.component.html',
  styleUrls: ['./create-alquiler.component.css']
})
export class CreateAlquilerComponent implements OnInit {
  alquilerForm: FormGroup;
  clientes: Cliente[] = [];
  vehiculosDisponibles: Vehiculo[] = [];
  isSubmitting: boolean = false;
  errorMessage: string = '';
  fechaMin: string;
  fechaMax: string;

  constructor(
    private fb: FormBuilder,
    private alquilerService: AlquilerService,
    private vehiculoService: VehiculoService,
    private clienteService: ClienteService,
    private authService: AuthService,
    private router: Router
  ) {
    const today = new Date();
    this.fechaMin = today.toISOString().slice(0, 16);
    const maxDate = new Date();
    maxDate.setFullYear(today.getFullYear() + 1);
    this.fechaMax = maxDate.toISOString().slice(0, 16);

    this.alquilerForm = this.fb.group({
      clienteId: ['', Validators.required],
      vehiculoId: ['', Validators.required],
      fechaInicio: [this.fechaMin, Validators.required],
      fechaFinEstimada: ['', Validators.required],
      kilometrajeInicio: [0, [Validators.required, Validators.min(0)]],
      observaciones: ['']
    });
  }

  ngOnInit(): void {
    if (!this.authService.hasAnyRole(['ADMIN', 'SUPERVISOR'])) {
      this.router.navigate(['/dashboard']);
    }
    this.loadInitialData();
  }

  loadInitialData(): void {
    this.clienteService.getClientes(0, 100).subscribe(response => {
      this.clientes = response.content || response;
    });
    this.vehiculoService.getVehiculosDisponibles().subscribe(data => {
      this.vehiculosDisponibles = data;
    });
  }

  get f() {
    return this.alquilerForm.controls;
  }

  onVehiculoSelect(vehiculoId: any): void {
    const vehiculo = this.vehiculosDisponibles.find(v => v.id === Number(vehiculoId));
    if (vehiculo) {
      this.alquilerForm.patchValue({
        kilometrajeInicio: vehiculo.kilometraje
      });
    }
  }

  calcularDias(): number {
    const inicio = this.alquilerForm.get('fechaInicio')?.value;
    const fin = this.alquilerForm.get('fechaFinEstimada')?.value;
    if (inicio && fin) {
      const diff = new Date(fin).getTime() - new Date(inicio).getTime();
      return Math.ceil(diff / (1000 * 60 * 60 * 24));
    }
    return 0;
  }

  getVehiculoPrecio(): number {
    const vehiculoId = this.alquilerForm.get('vehiculoId')?.value;
    const vehiculo = this.vehiculosDisponibles.find(v => v.id === Number(vehiculoId));
    return vehiculo?.precioPorDia || 0;
  }

  calcularPrecioTotal(): number {
    return this.calcularDias() * this.getVehiculoPrecio();
  }

  onSubmit(): void {
    if (this.alquilerForm.valid) {
      this.isSubmitting = true;
      this.errorMessage = '';
      this.alquilerService.crearAlquiler(this.alquilerForm.value).subscribe({
        next: () => {
          this.isSubmitting = false;
          alert('Alquiler creado exitosamente');
          this.router.navigate(['/dashboard/alquileres']);
        },
        error: (error) => {
          this.isSubmitting = false;
          this.errorMessage = error.error?.message || 'Error al crear el alquiler';
        }
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/dashboard/alquileres']);
  }
}