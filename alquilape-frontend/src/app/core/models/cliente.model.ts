export interface Cliente {
  id: number;
  nombre: string;
  apellido: string;
  dniRuc: string;
  email: string;
  telefono: string;
  direccion: string;
  tipo: string;
  licenciaNumero: string;
  licenciaVencimiento: Date;
  activo: boolean;
}

export enum TipoCliente {
  PERSONA = 'PERSONA',
  EMPRESA = 'EMPRESA'
}