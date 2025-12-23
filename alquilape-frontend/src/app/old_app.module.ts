/*import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';

import { AppComponent } from './app.component';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';

// Importar componentes
import { LoginComponent } from './modules/auth/components/login/login.component';
import { DashboardComponent } from './modules/dashboard/dashboard.component';
import { UsuarioListComponent } from './modules/usuarios/components/usuario-list/usuario-list.component';
import { VehiculoListComponent } from './modules/vehiculos/components/vehiculo-list/vehiculo-list.component';
import { ClienteListComponent } from './modules/clientes/components/cliente-list/cliente-list.component';
import { AlquilerListComponent } from './modules/alquileres/components/alquiler-list/alquiler-list.component';
import { CreateUsuarioComponent } from './modules/usuarios/components/create-usuario/create-usuario.component';
import { CreateVehiculoComponent } from './modules/vehiculos/components/create-vehiculo/create-vehiculo.component';
import { CreateClienteComponent } from './modules/clientes/components/create-cliente/create-cliente.component';
import { CreateAlquilerComponent } from './modules/alquileres/components/create-alquiler/create-alquiler.component';
import { AuthModule } from './modules/auth/auth.module';

const routes: Routes = [
  { path: '', redirectTo: '/auth/login', pathMatch: 'full' },
  { path: 'auth/login', component: LoginComponent },
  
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'usuarios',
        component: UsuarioListComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN'] }
      },
      {
        path: 'usuarios/nuevo',
        component: CreateUsuarioComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN'] }
      },
      {
        path: 'vehiculos',
        component: VehiculoListComponent,
        canActivate: [AuthGuard]
      },
      {
        path: 'vehiculos/nuevo',
        component: CreateVehiculoComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN'] }
      },
      {
        path: 'clientes',
        component: ClienteListComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SUPERVISOR'] }
      },
      {
        path: 'clientes/nuevo',
        component: CreateClienteComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SUPERVISOR'] }
      },
      {
        path: 'alquileres',
        component: AlquilerListComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SUPERVISOR'] }
      },
      {
        path: 'alquileres/nuevo',
        component: CreateAlquilerComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'SUPERVISOR'] }
      }
    ]
  },
  { path: '**', redirectTo: '/auth/login' }
];

@NgModule({
  declarations: [],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(routes),
    AuthModule,

    // Standalone Components
    AppComponent,
    LoginComponent,
    DashboardComponent,
    UsuarioListComponent,
    VehiculoListComponent,
    ClienteListComponent,
    AlquilerListComponent,
    CreateUsuarioComponent,
    CreateVehiculoComponent,
    CreateClienteComponent,
    CreateAlquilerComponent,
    
    // Material Modules
    MatPaginatorModule,
    MatTableModule,
    MatButtonModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDialogModule,
    MatIconModule,
    MatCardModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatMenuModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    AuthGuard,
    RoleGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }*/