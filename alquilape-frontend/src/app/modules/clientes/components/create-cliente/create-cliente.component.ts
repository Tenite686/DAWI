import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
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
  
  tiposCliente = Object.values(TipoCliente);
  
  fechaMin: string;
  fechaMax: string;

  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService,
    private authService: AuthService,
    private router: Router
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
      licenciaVencimiento: ['']
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
    }
  }

  onSubmit(): void {
    if (this.clienteForm.valid) {
      this.isSubmitting = true;
      this.errorMessage = '';
      
      const clienteData = this.clienteForm.value;
      
      this.clienteService.crearCliente(clienteData).subscribe({
        next: (response) => {
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
    } else {
      this.markFormGroupTouched(this.clienteForm);
    }
  }

  cancelar(): void {
    if (confirm('¿Desea cancelar la creación del cliente? Los datos no guardados se perderán.')) {
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