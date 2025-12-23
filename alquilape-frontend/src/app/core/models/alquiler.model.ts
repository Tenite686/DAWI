export interface Alquiler {
  id: number;
  clienteId: number;
  clienteNombre?: string;
  vehiculoId: number;
  vehiculoModelo?: string;
  fechaInicio: Date;
  fechaFinEstimada: Date;
  fechaDevolucion?: Date;
  kilometrajeInicio: number;
  kilometrajeFin?: number;
  precioTotal?: number;
  estado: string;
  observaciones?: string;
}

export enum EstadoAlquiler {
  ACTIVO = 'ACTIVO',
  FINALIZADO = 'FINALIZADO',
  CANCELADO = 'CANCELADO'
}