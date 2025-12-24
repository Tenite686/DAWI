export interface Alquiler {
  id: number;

  // --- NUEVO: Objetos completos (Soluciona el error rojo) ---
  cliente?: {
    id: number;
    nombreCompleto: string;
    dniRuc?: string;
    telefono?: string;
  };

  vehiculo?: {
    id: number;
    marca: string;
    modelo: string;
    placa: string;
    precioPorDia?: number;
  };

  usuario?: {
    id: number;
    nombreCompleto: string;
  };

  // Mantenemos estos por si usas filtros, pero son opcionales
  clienteId?: number;
  vehiculoId?: number;

  // En el JSON vienen, string funciona mejor con el pipe date
  fechaInicio: string; 
  fechaFinEstimada: string;
  fechaDevolucionReal?: string; 
  
  kilometrajeInicio: number;
  kilometrajeFin?: number;
  precioTotal?: number;
  estado: string;
  observaciones?: string;
}

export enum EstadoAlquiler {
  ACTIVO = 'ACTIVO',
  FINALIZADO = 'FINALIZADO',
  CANCELADO = 'CANCELADO',
  COMPLETADO = 'COMPLETADO'
}