import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { UsuarioService } from '../../../../core/services/usuario.service';
import { AuthService } from '../../../../core/services/auth.service';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-create-usuario',
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
    MatIconModule
  ],
  templateUrl: './create-usuario.component.html',
  styleUrls: ['./create-usuario.component.css']
})
export class CreateUsuarioComponent implements OnInit {
  usuarioForm: FormGroup;
  isLoading: boolean = false;
  isSubmitting: boolean = false;
  errorMessage: string = '';
  
  roles = ['ADMIN', 'SUPERVISOR', 'ASISTENTE'];
  
  passwordVisible: boolean = false;

  constructor(
    private fb: FormBuilder,
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private router: Router
  ) {
    this.usuarioForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      email: ['', [Validators.required, Validators.email]],
      nombreCompleto: ['', Validators.required],
      rol: ['ASISTENTE', Validators.required]
    });
  }

  ngOnInit(): void {
    if (!this.authService.hasRole('ADMIN')) {
      this.router.navigate(['/dashboard']);
    }
  }

  togglePasswordVisibility(): void {
    this.passwordVisible = !this.passwordVisible;
  }

  onSubmit(): void {
    if (this.usuarioForm.valid) {
      this.isSubmitting = true;
      this.errorMessage = '';
      
      const usuarioData = this.usuarioForm.value;
      
      this.usuarioService.crearUsuario(usuarioData).subscribe({
        next: (response) => {
          this.isSubmitting = false;
          alert('Usuario creado exitosamente');
          this.router.navigate(['/dashboard/usuarios']);
        },
        error: (error) => {
          this.isSubmitting = false;
          this.errorMessage = error.error?.message || 'Error al crear el usuario';
          console.error('Error creating usuario:', error);
        }
      });
    } else {
      this.markFormGroupTouched(this.usuarioForm);
    }
  }

  cancelar(): void {
    if (confirm('¿Desea cancelar la creación del usuario? Los datos no guardados se perderán.')) {
      this.router.navigate(['/dashboard/usuarios']);
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
    return this.usuarioForm.controls;
  }
}