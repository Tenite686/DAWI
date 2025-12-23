import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  userRole: string = '';
  userName: string = '';
  
  menuItems: any[] = [];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUserValue;
    console.log("user: ", user)
    this.userRole = user?.role || '';
    this.userName = user?.username || '';
    
    this.initializeMenu();
  }

  initializeMenu(): void {
    const baseMenu = [
      { path: '/dashboard', icon: 'home', label: 'Inicio', roles: ['ADMIN', 'SUPERVISOR', 'ASISTENTE'] },
      { path: 'vehiculos', icon: 'directions_car', label: 'Vehículos', roles: ['ADMIN', 'SUPERVISOR', 'ASISTENTE'] }
    ];

    if (this.userRole === 'ADMIN') {
      this.menuItems = [
        ...baseMenu,
        { path: 'usuarios', icon: 'people', label: 'Usuarios', roles: ['ADMIN'] },
        { path: 'clientes', icon: 'person', label: 'Clientes', roles: ['ADMIN', 'SUPERVISOR'] },
        { path: 'alquileres', icon: 'receipt', label: 'Alquileres', roles: ['ADMIN', 'SUPERVISOR'] }
      ];
    } else if (this.userRole === 'SUPERVISOR') {
      this.menuItems = [
        ...baseMenu,
        { path: 'clientes', icon: 'person', label: 'Clientes', roles: ['ADMIN', 'SUPERVISOR'] },
        { path: 'alquileres', icon: 'receipt', label: 'Alquileres', roles: ['ADMIN', 'SUPERVISOR'] }
      ];
    } else if (this.userRole === 'ASISTENTE') {
      this.menuItems = baseMenu;
    }
  }

  hasPermission(item: any): boolean {
    return item.roles.includes(this.userRole);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
