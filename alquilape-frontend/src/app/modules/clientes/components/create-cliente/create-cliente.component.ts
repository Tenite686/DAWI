import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ClienteService } from '../../../../core/services/cliente.service';
import { AuthService } from '../../../../core/services/auth.service';
import { TipoCliente } from '../../../../core/models/cliente.model';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';

@Component({
  selector: 'app-create-cliente',
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
  templateUrl: './create-cliente.component.html',
  styleUrls: ['./create-cliente.component.css']
})
export class CreateClienteComponent implements OnInit {
  clienteForm: FormGroup;
  isLoading: boolean = false;
  isSubmitting: boolean = false;
  errorMessage: string = '';

  //Variable para la edicion
  isEditMode: boolean = false;
  clienteId: number | null= null;
  titulo: string = "Nuevo CLiente";
  
  tiposCliente = Object.values(TipoCliente);
  
  fechaMin: string;
  fechaMax: string;

  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    const today = new Date();
    this.fechaMin = today.toISOString().split('T')[0];
    
    const maxDate = new Date();
    maxDate.setFullYear(today.getFullYear() + 10);
    this.fechaMax = maxDate.toISOString().split('T')[0];
    
    this.clienteForm = this.fb.group({
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      dniRuc: ['', [Validators.required, Validators.pattern(/^\d{8,11}$/)]],
      email: ['', [Validators.required, Validators.email]],
      telefono: ['', [Validators.required, Validators.pattern(/^\d{9}$/)]],
      direccion: ['', Validators.required],
      tipo: ['PERSONA', Validators.required],
      licenciaNumero: [''],
      licenciaVencimiento: [''],
      activo: [true]
    });
    
    // Agregar validaciones condicionales para PERSONA
    this.clienteForm.get('tipo')?.valueChanges.subscribe(tipo => {
      const licenciaNumeroControl = this.clienteForm.get('licenciaNumero');
      const licenciaVencimientoControl = this.clienteForm.get('licenciaVencimiento');
      
      if (tipo === 'PERSONA') {
        licenciaNumeroControl?.setValidators([Validators.required]);
        licenciaVencimientoControl?.setValidators([Validators.required]);
      } else {
        licenciaNumeroControl?.clearValidators();
        licenciaVencimientoControl?.clearValidators();
      }
      
      licenciaNumeroControl?.updateValueAndValidity();
      licenciaVencimientoControl?.updateValueAndValidity();
    });
  }

  ngOnInit(): void {
    if (!this.authService.hasAnyRole(['ADMIN', 'SUPERVISOR'])) {
      this.router.navigate(['/dashboard']);
      return;
    }

    //logica de deteccion de edicion
    this.route.queryParamMap.subscribe(params => {
      const id = params.get('id');
      if(id) {
        this.isEditMode = true;
        this.clienteId = +id;
        this.titulo = "Editar Cliente"; //cambia titulo
        this.cargarDatosCliente(this.clienteId);
      }
    });
  }

 onSubmit(): void {
  if (this.clienteForm.invalid) {
    this.markFormGroupTouched(this.clienteForm);
    return;
  }

  this.isSubmitting = true;
  this.errorMessage = '';
  
  // Clonamos el valor del formulario y aseguramos el campo 'activo'
  const clienteData = {
    ...this.clienteForm.value,
    // Forzamos que sea booleano por si el select lo devuelve como string
    activo: this.clienteForm.get('activo')?.value === true
  };

  // LOG DE CONTROL: Mira la consola del navegador al dar clic en Guardar
  console.log('Enviando datos al servidor:', clienteData);

  if (this.isEditMode && this.clienteId) {
    this.clienteService.actualizarCliente(this.clienteId, clienteData).subscribe({
      next: () => {
        this.isSubmitting = false;
        alert('Cliente actualizado exitosamente');
        this.router.navigate(['/dashboard/clientes']);
      },
      error: (error) => {
        this.isSubmitting = false;
        this.errorMessage = error.error?.message || 'Error al actualizar el cliente';
        console.error('Error updating cliente: ', error);
      }
    });
  } else {
    this.clienteService.crearCliente(clienteData).subscribe({
      next: () => {
        this.isSubmitting = false;
        alert('Cliente creado exitosamente');
        this.router.navigate(['/dashboard/clientes']);
      },
      error: (error) => {
        this.isSubmitting = false;
        this.errorMessage = error.error?.message || 'Error al crear el cliente';
        console.error('Error creating cliente:', error);
      }
    });
  }
}
 
  //Funcion para carhar datos
  cargarDatosCliente(id: number): void {
    this.isLoading = true;
    this.clienteService.getClienteById(id).subscribe({
        next: (cliente) => {
          //Rellena el form con los datos que vienen del backend
          this.clienteForm.patchValue(cliente);
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error cargando cliente', error);
          this.errorMessage = "No se puede cargar la información del cliente";
          this,this.isLoading = false;
        }
    });
  }

  cancelar(): void {
   const accion = this.isEditMode ? 'edición' : 'creación';
   if(confirm('¿Desea cancelar?, Los datos no guardados se perderán')){
    this.router.navigate(['/dashboard/clientes']);
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
    return this.clienteForm.controls;
  }
}