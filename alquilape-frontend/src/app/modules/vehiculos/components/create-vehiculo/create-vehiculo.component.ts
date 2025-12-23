import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { VehiculoService } from '../../../../core/services/vehiculo.service';
import { AuthService } from '../../../../core/services/auth.service';
import { TipoVehiculo, EstadoVehiculo } from '../../../../core/models/vehiculo.model';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-create-vehiculo',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './create-vehiculo.component.html',
  styleUrls: ['./create-vehiculo.component.css']
})
export class CreateVehiculoComponent implements OnInit {
  vehiculoForm: FormGroup;
  isLoading: boolean = false;
  isSubmitting: boolean = false;
  errorMessage: string = '';

  //Para controlar el modo Edicion
  isEditMode: boolean = false;
  vehiculoId: number | null = null;
  titulo: string = "Crear Nuevo vehículo";
  
  tiposVehiculo = Object.values(TipoVehiculo);
  estadosVehiculo = Object.values(EstadoVehiculo);
  
  caracteristicasAdicionales: {key: string, value: any}[] = [];
  maxYear: number;

  constructor(
    private fb: FormBuilder,
    private vehiculoService: VehiculoService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.maxYear = new Date().getFullYear() + 1;
    this.vehiculoForm = this.fb.group({
      marca: ['', Validators.required],
      modelo: ['', Validators.required],
      anio: [new Date().getFullYear(), [Validators.required, Validators.min(1900), Validators.max(this.maxYear)]],
      placa: ['', Validators.required],
      color: ['', Validators.required],
      tipo: ['AUTO', Validators.required],
      estado: ['DISPONIBLE', Validators.required],
      precioPorDia: [0, [Validators.required, Validators.min(0)]],
      kilometraje: [0, [Validators.required, Validators.min(0)]],
      capacidadPasajeros: [5, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    if (!this.authService.hasRole('ADMIN')) {
      this.router.navigate(['/dashboard']);
      return; //puse esto
    }
    
   

    //Detectar si venimos a editar
    this.route.queryParamMap.subscribe(params => {
      const id = params.get('id');
      if (id){
        this.isEditMode = true;
        this.vehiculoId= +id;
        this.titulo = "Editar Vehículo"; //Cambia el titulo
        this.cargarDatosVehiculo(this.vehiculoId);
      }
    });
  }

  //function para pedir datos al backend
  cargarDatosVehiculo(id: number): void {
    this.isLoading = true;
    this.vehiculoService.getVehiculoById(id).subscribe({
      next: (vehiculo) => {
        //Rellena form auto
        this.vehiculoForm.patchValue(vehiculo);

        // Rellena caracteristicas Adicionales
        if(vehiculo.caracteristicasAdicionales){
          this.caracteristicasAdicionales = Object.entries(vehiculo.caracteristicasAdicionales)
          .map(([key, value]) => ({
            key: key,
            value: value
          }));
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error cargando vehiculos: ', error);
        this.errorMessage = "No se pudo cargar la informacion del vehículo";
        this.isLoading = false;
      }
    })
  }

  agregarCaracteristica(): void {
    this.caracteristicasAdicionales.push({key: '', value: ''});
  }

  removerCaracteristica(index: number): void {
    this.caracteristicasAdicionales.splice(index, 1);
  }

  onSubmit(): void {
    if (this.vehiculoForm.valid) {
      this.isSubmitting = true;
      this.errorMessage = '';
      
      const vehiculoData = this.vehiculoForm.value;
      
      // Convertir características adicionales a objeto
      if (this.caracteristicasAdicionales.length > 0) {
        const caracteristicas: any = {};
        this.caracteristicasAdicionales.forEach(c => {
          if (c.key.trim()) {
            caracteristicas[c.key] = c.value;
          }
        });
        vehiculoData.caracteristicasAdicionales = caracteristicas;
      }
      //Desicion entre actalizar o crear
      if(this.isEditMode && this.vehiculoId) {
        //Modo actualziar
        this.vehiculoService.actualizarVehiculo(this.vehiculoId, vehiculoData).subscribe({
          next: () => {
            this.isSubmitting = false;
            alert('Vehículo actualizado exitosamente');
            this.router.navigate(['/dashboard/vehiculos']);
          },
          error: (error) => {
            this.isSubmitting = false;
            this.errorMessage = error.error?.message || 'Error al actualizar el vehículo';
            console.error('Error updating vehiculo:', error);
          }
        });
      }else{
      
      this.vehiculoService.crearVehiculo(vehiculoData).subscribe({
        next: (response) => {
          this.isSubmitting = false;
          alert('Vehículo creado exitosamente');
          this.router.navigate(['/dashboard/vehiculos']);
        },
        error: (error) => {
          this.isSubmitting = false;
          this.errorMessage = error.error?.message || 'Error al crear el vehículo';
          console.error('Error creating vehiculo:', error);
        }
      });
    }
    } else {
      this.markFormGroupTouched(this.vehiculoForm);
    }
  }

  cancelar(): void {
    if (confirm('¿Desea cancelar la creación del vehículo? Los datos no guardados se perderán.')) {
      this.router.navigate(['/dashboard/vehiculos']);
    }
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  get f() {
    return this.vehiculoForm.controls;
  }
}