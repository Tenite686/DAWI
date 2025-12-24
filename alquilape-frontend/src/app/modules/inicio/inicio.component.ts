import { Component, OnInit } from '@angular/core';
import { ClienteService } from '../../core/services/cliente.service';
import { VehiculoService } from '../../core/services/vehiculo.service';
import { AlquilerService } from '../../core/services/alquiler.service';

@Component({
  selector: 'app-inicio',
  templateUrl: './inicio.component.html',
  styleUrls: ['./inicio.component.css']
})
export class InicioComponent implements OnInit {
  totalClientes: number = 0;
  totalVehiculos: number = 0;
  totalReservas: number = 0;

  constructor(
    private clienteService: ClienteService,
    private vehiculoService: VehiculoService,
    private alquilerService: AlquilerService
  ) {}

  ngOnInit(): void {
  this.clienteService.getClientes().subscribe(clientes => {
    console.log('Clientes:', clientes); // <-- ¿Ves datos aquí?
    this.totalClientes = clientes.totalElements;
  });

  this.vehiculoService.getVehiculos().subscribe(vehiculos => {
    console.log('Vehículos:', vehiculos); // <-- ¿Ves datos aquí?
    this.totalVehiculos = vehiculos.totalElements;
  });

  this.alquilerService.getAlquileres().subscribe(alquileres => {
    console.log('Alquileres:', alquileres); // <-- ¿Ves datos aquí?
    this.totalReservas = alquileres.totalElements;
  });
}
}