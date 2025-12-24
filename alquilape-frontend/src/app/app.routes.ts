import { Routes } from '@angular/router';
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
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';
import { InicioComponent } from './modules/inicio/inicio.component';
export const routes: Routes = [
  { path: '', redirectTo: '/auth/login', pathMatch: 'full' },
  { path: 'auth/login', component: LoginComponent },
  
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        component: InicioComponent,
        canActivate: [AuthGuard]
      },
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