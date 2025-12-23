export interface Vehiculo {
  id: number;
  marca: string;
  modelo: string;
  anio: number;
  placa: string;
  color: string;
  tipo: string;
  estado: string;
  precioPorDia: number;
  kilometraje: number;
  capacidadPasajeros: number;
  caracteristicasAdicionales?: Record<string, any>;
}

export enum TipoVehiculo {
  AUTO = 'AUTO',
  CAMIONETA = 'CAMIONETA',
  SUV = 'SUV',
  MOTO = 'MOTO'
}

export enum EstadoVehiculo {
  DISPONIBLE = 'DISPONIBLE',
  ALQUILADO = 'ALQUILADO',
  MANTENIMIENTO = 'MANTENIMIENTO',
  INACTIVO = 'INACTIVO'
}